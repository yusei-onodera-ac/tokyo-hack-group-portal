package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEvent;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEventType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.CalendarEventRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ProjectRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.dto.CalendarEventDto;

/**
 * カレンダーイベントの検索・作成・更新・日程変更・削除を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final ProjectRepository projectRepository;
    private final UserAccountRepository userAccountRepository;

    public CalendarService(CalendarEventRepository calendarEventRepository, ProjectRepository projectRepository, UserAccountRepository userAccountRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.projectRepository = projectRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * 指定期間のイベントを、閲覧権限に応じてDTOへ詰め替えて取得する。
     */
    public List<CalendarEventDto> fetchEventsForRange(UserAccount currentUser, LocalDateTime from, LocalDateTime to, Long projectIdFilter) {
        List<Long> myProjectIds = projectRepository.findProjectIdsForMember(currentUser);
        List<CalendarEvent> events = calendarEventRepository.search(from, to, projectIdFilter, myProjectIds, currentUser);

        return events.stream().map(event -> toDto(event, currentUser)).toList();
    }

    private CalendarEventDto toDto(CalendarEvent event, UserAccount currentUser) {
        Project project = event.getProject();
        return new CalendarEventDto(
                event.getId(),
                event.getTitle(),
                event.getEventType().name(),
                event.getEventType().getDisplayLabel(),
                event.getStartDateTime().toString(),
                event.getEndDateTime().toString(),
                event.isAllDay(),
                event.getDescription(),
                event.getLocation(),
                project != null ? project.getId() : null,
                project != null ? project.getTitle() : null,
                event.getCreatedBy().getDisplayName(),
                event.isEditableBy(currentUser));
    }

    /**
     * 新規イベントを作成する。
     */
    @Transactional
    public CalendarEvent createEvent(Long projectId, String title, CalendarEventType eventType, LocalDateTime start, LocalDateTime end,
            boolean isAllDay, String description, String location, List<Long> participantUserIds, UserAccount creator) {

        Project project = (projectId != null)
                ? projectRepository.findById(projectId).orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId))
                : null;

        CalendarEvent newEvent = new CalendarEvent(project, title, eventType, start, end, isAllDay, description, location, creator);
        newEvent.addParticipant(creator);

        if (participantUserIds != null) {
            for (Long participantUserId : participantUserIds) {
                userAccountRepository.findById(participantUserId).ifPresent(newEvent::addParticipant);
            }
        }

        return calendarEventRepository.save(newEvent);
    }

    /**
     * イベント内容を更新する。作成者/プロジェクトメンバー/管理者のみ許可する。
     */
    @Transactional
    public void updateEvent(Long eventId, String title, CalendarEventType eventType, LocalDateTime start, LocalDateTime end,
            boolean isAllDay, String description, String location, UserAccount actingUser) {

        CalendarEvent targetEvent = findEventOrThrow(eventId);
        requireEditable(targetEvent, actingUser);

        targetEvent.update(title, eventType, start, end, isAllDay, description, location);
        calendarEventRepository.save(targetEvent);
    }

    /**
     * ドラッグ＆ドロップによる日時変更を保存する。
     */
    @Transactional
    public void reschedule(Long eventId, LocalDateTime newStart, LocalDateTime newEnd, UserAccount actingUser) {
        CalendarEvent targetEvent = findEventOrThrow(eventId);
        requireEditable(targetEvent, actingUser);

        targetEvent.reschedule(newStart, newEnd);
        calendarEventRepository.save(targetEvent);
    }

    @Transactional
    public void deleteEvent(Long eventId, UserAccount actingUser) {
        CalendarEvent targetEvent = findEventOrThrow(eventId);
        requireEditable(targetEvent, actingUser);

        calendarEventRepository.delete(targetEvent);
    }

    /**
     * 日程調整の確定時に、確定した日時でプロジェクト/個人カレンダーへイベントを自動登録する。
     */
    @Transactional
    public CalendarEvent createConfirmedMeeting(Project project, String title, LocalDateTime confirmedDateTime, UserAccount organizer, List<UserAccount> participants) {
        CalendarEvent newEvent = new CalendarEvent(project, title, CalendarEventType.MEETING, confirmedDateTime,
                confirmedDateTime.plusHours(1), false, "日程調整により確定した予定です。", null, organizer);

        newEvent.addParticipant(organizer);
        participants.forEach(newEvent::addParticipant);

        return calendarEventRepository.save(newEvent);
    }

    private CalendarEvent findEventOrThrow(Long eventId) {
        Optional<CalendarEvent> eventOptional = calendarEventRepository.findById(eventId);
        return eventOptional.orElseThrow(() -> new IllegalArgumentException("指定されたイベントが見つかりません。ID: " + eventId));
    }

    private void requireEditable(CalendarEvent event, UserAccount actingUser) {
        if (!event.isEditableBy(actingUser)) {
            throw new IllegalStateException("このイベントを編集する権限がありません。");
        }
    }
}
