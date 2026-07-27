package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectMemberRole;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.ProjectStatus;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.ProjectRepository;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.UserAccountRepository;

/**
 * プロジェクトの検索・作成・ステータス変更・お気に入り管理を統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class ProjectService {

    /** 一覧画面での1ページあたりの表示件数 */
    public static final int PAGE_SIZE = 12;

    private final ProjectRepository projectRepository;
    private final UserAccountRepository userAccountRepository;

    public ProjectService(ProjectRepository projectRepository, UserAccountRepository userAccountRepository) {
        this.projectRepository = projectRepository;
        this.userAccountRepository = userAccountRepository;
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
     * お気に入り登録・解除をトグルする。
     */
    @Transactional
    public void toggleFavorite(Long projectId, UserAccount user) {
        Project targetProject = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたプロジェクトが見つかりません。ID: " + projectId));

        targetProject.toggleFavorite(user);
        projectRepository.save(targetProject);
    }
}
