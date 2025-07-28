package taskdb.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import taskdb.taskmanager.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDepartment(String department);
}