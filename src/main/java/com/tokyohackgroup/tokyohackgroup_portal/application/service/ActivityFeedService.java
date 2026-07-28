package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.Notice;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollStatus;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.SchedulingPoll;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.presentation.dto.ActivityItemDto;

/**
 * ダッシュボードに表示する「最近の活動」フィードを、既存のお知らせ・プロジェクト・日程調整データから
 * 都度集計して生成するアプリケーションサービス。専用のテーブルは持たない。
 */
@Service
public class ActivityFeedService {

    /** 各カテゴリから取り込む最大件数（集約前） */
    private static final int SOURCE_LIMIT = 10;

    private final NoticeService noticeService;
    private final ProjectService projectService;
    private final SchedulingPollService schedulingPollService;

    public ActivityFeedService(NoticeService noticeService, ProjectService projectService, SchedulingPollService schedulingPollService) {
        this.noticeService = noticeService;
        this.projectService = projectService;
        this.schedulingPollService = schedulingPollService;
    }

    public List<ActivityItemDto> fetchRecentActivity(UserAccount user, int limit) {
        List<ActivityItemDto> items = new ArrayList<>();

        noticeService.fetchAllNotices().stream()
                .limit(SOURCE_LIMIT)
                .forEach(notice -> items.add(toNoticeItem(notice)));

        projectService.searchProjects(null, null, "updatedAt", user, 0).getContent().stream()
                .limit(SOURCE_LIMIT)
                .forEach(project -> items.add(toProjectItem(project)));

        schedulingPollService.fetchPollsForUser(user).stream()
                .limit(SOURCE_LIMIT)
                .forEach(poll -> items.add(toPollItem(poll)));

        return items.stream()
                .sorted(Comparator.comparing(ActivityItemDto::occurredAt).reversed())
                .limit(limit)
                .toList();
    }

    private ActivityItemDto toNoticeItem(Notice notice) {
        return new ActivityItemDto("📢", "お知らせ「" + notice.getTitle() + "」が投稿されました", "/notices", notice.getCreatedAt());
    }

    private ActivityItemDto toProjectItem(Project project) {
        return new ActivityItemDto("📁", "プロジェクト「" + project.getTitle() + "」が更新されました",
                "/projects/" + project.getId(), project.getUpdatedAt());
    }

    private ActivityItemDto toPollItem(SchedulingPoll poll) {
        if (poll.getStatus() == PollStatus.CLOSED) {
            return new ActivityItemDto("🗳️", "日程調整「" + poll.getTitle() + "」の日程が確定しました",
                    "/polls/" + poll.getId(), poll.getCreatedAt());
        }
        return new ActivityItemDto("🗳️", "日程調整「" + poll.getTitle() + "」が開始されました",
                "/polls/" + poll.getId(), poll.getCreatedAt());
    }
}
