package taskdb.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import taskdb.taskmanager.dto.TaskDTO;
import taskdb.taskmanager.entity.Task;
import taskdb.taskmanager.enums.TaskStatus;
import taskdb.taskmanager.service.TaskService;
import taskdb.taskmanager.service.PersonService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllTasks() throws Exception {
        Task task1 = Task.builder()
                .id(1L)
                .title("Task 1")
                .status(TaskStatus.IDLE)
                .department("HR")
                .deadline(LocalDate.now().plusDays(1))
                .duration(60)
                .build();

        Task task2 = Task.builder()
                .id(2L)
                .title("Task 2")
                .status(TaskStatus.ACTIVE)
                .department("IT")
                .deadline(LocalDate.now().plusDays(2))
                .duration(30)
                .build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].status").value("ACTIVE"));
    }

    @Test
    void testGetTaskById_Found() throws Exception {
        Task task = Task.builder()
                .id(1L)
                .title("Task 1")
                .status(TaskStatus.IDLE)
                .build();

        Mockito.when(taskService.getById(1L)).thenReturn(Optional.of(task));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.status").value("IDLE"));
    }

    @Test
    void testGetTaskById_NotFound() throws Exception {
        Mockito.when(taskService.getById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSaveTask_Success() throws Exception {
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setTitle("New Task");
        taskDTO.setStatus("IDLE");
        taskDTO.setDepartment("HR");
        taskDTO.setDeadline(LocalDate.now().plusDays(3));
        taskDTO.setDuration(45);
        taskDTO.setAssignedPersonId(1L);

        Task savedTask = Task.builder()
                .id(1L)
                .title("New Task")
                .status(TaskStatus.IDLE)
                .department("HR")
                .deadline(LocalDate.now().plusDays(3))
                .duration(45)
                .build();

        Mockito.when(taskService.save(any(Task.class), eq(1L))).thenReturn(Optional.of(savedTask));
    }


    @Test
    void testGetAllTasks_EmptyList() throws Exception {
        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetPendingTasks_NoIdleTasks() throws Exception {
        Task task1 = Task.builder().id(1L).status(TaskStatus.ACTIVE).build();
        Task task2 = Task.builder().id(2L).status(TaskStatus.COMPLETED).build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetRandPendingTasks_LessThanThree() throws Exception {
        Task task1 = Task.builder().id(1L).status(TaskStatus.IDLE).build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1));

        mockMvc.perform(get("/api/tasks/pending/rand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void testGetTasksByDepartment_NoMatch() throws Exception {
        Task task1 = Task.builder().id(1L).department("HR").build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1));

        mockMvc.perform(get("/api/tasks/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testUpdateTaskStatus_InvalidStatus() throws Exception {
        // This test checks if Spring returns 400 for invalid enum value
        mockMvc.perform(put("/api/tasks/1/status/INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAssignedPerson_Success() throws Exception {
        Task updatedTask = Task.builder().id(1L).build();

        Mockito.when(taskService.updateAssignedPerson(1L, 2L)).thenReturn(Optional.of(updatedTask));

        mockMvc.perform(put("/api/tasks/1/assign/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateAssignedPerson_NotFound() throws Exception {
        Mockito.when(taskService.updateAssignedPerson(1L, 99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tasks/1/assign/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTaskStatus_Success() throws Exception {
        Task updatedTask = Task.builder()
                .id(1L)
                .status(TaskStatus.COMPLETED)
                .finishedAt(LocalDateTime.now())
                .build();

        Mockito.when(taskService.updateTaskStatus(1L, TaskStatus.COMPLETED)).thenReturn(Optional.of(updatedTask));

        mockMvc.perform(put("/api/tasks/1/status/COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void testUpdateTaskStatus_NotFound() throws Exception {
        Mockito.when(taskService.updateTaskStatus(1L, TaskStatus.COMPLETED)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tasks/1/status/COMPLETED"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPendingTasks() throws Exception {
        Task task1 = Task.builder().id(1L).status(TaskStatus.IDLE).build();
        Task task2 = Task.builder().id(2L).status(TaskStatus.ACTIVE).build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("IDLE"));
    }

    @Test
    void testGetRandPendingTasks() throws Exception {
        Task task1 = Task.builder().id(1L).status(TaskStatus.IDLE).build();
        Task task2 = Task.builder().id(2L).status(TaskStatus.IDLE).build();
        Task task3 = Task.builder().id(3L).status(TaskStatus.IDLE).build();
        Task task4 = Task.builder().id(4L).status(TaskStatus.IDLE).build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1, task2, task3, task4));

        mockMvc.perform(get("/api/tasks/pending/rand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }

    @Test
    void testGetTasksByDepartment() throws Exception {
        Task task1 = Task.builder().id(1L).department("HR").build();
        Task task2 = Task.builder().id(2L).department("IT").build();

        Mockito.when(taskService.getAll()).thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/api/tasks/department/HR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].department").value("HR"));
    }
}
