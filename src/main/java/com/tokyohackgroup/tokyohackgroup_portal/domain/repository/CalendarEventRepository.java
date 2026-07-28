package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.UserAccount;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.calendar.CalendarEvent;

/**
 * カレンダーイベント（calendar_eventsテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface CalendarEventRepository extends JpaRepository<CalendarEvent, Long> {

    /**
     * 指定期間と重なるイベントを、閲覧権限の範囲で取得する。
     *
     * <p>{@code projectId} が指定された場合はそのプロジェクトのイベントのみに絞り込む。
     * 指定がない場合は、ログインユーザーが所属するプロジェクトのイベント・自身が作成したイベント・
     * 自身が参加者に含まれるイベントを横断して集約する。</p>
     */
    @Query("SELECT e FROM CalendarEvent e WHERE e.startDateTime <= :to AND e.endDateTime >= :from AND ("
            + "(:projectId IS NOT NULL AND e.project.id = :projectId) OR "
            + "(:projectId IS NULL AND (e.project.id IN :myProjectIds OR e.createdBy = :currentUser OR :currentUser MEMBER OF e.participants))"
            + ") ORDER BY e.startDateTime ASC")
    List<CalendarEvent> search(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("projectId") Long projectId,
            @Param("myProjectIds") List<Long> myProjectIds,
            @Param("currentUser") UserAccount currentUser);
}
