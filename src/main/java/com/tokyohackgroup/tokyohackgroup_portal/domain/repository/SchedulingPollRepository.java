package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.SchedulingPoll;

/**
 * 日程調整（scheduling_pollsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface SchedulingPollRepository extends JpaRepository<SchedulingPoll, Long> {

    @Query("SELECT p FROM SchedulingPoll p WHERE p.organizer = :user OR :user MEMBER OF p.invitees ORDER BY p.createdAt DESC")
    List<SchedulingPoll> findByOrganizerOrInvitee(@Param("user") UserAccount user);
}
