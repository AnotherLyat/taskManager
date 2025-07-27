package taskdb.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import taskdb.taskmanager.dto.TaskDTO;
import taskdb.taskmanager.mapper.TaskMapper;
import taskdb.taskmanager.service.TaskService;
import taskdb.taskmanager.service.PersonService;
import taskdb.taskmanager.entity.Task;
import taskdb.taskmanager.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private PersonService personService;

    @GetMapping("/all")
    public List<TaskDTO> getAllTasks() {
        return taskService.getAll().stream()
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        return taskService.getById(id)
                .map(TaskMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TaskDTO> saveTask(@RequestBody TaskDTO taskDTO) {
        Task task = TaskMapper.toEntity(taskDTO);
        
        return taskService.save(task, taskDTO.getAssignedPersonId())
                .map(TaskMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Person not found for ID: " + taskDTO.getAssignedPersonId()));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{taskId}/assign/{personId}")
    public ResponseEntity<TaskDTO> updateAssignedPerson(@PathVariable Long taskId, @PathVariable Long personId) {
        return taskService.updateAssignedPerson(taskId, personId)
                .map(TaskMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{taskId}/status/{status}")
    public ResponseEntity<TaskDTO> updateTaskStatus(@PathVariable Long taskId, @PathVariable TaskStatus status) {
        return taskService.updateTaskStatus(taskId, status)
                .map(TaskMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pending")
    public List<TaskDTO> getPendingTasks() {
        return taskService.getAll().stream()
                .filter(task -> task.getStatus() == TaskStatus.IDLE)
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/department/{department}")
    public List<TaskDTO> getTasksByDepartment(@PathVariable String department) {
        return taskService.getAll().stream()
                .filter(task -> department.equalsIgnoreCase(task.getDepartment()))
                .map(TaskMapper::toDTO)
                .collect(Collectors.toList());
    }
}