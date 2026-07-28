package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollCandidate;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.PollResponse;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.poll.SchedulingPoll;

/**
 * 日程調整の回答（poll_responsesテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface PollResponseRepository extends JpaRepository<PollResponse, Long> {

    Optional<PollResponse> findByCandidateAndUser(PollCandidate candidate, UserAccount user);

    @Query("SELECT r FROM PollResponse r WHERE r.candidate.poll = :poll")
    List<PollResponse> findByPoll(@Param("poll") SchedulingPoll poll);
}
