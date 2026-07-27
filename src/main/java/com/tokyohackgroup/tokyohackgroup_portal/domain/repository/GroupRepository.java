package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.group.Group;

/**
 * グループ・プロジェクト（member_groupsテーブル）のデータアクセスを担うリポジトリ。
 */
@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    /**
     * 指定されたユーザーが所属するグループの一覧を取得する。
     *
     * @param member 所属確認対象のユーザー
     * @return 所属グループのリスト
     */
    List<Group> findByMembersContaining(UserAccount member);
}
