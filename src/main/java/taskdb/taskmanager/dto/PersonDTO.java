package taskdb.taskmanager.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PersonDTO {

    private Long id;
    private String name;
    private String department;
    private double averageTaskDuration;
    private int totalTasksCompleted;

}