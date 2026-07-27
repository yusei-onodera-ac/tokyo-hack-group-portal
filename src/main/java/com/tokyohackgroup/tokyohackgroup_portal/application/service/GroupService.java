package com.tokyohackgroup.tokyohackgroup_portal.application.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.group.Group;
import com.tokyohackgroup.tokyohackgroup_portal.domain.repository.GroupRepository;

/**
 * 所属グループ・プロジェクトに関するビジネスロジックを統括するアプリケーションサービス。
 */
@Service
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    /**
     * 指定されたユーザーが所属するグループの一覧を取得する。
     *
     * <p>open-in-view を無効化しているため、ビュー描画時の LazyInitializationException を防ぐべく
     * トランザクション境界内でメンバーコレクションを初期化してから返却する。</p>
     */
    public List<Group> fetchGroupsForUser(UserAccount user) {
        List<Group> groups = groupRepository.findByMembersContaining(user);
        groups.forEach(group -> group.getMembers().size());
        return groups;
    }

    public Optional<Group> findById(Long groupId) {
        Optional<Group> groupOptional = groupRepository.findById(groupId);
        groupOptional.ifPresent(group -> group.getMembers().size());
        return groupOptional;
    }

    @Transactional
    public Group registerGroup(String name, String description) {
        Group newGroup = new Group(name, description);
        return groupRepository.save(newGroup);
    }

    @Transactional
    public void addMember(Long groupId, UserAccount member) {
        Group targetGroup = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("指定されたグループが見つかりません。ID: " + groupId));

        targetGroup.addMember(member);
        groupRepository.save(targetGroup);
    }
}
