package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEvent;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollAnswer;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollCandidate;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollResponse;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.SchedulingPoll;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.PollResponseRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ProjectRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.SchedulingPollRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.dto.PollResponseDto;

/**
 * 日程調整（投票）の作成・回答受付・確定処理を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class SchedulingPollService {

    private final SchedulingPollRepository schedulingPollRepository;
    private final PollResponseRepository pollResponseRepository;
    private final ProjectRepository projectRepository;
    private final UserAccountRepository userAccountRepository;
    private final CalendarService calendarService;
    private final EmailNotificationService emailNotificationService;

    public SchedulingPollService(
            SchedulingPollRepository schedulingPollRepository,
            PollResponseRepository pollResponseRepository,
            ProjectRepository projectRepository,
            UserAccountRepository userAccountRepository,
            CalendarService calendarService,
            EmailNotificationService emailNotificationService) {
        this.schedulingPollRepository = schedulingPollRepository;
        this.pollResponseRepository = pollResponseRepository;
        this.projectRepository = projectRepository;
        this.userAccountRepository = userAccountRepository;
        this.calendarService = calendarService;
        this.emailNotificationService = emailNotificationService;
    }

    public List<SchedulingPoll> fetchPollsForUser(UserAccount user) {
        List<SchedulingPoll> polls = schedulingPollRepository.findByOrganizerOrInvitee(user);
        polls.forEach(this::initializeLazyAssociations);
        return polls;
    }

    public Optional<SchedulingPoll> findById(Long pollId) {
        Optional<SchedulingPoll> pollOptional = schedulingPollRepository.findById(pollId);
        pollOptional.ifPresent(this::initializeLazyAssociations);
        return pollOptional;
    }

    /**
     * 候補×招待者の回答マトリクス（candidateId -> userId -> PollResponse）を構築する。
     */
    public Map<Long, Map<Long, PollResponse>> buildResponseMatrix(SchedulingPoll poll) {
        List<PollResponse> responses = pollResponseRepository.findByPoll(poll);
        Map<Long, Map<Long, PollResponse>> matrix = new HashMap<>();

        for (PollResponse response : responses) {
            matrix.computeIfAbsent(response.getCandidate().getId(), key -> new HashMap<>())
                    .put(response.getUser().getId(), response);
        }
        return matrix;
    }

    /**
     * ポーリング更新用に、回答一覧をDTOへ詰め替えて返す。
     */
    public List<PollResponseDto> fetchResponseDtos(Long pollId) {
        SchedulingPoll poll = schedulingPollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("指定された日程調整が見つかりません。ID: " + pollId));

        return pollResponseRepository.findByPoll(poll).stream()
                .map(response -> new PollResponseDto(
                        response.getCandidate().getId(),
                        response.getUser().getId(),
                        response.getAnswer().name(),
                        response.getAnswer().getSymbol(),
                        response.getComment()))
                .toList();
    }

    @Transactional
    public SchedulingPoll createPoll(Long projectId, String title, String description, UserAccount organizer,
            LocalDateTime responseDeadline, List<LocalDateTime> candidateDateTimes, List<Long> inviteeUserIds) {

        Project project = (projectId != null)
                ? projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId))
                : null;

        SchedulingPoll newPoll = new SchedulingPoll(project, title, description, organizer, responseDeadline);

        int order = 0;
        for (LocalDateTime candidateDateTime : candidateDateTimes) {
            newPoll.addCandidate(candidateDateTime, order++);
        }

        if (inviteeUserIds != null) {
            for (Long inviteeUserId : inviteeUserIds) {
                userAccountRepository.findById(inviteeUserId).ifPresent(newPoll::addInvitee);
            }
        }

        return schedulingPollRepository.save(newPoll);
    }

    /**
     * 自分の回答を登録・更新する（upsert）。
     */
    @Transactional
    public void submitVote(Long pollId, Long candidateId, UserAccount user, PollAnswer answer, String comment) {
        SchedulingPoll poll = schedulingPollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("指定された日程調整が見つかりません。ID: " + pollId));

        if (!poll.isParticipant(user)) {
            throw new IllegalStateException("この日程調整に回答する権限がありません。");
        }

        PollCandidate candidate = poll.getCandidates().stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("指定された候補日時が見つかりません。ID: " + candidateId));

        Optional<PollResponse> existingResponse = pollResponseRepository.findByCandidateAndUser(candidate, user);
        if (existingResponse.isPresent()) {
            existingResponse.get().changeAnswer(answer, comment);
            pollResponseRepository.save(existingResponse.get());
        } else {
            pollResponseRepository.save(new PollResponse(candidate, user, answer, comment));
        }
    }

    /**
     * 候補日時を確定し、確定通知メールの送信とカレンダーへのイベント自動登録を行う。
     */
    @Transactional
    public void confirmPoll(Long pollId, Long candidateId, UserAccount actingUser) {
        SchedulingPoll poll = schedulingPollRepository.findById(pollId)
                .orElseThrow(() -> new IllegalArgumentException("指定された日程調整が見つかりません。ID: " + pollId));

        if (!poll.isOrganizer(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("この日程調整を確定する権限がありません。");
        }

        PollCandidate candidate = poll.getCandidates().stream()
                .filter(c -> c.getId().equals(candidateId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("指定された候補日時が見つかりません。ID: " + candidateId));

        List<UserAccount> inviteeList = poll.getInvitees().stream().toList();

        CalendarEvent confirmedEvent = calendarService.createConfirmedMeeting(
                poll.getProject(), poll.getTitle(), candidate.getCandidateDateTime(), poll.getOrganizer(), inviteeList);

        poll.confirm(candidate, confirmedEvent);
        schedulingPollRepository.save(poll);

        for (UserAccount invitee : inviteeList) {
            emailNotificationService.sendPollConfirmedEmail(invitee.getEmailAddress(), invitee.getDisplayName(), poll.getTitle(), candidate.getCandidateDateTime());
        }
    }

    /**
     * open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内で主催者・招待者・候補日時・確定候補の遅延ロードプロキシを初期化しておく。
     */
    private void initializeLazyAssociations(SchedulingPoll poll) {
        poll.getOrganizer().getDisplayName();
        poll.getInvitees().size();
        poll.getCandidates().size();
        if (poll.getProject() != null) {
            poll.getProject().getTitle();
        }
        if (poll.getConfirmedCandidate() != null) {
            poll.getConfirmedCandidate().getCandidateDateTime();
        }
    }
}
