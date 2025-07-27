package taskdb.taskmanager.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDate deadline;
    private String department;
    private int duration;
    private String status;
    private LocalDateTime activeAt;
    private LocalDateTime finishedAt;
    private Long assignedPersonId;

}
