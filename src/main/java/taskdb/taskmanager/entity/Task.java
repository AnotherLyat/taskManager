package taskdb.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import taskdb.taskmanager.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder

public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate deadline;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private int duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.IDLE;
    
    private LocalDateTime activeAt;
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = true)
    private Person assignedPerson;
}
