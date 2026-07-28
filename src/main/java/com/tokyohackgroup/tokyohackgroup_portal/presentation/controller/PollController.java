package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.SchedulingPollService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollAnswer;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.SchedulingPoll;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.dto.PollResponseDto;

/**
 * 日程調整（投票）の作成・一覧表示・投票・確定を制御するコントローラー。
 */
@Controller
@RequestMapping("/polls")
public class PollController {

    private static final String VIEW_POLL_LIST = "poll/list";
    private static final String VIEW_POLL_DETAIL = "poll/detail";
    private static final String REDIRECT_POLL_LIST = "redirect:/polls";

    private final SchedulingPollService schedulingPollService;
    private final ProjectService projectService;
    private final UserService userService;

    public PollController(SchedulingPollService schedulingPollService, ProjectService projectService, UserService userService) {
        this.schedulingPollService = schedulingPollService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @GetMapping
    public String showPollList(HttpSession session, Model model) {
        UserAccount loginUser = getLoginUser(session);

        model.addAttribute("pageTitle", "日程調整");
        model.addAttribute("activeNav", "polls");
        model.addAttribute("pollList", schedulingPollService.fetchPollsForUser(loginUser));
        model.addAttribute("myProjectList", projectService.findProjectsForUser(loginUser));
        model.addAttribute("memberList", userService.fetchAllActiveUsers());
        return VIEW_POLL_LIST;
    }

    @PostMapping
    public String processCreatePoll(
            @RequestParam(name = "projectId", required = false) Long projectId,
            @RequestParam("title") String title,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "responseDeadline", required = false) String responseDeadline,
            @RequestParam("candidateDateTimes") List<String> candidateDateTimes,
            @RequestParam(name = "inviteeUserIds", required = false) List<Long> inviteeUserIds,
            HttpSession session) {

        UserAccount loginUser = getLoginUser(session);
        LocalDateTime deadline = (responseDeadline != null && !responseDeadline.isBlank()) ? LocalDateTime.parse(responseDeadline) : null;
        List<LocalDateTime> candidates = candidateDateTimes.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(LocalDateTime::parse)
                .toList();

        SchedulingPoll newPoll = schedulingPollService.createPoll(projectId, title, description, loginUser, deadline, candidates, inviteeUserIds);
        return "redirect:/polls/" + newPoll.getId();
    }

    @GetMapping("/{id}")
    public String showPollDetail(@PathVariable("id") Long pollId, HttpSession session, Model model) {
        UserAccount loginUser = getLoginUser(session);
        Optional<SchedulingPoll> pollOptional = schedulingPollService.findById(pollId);

        if (pollOptional.isEmpty()) {
            return REDIRECT_POLL_LIST;
        }

        SchedulingPoll poll = pollOptional.get();
        if (!poll.isParticipant(loginUser) && !loginUser.isAdmin()) {
            return REDIRECT_POLL_LIST;
        }

        model.addAttribute("pageTitle", poll.getTitle());
        model.addAttribute("activeNav", "polls");
        model.addAttribute("pollTarget", poll);
        model.addAttribute("responseMatrix", schedulingPollService.buildResponseMatrix(poll));
        model.addAttribute("answerList", PollAnswer.values());
        model.addAttribute("isOrganizer", poll.isOrganizer(loginUser) || loginUser.isAdmin());
        return VIEW_POLL_DETAIL;
    }

    /**
     * 自分の回答を登録・更新する（Ajax送信）。
     */
    @PostMapping("/{id}/vote")
    @ResponseBody
    public void processSubmitVote(
            @PathVariable("id") Long pollId,
            @RequestParam("candidateId") Long candidateId,
            @RequestParam("answer") PollAnswer answer,
            @RequestParam(name = "comment", required = false) String comment,
            HttpSession session) {

        UserAccount loginUser = getLoginUser(session);
        schedulingPollService.submitVote(pollId, candidateId, loginUser, answer, comment);
    }

    /**
     * 他メンバーの回答状況をポーリング取得するためのJSON API。
     */
    @GetMapping("/{id}/status")
    @ResponseBody
    public List<PollResponseDto> fetchPollStatus(@PathVariable("id") Long pollId) {
        return schedulingPollService.fetchResponseDtos(pollId);
    }

    @PostMapping("/{id}/confirm")
    public String processConfirmPoll(
            @PathVariable("id") Long pollId,
            @RequestParam("candidateId") Long candidateId,
            HttpSession session) {

        UserAccount loginUser = getLoginUser(session);
        schedulingPollService.confirmPoll(pollId, candidateId, loginUser);
        return "redirect:/polls/" + pollId;
    }

    private UserAccount getLoginUser(HttpSession session) {
        return (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
    }
}
