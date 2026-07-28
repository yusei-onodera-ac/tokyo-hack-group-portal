package com.tokyohackgroup.tokyohackgroup_portal.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tokyohackgroup.tokyohackgroup_portal.domain.model.project.Project;
import com.tokyohackgroup.tokyohackgroup_portal.domain.model.task.Task;

/**
 * プロジェクトタスク（tasksテーブル）に対するデータアクセスを担うリポジトリ。
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByProjectOrderByStatusAscDueDateAsc(Project project);
}
