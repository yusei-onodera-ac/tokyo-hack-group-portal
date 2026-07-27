package com.tokyohackgroup.tokyohackgroup_portal.domain.model.group;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;

/**
 * 所属グループ・プロジェクト単位の情報を管理する永続化エンティティ。
 */
@Entity
@Table(name = "member_groups")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /** このグループ・プロジェクトに所属するメンバー */
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<UserAccount> members = new HashSet<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Group() {
    }

    public Group(String name, String description) {
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<UserAccount> getMembers() {
        return members;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * グループへメンバーを追加する。
     */
    public void addMember(UserAccount member) {
        this.members.add(member);
    }
}
