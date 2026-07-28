package com.tokyohackgroup.tokyohackgroup_portal.presentation.controller;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tokyohackgroup.tokyohackgroup_portal.application.service.CalendarService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.ProjectService;
import com.tokyohackgroup.tokyohackgroup_portal.application.service.UserService;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEventType;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.dto.CalendarEventDto;

/**
 * カレンダー画面の表示、イベントの作成・更新・日程変更・削除を制御するコントローラー。
 */
@Controller
@RequestMapping("/calendar")
public class CalendarController {

    private static final String VIEW_CALENDAR = "calendar/view";
    private static final String REDIRECT_CALENDAR = "redirect:/calendar";

    private final CalendarService calendarService;
    private final ProjectService projectService;
    private final UserService userService;

    public CalendarController(CalendarService calendarService, ProjectService projectService, UserService userService) {
        this.calendarService = calendarService;
        this.projectService = projectService;
        this.userService = userService;
    }

    /**
     * カレンダー画面のシェルを表示する。イベント自体は画面側からJSON APIを非同期取得する。
     */
    @GetMapping
    public String showCalendar(
            @RequestParam(name = "projectId", required = false) Long projectId,
            HttpSession session,
            Model model) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);

        model.addAttribute("pageTitle", "カレンダー");
        model.addAttribute("activeNav", "calendar");
        model.addAttribute("projectIdFilter", projectId);
        model.addAttribute("eventTypeList", CalendarEventType.values());
        model.addAttribute("memberList", userService.fetchAllActiveUsers());
        model.addAttribute("myProjectList", projectService.findProjectsForUser(loginUser));
        return VIEW_CALENDAR;
    }

    /**
     * 指定期間のイベントをJSON配列で返す。カレンダー画面のJSから呼び出される。
     */
    @GetMapping("/events")
    @ResponseBody
    public List<CalendarEventDto> fetchEvents(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam(name = "projectId", required = false) Long projectId,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        return calendarService.fetchEventsForRange(loginUser, LocalDateTime.parse(from), LocalDateTime.parse(to), projectId);
    }

    @PostMapping("/events")
    public String processCreateEvent(
            @RequestParam(name = "projectId", required = false) Long projectId,
            @RequestParam("title") String title,
            @RequestParam("eventType") CalendarEventType eventType,
            @RequestParam("start") String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "isAllDay", required = false, defaultValue = "false") boolean isAllDay,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "location", required = false) String location,
            @RequestParam(name = "participantUserIds", required = false) List<Long> participantUserIds,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = (end != null && !end.isBlank()) ? LocalDateTime.parse(end) : startDateTime.plusHours(1);

        calendarService.createEvent(projectId, title, eventType, startDateTime, endDateTime, isAllDay, description, location, participantUserIds, loginUser);

        return REDIRECT_CALENDAR + (projectId != null ? "?projectId=" + projectId : "");
    }

    @PostMapping("/events/{id}")
    public String processUpdateEvent(
            @PathVariable("id") Long eventId,
            @RequestParam(name = "projectId", required = false) Long projectId,
            @RequestParam("title") String title,
            @RequestParam("eventType") CalendarEventType eventType,
            @RequestParam("start") String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "isAllDay", required = false, defaultValue = "false") boolean isAllDay,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "location", required = false) String location,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        LocalDateTime startDateTime = LocalDateTime.parse(start);
        LocalDateTime endDateTime = (end != null && !end.isBlank()) ? LocalDateTime.parse(end) : startDateTime.plusHours(1);

        calendarService.updateEvent(eventId, title, eventType, startDateTime, endDateTime, isAllDay, description, location, loginUser);

        return REDIRECT_CALENDAR + (projectId != null ? "?projectId=" + projectId : "");
    }

    /**
     * ドラッグ＆ドロップによる日時変更をAjaxで受け付ける。
     */
    @PostMapping("/events/{id}/reschedule")
    @ResponseBody
    public void processReschedule(
            @PathVariable("id") Long eventId,
            @RequestParam("newStart") String newStart,
            @RequestParam("newEnd") String newEnd,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        calendarService.reschedule(eventId, LocalDateTime.parse(newStart), LocalDateTime.parse(newEnd), loginUser);
    }

    @PostMapping("/events/{id}/delete")
    public String processDeleteEvent(
            @PathVariable("id") Long eventId,
            @RequestParam(name = "projectId", required = false) Long projectId,
            HttpSession session) {

        UserAccount loginUser = (UserAccount) session.getAttribute(LoginController.SESSION_KEY_LOGIN_USER);
        calendarService.deleteEvent(eventId, loginUser);

        return REDIRECT_CALENDAR + (projectId != null ? "?projectId=" + projectId : "");
    }
}
