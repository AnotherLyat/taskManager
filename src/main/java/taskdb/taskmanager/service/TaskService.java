package taskdb.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskdb.taskmanager.entity.Person;
import taskdb.taskmanager.entity.Task;
import taskdb.taskmanager.enums.TaskStatus;
import taskdb.taskmanager.repository.TaskRepository;
import taskdb.taskmanager.repository.PersonRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private PersonRepository personRepository;

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> getById(Long id) {
        return taskRepository.findById(id);
    }

    public Optional<Task> save(Task task, Long personId) {
        if (personId != null) {
            Optional<Person> personOpt = personRepository.findById(personId);

            if (personOpt.isEmpty()) {
                return Optional.empty();
            }

            task.setAssignedPerson(personOpt.get());
        }

        return Optional.of(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    public Optional<Task> updateAssignedPerson(Long taskId, Long personId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<Person> personOpt = personRepository.findById(personId);

        if (taskOpt.isEmpty() || personOpt.isEmpty()) return Optional.empty();

        Task task = taskOpt.get();
        task.setAssignedPerson(personOpt.get());

        return Optional.of(taskRepository.save(task));
    }

    public Optional<Task> updateTaskStatus(Long taskId, TaskStatus status) {
        
        return taskRepository.findById(taskId).map(task -> {
            TaskStatus currentStatus = task.getStatus();
            task.setStatus(status);
            LocalDateTime now = LocalDateTime.now();

            if (status == TaskStatus.ACTIVE) {
                task.setActiveAt(now);
            } else if (status == TaskStatus.COMPLETED || status == TaskStatus.CANCELLED) {
                task.setFinishedAt(now);
            }

            if(status == TaskStatus.COMPLETED && currentStatus != TaskStatus.COMPLETED) {
                Person assignedPerson = task.getAssignedPerson();
                if (assignedPerson != null && task.getActiveAt() != null && task.getFinishedAt() != null) {
                    long taskDuration = Duration.between(task.getActiveAt(), task.getFinishedAt()).toMinutes();

                    int previousTotal = assignedPerson.getTotalTasksCompleted();
                    int newTotal = previousTotal + 1;
                    assignedPerson.setTotalTasksCompleted(newTotal);

                    double previousAverage = assignedPerson.getAverageTaskDuration();
                    double newAverage = ((previousAverage * previousTotal) + taskDuration) / newTotal;
                    assignedPerson.setAverageTaskDuration(newAverage);

                    personRepository.save(assignedPerson);
                }
            }

            return taskRepository.save(task);
        });
    }

    public List<Task> getTasksByDepartment(String department) {
        return taskRepository.findByDepartment(department);
    }

    public List<Object[]> getTaskReportRaw() {
        return taskRepository.countTasksByDepartment();
    }

    public List<String> getTaskSummaries() {
    return taskRepository.findAllByOrderByDeadlineDesc().stream()
            .map(task -> {
                String status;
                String horasGastas = "";

                if (task.getAssignedPerson() != null) {
                    status = "Encaminhado para " + task.getAssignedPerson().getName();
                    horasGastas = "\n Tempo estimado de horas gastas: " + task.getAssignedPerson().getAverageTaskDuration() * task.getAssignedPerson().getTotalTasksCompleted();
                } else {
                    status = "Pendente";
                }

                return "Título: " + task.getTitle() +
                       "\n Prazo: " + task.getDeadline() +
                       "\n Status: " + status +
                       horasGastas;
            })
            .collect(Collectors.toList());
}

}