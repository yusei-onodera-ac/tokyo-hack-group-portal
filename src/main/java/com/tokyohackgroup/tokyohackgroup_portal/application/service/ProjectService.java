package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.notification.NotificationType;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectMember;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectMemberRole;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectStatus;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.CommentRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ProjectRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * プロジェクトの検索・作成・編集・削除・ステータス変更・メンバー管理・お気に入り管理を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class ProjectService {

    /** 一覧画面での1ページあたりの表示件数 */
    public static final int PAGE_SIZE = 12;

    private final ProjectRepository projectRepository;
    private final UserAccountRepository userAccountRepository;
    private final ImageStorageService imageStorageService;
    private final CommentRepository commentRepository;
    private final SchedulingPollService schedulingPollService;
    private final TaskService taskService;
    private final DocumentService documentService;
    private final CalendarService calendarService;
    private final NotificationService notificationService;

    public ProjectService(
            ProjectRepository projectRepository,
            UserAccountRepository userAccountRepository,
            ImageStorageService imageStorageService,
            CommentRepository commentRepository,
            SchedulingPollService schedulingPollService,
            TaskService taskService,
            DocumentService documentService,
            CalendarService calendarService,
            NotificationService notificationService) {
        this.projectRepository = projectRepository;
        this.userAccountRepository = userAccountRepository;
        this.imageStorageService = imageStorageService;
        this.commentRepository = commentRepository;
        this.schedulingPollService = schedulingPollService;
        this.taskService = taskService;
        this.documentService = documentService;
        this.calendarService = calendarService;
        this.notificationService = notificationService;
    }

    /**
     * キーワード・ステータス・並び替えキーで絞り込んだプロジェクトをページング取得する。
     *
     * <p>open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内でメンバー・お気に入りコレクションを初期化してから返却する。</p>
     */
    public Page<Project> searchProjects(String keyword, ProjectStatus status, String sortKey, UserAccount currentUser, int pageNumber) {
        String normalizedKeyword = (keyword == null || keyword.isBlank())
                ? null
                : "%" + keyword.trim().toLowerCase(Locale.JAPAN) + "%";

        Pageable pageable = PageRequest.of(Math.max(pageNumber, 0), PAGE_SIZE, resolveSort(sortKey));

        Page<Project> resultPage = projectRepository.search(normalizedKeyword, status, currentUser, pageable);
        resultPage.forEach(this::initializeLazyAssociations);
        return resultPage;
    }

    private Sort resolveSort(String sortKey) {
        String targetProperty = switch (sortKey == null ? "" : sortKey) {
            case "memberCount" -> "memberCount";
            case "updatedAt" -> "updatedAt";
            default -> "createdAt";
        };
        return Sort.by(Sort.Direction.DESC, targetProperty);
    }

    /**
     * 指定ユーザーが参加しているプロジェクトの一覧を取得する（カレンダー・日程調整のプロジェクト選択に使用）。
     */
    public List<Project> findProjectsForUser(UserAccount user) {
        List<Long> projectIds = projectRepository.findProjectIdsForMember(user);
        return projectRepository.findAllById(projectIds);
    }

    /**
     * お知らせ作成・編集画面の「関連プロジェクト」選択肢用に、全プロジェクトを取得する。
     */
    public List<Project> findAllProjectsForSelection() {
        return projectRepository.findAllByOrderByTitleAsc();
    }

    public Optional<Project> findById(Long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        projectOptional.ifPresent(this::initializeLazyAssociations);
        return projectOptional;
    }

    /**
     * open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内でメンバー・担当者・お気に入りの遅延ロードプロキシを初期化しておく。
     */
    private void initializeLazyAssociations(Project project) {
        project.getCreatedBy().getDisplayName();
        project.getMembers().forEach(member -> member.getUser().getDisplayName());
        project.getFavoritedByUsers().size();
    }

    /**
     * 新規プロジェクトを作成する。作成者は自動的に OWNER として参加する。
     *
     * @param assigneeUserIds 担当者として MEMBER 権限で追加するユーザーIDの一覧（任意）
     */
    @Transactional
    public Project createProject(String title, String description, boolean isPublic, UserAccount creator, List<Long> assigneeUserIds) {
        Project newProject = new Project(title, description, isPublic, creator);
        newProject.addMember(creator, ProjectMemberRole.OWNER);

        if (assigneeUserIds != null) {
            for (Long assigneeUserId : assigneeUserIds) {
                if (assigneeUserId.equals(creator.getId())) {
                    continue;
                }
                userAccountRepository.findById(assigneeUserId)
                        .ifPresent(assignee -> newProject.addMember(assignee, ProjectMemberRole.MEMBER));
            }
        }

        return projectRepository.save(newProject);
    }

    /**
     * プロジェクトのステータスを変更する。OWNER または管理者のみ実行可能。
     */
    @Transactional
    public void changeStatus(Long projectId, ProjectStatus newStatus, UserAccount actingUser) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isOwner(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("ステータスを変更する権限がありません。");
        }

        targetProject.changeStatus(newStatus);
        projectRepository.save(targetProject);
    }

    /**
     * プロジェクトのタイトル・概要を変更する。OWNER または管理者のみ実行可能。
     */
    @Transactional
    public void updateDetails(Long projectId, String newTitle, String newDescription, UserAccount actingUser) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isOwner(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("このプロジェクトを編集する権限がありません。");
        }

        targetProject.updateDetails(newTitle, newDescription);
        projectRepository.save(targetProject);
    }

    /**
     * メンバーを追加する。OWNER または管理者のみ実行可能。
     */
    @Transactional
    public void addMember(Long projectId, Long userId, UserAccount actingUser) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isOwner(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("メンバーを追加する権限がありません。");
        }

        UserAccount newMember = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたユーザーが見つかりません。ID: " + userId));

        targetProject.addMember(newMember, ProjectMemberRole.MEMBER);
        projectRepository.save(targetProject);
    }

    /**
     * メンバーを除外する。OWNER または管理者のみ実行可能。唯一のOWNERは除外できない。
     */
    @Transactional
    public void removeMember(Long projectId, Long userId, UserAccount actingUser) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isOwner(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("メンバーを除外する権限がありません。");
        }

        targetProject.removeMember(userId);
        projectRepository.save(targetProject);
    }

    /**
     * プロジェクトを削除する。OWNER または管理者のみ実行可能。
     *
     * <p>タスク・カレンダーイベント・日程調整・ドキュメント（ディスク上のファイル含む）・コメント・
     * メンバー・お気に入りなど、関連する全データを連動して削除する。</p>
     */
    @Transactional
    public void deleteProject(Long projectId, UserAccount actingUser) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isOwner(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("このプロジェクトを削除する権限がありません。");
        }

        // コメント（プロジェクト直付け分 + ドキュメント付け分）を先に削除する
        commentRepository.deleteAll(commentRepository.findByProjectOrderByCreatedAtAsc(targetProject));
        for (var document : documentService.findByProject(targetProject)) {
            commentRepository.deleteAll(commentRepository.findByDocumentOrderByCreatedAtAsc(document));
        }

        // タスク（連動するカレンダーイベントも一緒に削除される）
        for (var task : taskService.findByProject(targetProject)) {
            taskService.deleteTask(task.getId(), actingUser);
        }

        // 日程調整（候補日時・回答・招待も連動して削除される）
        schedulingPollService.deletePollsForProject(targetProject);

        // 残りのカレンダーイベント（会議・マイルストーン等）
        calendarService.deleteEventsForProject(targetProject);

        // ドキュメント（バージョン情報・ディスク上のファイル実体も削除される）
        documentService.deleteDocumentsForProject(targetProject);

        // プロジェクト本体（メンバー・お気に入りは自動的にカスケード削除される）
        projectRepository.delete(targetProject);
    }

    /**
     * プロジェクトへの参加を申請する。公開プロジェクトかつ非メンバーのみ実行可能。
     * 申請するとプロジェクトの全 OWNER に通知が届き、OWNER が編集画面からメンバー追加する運用となる。
     */
    @Transactional
    public void requestToJoin(Long projectId, UserAccount requester) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isPublic()) {
            throw new IllegalStateException("非公開のプロジェクトには参加を申請できません。");
        }
        if (targetProject.isMember(requester)) {
            throw new IllegalStateException("既にこのプロジェクトのメンバーです。");
        }

        for (ProjectMember member : targetProject.getMembers()) {
            if (ProjectMemberRole.OWNER.equals(member.getRole())) {
                notificationService.notify(
                        member.getUser(),
                        NotificationType.PROJECT_JOIN_REQUEST,
                        requester.getDisplayName() + " さんがプロジェクトへの参加を申請しました: " + targetProject.getTitle(),
                        null,
                        "/projects/" + projectId + "/edit");
            }
        }
    }

    /**
     * お気に入り登録・解除をトグルする。
     */
    @Transactional
    public void toggleFavorite(Long projectId, UserAccount user) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        targetProject.toggleFavorite(user);
        projectRepository.save(targetProject);
    }

    /**
     * プロジェクトアイコン画像を更新する。OWNER または管理者のみ実行可能。既存の画像があれば置き換え時に削除される。
     */
    @Transactional
    public Project updateIcon(Long projectId, MultipartFile file, UserAccount actingUser) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        if (!targetProject.isOwner(actingUser) && !actingUser.isAdmin()) {
            throw new IllegalStateException("アイコンを変更する権限がありません。");
        }

        String storedFileName = imageStorageService.storeIcon(projectId, file, targetProject.getIconStoredFileName());
        targetProject.changeIcon(storedFileName);
        return projectRepository.save(targetProject);
    }

    /**
     * プロジェクトアイコン画像のストリームを取得する。
     */
    public InputStream loadIconStream(Long projectId, String storedFileName) {
        return imageStorageService.loadIcon(projectId, storedFileName);
    }
}
