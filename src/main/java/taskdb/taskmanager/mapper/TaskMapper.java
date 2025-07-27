package taskdb.taskmanager.mapper;

import taskdb.taskmanager.dto.TaskDTO;
import taskdb.taskmanager.entity.Task;
import taskdb.taskmanager.entity.Person;
import taskdb.taskmanager.enums.TaskStatus;

public class TaskMapper {

    public static TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .deadline(task.getDeadline())
                .department(task.getDepartment())
                .duration(task.getDuration())
                .status(task.getStatus() != null ? task.getStatus().name() : null)
                .assignedPersonId(task.getAssignedPerson() != null ? task.getAssignedPerson().getId() : null)
                .activeAt(task.getActiveAt())
                .finishedAt(task.getFinishedAt())
                .build();
    }

    public static Task toEntity(TaskDTO taskDTO) {
        if (taskDTO == null) {
            return null;
        }

        Task.TaskBuilder builder = Task.builder()
                .id(taskDTO.getId())
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .deadline(taskDTO.getDeadline())
                .department(taskDTO.getDepartment())
                .duration(taskDTO.getDuration());

        if (taskDTO.getStatus() != null) {
            builder.status(TaskStatus.valueOf(taskDTO.getStatus()));
        } else {
            builder.status(TaskStatus.IDLE);
        }

        return builder.build();
    }
}
