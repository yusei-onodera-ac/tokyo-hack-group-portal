package com.tokyohackgroup.tokyohackgroup_portal.domain.model.project;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.Formula;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * プロジェクト単位の情報を管理する永続化エンティティ。
 *
 * <p>メンバーは {@link ProjectMember} を通じて役割（OWNER/MEMBER）付きで管理し、
 * お気に入り（ブックマーク）は役割を持たない単純な多対多関係として別管理する。</p>
 */
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    /** true の場合、非メンバーにも一覧・詳細を公開する */
    @Column(nullable = false)
    private boolean isPublic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserAccount createdBy;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProjectMember> members = new ArrayList<>();

    /** お気に入り登録しているユーザー（project_favorites 中間テーブルで自己管理） */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "project_favorites",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserAccount> favoritedByUsers = new HashSet<>();

    /**
     * 一覧のメンバー数ソートを DB レベルで行うための算出（読み取り専用）カラム。
     * open-in-view 無効環境でも安全に参照できるよう、コレクションを介さず SQL 副問合せで取得する。
     */
    @Formula("(SELECT COUNT(*) FROM project_members pm WHERE pm.project_id = id)")
    private long memberCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected Project() {
    }

    public Project(String title, String description, boolean isPublic, UserAccount createdBy) {
        this.title = title;
        this.description = description;
        this.status = ProjectStatus.PREPARING;
        this.isPublic = isPublic;
        this.createdBy = createdBy;

        LocalDateTime currentTime = LocalDateTime.now();
        this.createdAt = currentTime;
        this.updatedAt = currentTime;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public UserAccount getCreatedBy() {
        return createdBy;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public Set<UserAccount> getFavoritedByUsers() {
        return favoritedByUsers;
    }

    public long getMemberCount() {
        return memberCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * プロジェクトへ役割付きでメンバーを追加する。
     */
    public void addMember(UserAccount user, ProjectMemberRole role) {
        ProjectMember newMember = new ProjectMember(this, user, role);
        this.members.add(newMember);
    }

    /**
     * 指定ユーザーが本プロジェクトの参加メンバー（OWNER含む）かどうかを判定する。
     */
    public boolean isMember(UserAccount user) {
        if (user == null) {
            return false;
        }
        for (ProjectMember member : this.members) {
            if (member.getUser().getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 指定ユーザーが本プロジェクトの OWNER かどうかを判定する。
     */
    public boolean isOwner(UserAccount user) {
        if (user == null) {
            return false;
        }
        for (ProjectMember member : this.members) {
            if (member.getUser().getId().equals(user.getId())) {
                return ProjectMemberRole.OWNER.equals(member.getRole());
            }
        }
        return false;
    }

    /**
     * 指定ユーザーがお気に入り登録済みかどうかを判定する。
     */
    public boolean isFavoritedBy(UserAccount user) {
        if (user == null) {
            return false;
        }
        return this.favoritedByUsers.stream().anyMatch(favoriteUser -> favoriteUser.getId().equals(user.getId()));
    }

    /**
     * お気に入り登録・解除をトグルする。
     */
    public void toggleFavorite(UserAccount user) {
        boolean removed = this.favoritedByUsers.removeIf(favoriteUser -> favoriteUser.getId().equals(user.getId()));
        if (!removed) {
            this.favoritedByUsers.add(user);
        }
    }

    /**
     * ステータスを変更する。
     */
    public void changeStatus(ProjectStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
}
