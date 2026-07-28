package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserRole;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEventType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.document.DocumentCategory;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollAnswer;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.task.Task;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.CalendarEventRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.CommentRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.DocumentRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ProjectRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.SchedulingPollRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.TaskRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * プロジェクト削除時のカスケード削除（タスク・カレンダー・日程調整・ドキュメント・コメント）を検証する結合テスト。
 */
@SpringBootTest
@Transactional
class ProjectDeletionCascadeTest {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private CalendarService calendarService;
    @Autowired
    private SchedulingPollService schedulingPollService;

    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private CalendarEventRepository calendarEventRepository;
    @Autowired
    private SchedulingPollRepository schedulingPollRepository;

    @Test
    void deleteProject_removesAllRelatedData() {
        UserAccount owner = userAccountRepository.save(
                new UserAccount("cascade-owner@example.com", "hash", "オーナー", UserRole.GENERAL_USER));
        UserAccount invitee = userAccountRepository.save(
                new UserAccount("cascade-invitee@example.com", "hash", "招待者", UserRole.GENERAL_USER));

        Project project = projectService.createProject("削除検証プロジェクト", "説明", true, owner, null);
        Long projectId = project.getId();

        Task task = taskService.createTask(project, "タスク", "説明", null, LocalDate.now().plusDays(1), owner);

        documentService.createTextDocument(project, "ドキュメント", "説明", DocumentCategory.OTHER, "本文", owner);

        commentService.postProjectComment(project, owner, "プロジェクトへのコメント");

        calendarService.createEvent(projectId, "会議", CalendarEventType.MEETING,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(1),
                false, "説明", null, List.of(), owner);

        var poll = schedulingPollService.createPoll(projectId, "日程調整", "説明", owner,
                LocalDateTime.now().plusDays(3), List.of(LocalDateTime.now().plusDays(5)), List.of(invitee.getId()));
        Long candidateId = poll.getCandidates().get(0).getId();
        schedulingPollService.submitVote(poll.getId(), candidateId, invitee, PollAnswer.AVAILABLE, "");
        schedulingPollService.confirmPoll(poll.getId(), candidateId, owner);

        assertThat(taskRepository.findByProjectOrderByStatusAscDueDateAsc(project)).hasSize(1);
        assertThat(documentRepository.findByProjectOrderByUpdatedAtDesc(project)).hasSize(1);
        assertThat(commentRepository.findByProjectOrderByCreatedAtAsc(project)).hasSize(1);
        assertThat(calendarEventRepository.findByProject(project)).hasSizeGreaterThanOrEqualTo(2);
        assertThat(schedulingPollRepository.findByProject(project)).hasSize(1);

        projectService.deleteProject(projectId, owner);

        assertThat(projectRepository.findById(projectId)).isEmpty();
        assertThat(taskRepository.findById(task.getId())).isEmpty();
        assertThat(documentRepository.findByProjectOrderByUpdatedAtDesc(project)).isEmpty();
        assertThat(commentRepository.findByProjectOrderByCreatedAtAsc(project)).isEmpty();
        assertThat(calendarEventRepository.findByProject(project)).isEmpty();
        assertThat(schedulingPollRepository.findByProject(project)).isEmpty();
    }
}
