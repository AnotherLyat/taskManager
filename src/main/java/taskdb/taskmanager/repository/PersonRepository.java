package taskdb.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import taskdb.taskmanager.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
    List<Person> findByDepartment(String department);
}
