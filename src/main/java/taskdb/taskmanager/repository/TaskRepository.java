package taskdb.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import taskdb.taskmanager.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t.department, " +
       "SUM(CASE WHEN t.status = 'FINISHED' THEN 1 ELSE 0 END), " +
       "SUM(CASE WHEN t.status != 'FINISHED' THEN 1 ELSE 0 END) " +
       "FROM Task t " +
       "GROUP BY t.department")
    List<Object[]> countTasksByDepartment();
    List<Task> findAllByOrderByDeadlineDesc();
    List<Task> findByDepartment(String department);
}