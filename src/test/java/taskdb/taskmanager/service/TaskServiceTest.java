package taskdb.taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import taskdb.taskmanager.entity.Person;
import taskdb.taskmanager.entity.Task;
import taskdb.taskmanager.enums.TaskStatus;
import taskdb.taskmanager.repository.PersonRepository;
import taskdb.taskmanager.repository.TaskRepository;

import java.lang.StackWalker.Option;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTaskWithValidPerson() {
        Person person = Person.builder().id(1L).build();
        Task task = Task.builder().title("Test Task").build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(person));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        Optional<Task> result = taskService.save(task, 1L);

        assertTrue(result.isPresent());
        assertEquals(person, result.get().getAssignedPerson());
        verify(taskRepository).save(task);
    }
    
    @Test
    void testSaveTaskWithInvalidPerson() {
        Task task = Task.builder().title("Test Task").build();

        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.save(task, 1L);

        assertFalse(result.isPresent());
        verify(taskRepository, never()).save(any(Task.class));
    }

    void testUpdateStatusToCompleted_ShouldUpdatePersonStats() {
        Person person = Person.builder()
                .id(1L)
                .totalTasksCompleted(2)
                .averageTaskDuration(30.0)
                .build();

        LocalDateTime activeAt = LocalDateTime.now().minusMinutes(60);
        Task task = Task.builder()
                .id(1L)
                .status(TaskStatus.ACTIVE)
                .activeAt(activeAt)
                .assignedPerson(person)
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(personRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Task> result = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);

        assertTrue(result.isPresent());
        assertEquals(TaskStatus.COMPLETED, result.get().getStatus());
        assertEquals(3, person.getTotalTasksCompleted());

        // average duration: ((30 * 2) + 60) / 3 = 40
        assertEquals(40.0, person.getAverageTaskDuration());
    }

    @Test
    void testUpdateAssignedPerson() {
        Task task = Task.builder().id(1L).build();
        Person newPerson = Person.builder().id(2L).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(personRepository.findById(2L)).thenReturn(Optional.of(newPerson));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Task> result = taskService.updateAssignedPerson(1L, 2L);

        assertTrue(result.isPresent());
        assertEquals(newPerson, result.get().getAssignedPerson());
    }

    @Test
    void testDeleteTask() {
        taskService.delete(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetTaskById_validAndInvalidId() {
        Task task = Task.builder().id(1L).title("Test Task").build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Task> result = taskService.getById(1L);
        assertTrue(result.isPresent());
        assertEquals("Test Task", result.get().getTitle());

        Optional<Task> invalidResult = taskService.getById(2L);
        assertFalse(invalidResult.isPresent());
    }

    @Test
    void testGetTaskByDepartment() {
        Task task1 = Task.builder().id(1L).department("IT").build();

        List<Task> tasks = List.of(task1);

        when(taskRepository.findByDepartment("IT")).thenReturn(tasks);

        List<Task> result = taskService.getTasksByDepartment("IT");

        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getDepartment());
    }

    @Test
    void testGetAllTasks() {
        Task task1 = Task.builder().id(1L).title("Task 1").build();
        Task task2 = Task.builder().id(2L).title("Task 2").build();

        List<Task> tasks = List.of(task1, task2);

        when(taskRepository.findAll()).thenReturn(tasks);

        List<Task> result = taskService.getAll();

        assertEquals(2, result.size());
        assertEquals("Task 1", result.get(0).getTitle());
        assertEquals("Task 2", result.get(1).getTitle());
    }

    @Test
    void testSaveTask_idleDeafaultStatus() {
        Task task = Task.builder().title("Test Task").status(null).build();

        Task savedTask = Task.builder()
                .title("Test Task")
                .status(TaskStatus.IDLE)
                .build();

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task toSave = invocation.getArgument(0);
            if (toSave.getStatus() == null) {
                toSave.setStatus(TaskStatus.IDLE);
            }
            return toSave;
        });

        Optional<Task> result = taskService.save(task, null);

        assertTrue(result.isPresent());
        assertEquals(TaskStatus.IDLE, result.get().getStatus());
    }

    @Test
    void testUpdateTaskStatusToCompleted_UpdatesPersonStats() {
        Person person = Person.builder()
                .id(1L)
                .totalTasksCompleted(1)
                .averageTaskDuration(60.0)
                .build();

        Task task = Task.builder()
                .id(1L)
                .assignedPerson(person)
                .status(TaskStatus.ACTIVE)
                .activeAt(LocalDateTime.now().minusMinutes(30))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Task> result = taskService.updateTaskStatus(1L, TaskStatus.COMPLETED);

        assertTrue(result.isPresent());

        Person updatedPerson = result.get().getAssignedPerson();
        assertEquals(2, updatedPerson.getTotalTasksCompleted());
        assertTrue(updatedPerson.getAverageTaskDuration() < 60.0); // now ~50min
    }

    @Test
    void testUpdateAssignedPerson_InvalidInputs() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());
        when(personRepository.findById(2L)).thenReturn(Optional.of(new Person()));

        Optional<Task> result1 = taskService.updateAssignedPerson(1L, 2L);
        assertFalse(result1.isPresent());

        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task()));
        when(personRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Task> result2 = taskService.updateAssignedPerson(1L, 2L);
        assertFalse(result2.isPresent());
    }


    @Test
    void testDeleteTask_CallsRepository() {
        taskService.delete(1L);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateTaskStatus_SetsTimestamps() {
        Task task = Task.builder().id(1L).status(TaskStatus.IDLE).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Task> result = taskService.updateTaskStatus(1L, TaskStatus.ACTIVE);

        assertTrue(result.isPresent());
        assertNotNull(result.get().getActiveAt());
    }

}
