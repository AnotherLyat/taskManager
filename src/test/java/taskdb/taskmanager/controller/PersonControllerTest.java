package taskdb.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import taskdb.taskmanager.entity.Person;
import taskdb.taskmanager.service.PersonService;

import java.util.Arrays;
import java.util.Optional;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonController.class)
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllPersons() throws Exception {
        Person p1 = Person.builder().id(1L).name("Alice").build();
        Person p2 = Person.builder().id(2L).name("Bob").build();

        Mockito.when(personService.getAllPersons()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/persons/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }

    @Test
    void testGetPersonById_Found() throws Exception {
        Person p = Person.builder().id(1L).name("Alice").build();

        Mockito.when(personService.getPersonById(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/persons/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void testGetPersonById_NotFound() throws Exception {
        Mockito.when(personService.getPersonById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persons/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSavePerson_Success() throws Exception {
        Person p = Person.builder().id(1L).name("Alice").build();

        Mockito.when(personService.savePerson(any(Person.class))).thenReturn(p);

        mockMvc.perform(post("/api/persons")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void testDeletePerson() throws Exception {
        mockMvc.perform(delete("/api/persons/1/delete"))
                .andExpect(status().isNoContent());

        Mockito.verify(personService).deletePerson(1L);
    }

    @Test
    void testUpdatePerson_Success() throws Exception {
        Person p = Person.builder().id(1L).name("Alice").build();

        Mockito.when(personService.alterData(eq(1L), any(Person.class))).thenReturn(Optional.of(p));

        mockMvc.perform(put("/api/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void testUpdatePerson_NotFound() throws Exception {
        Person p = Person.builder().name("Alice").build();

        Mockito.when(personService.alterData(eq(1L), any(Person.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/persons/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBill_Success() throws Exception {
        Person p = Person.builder().id(1L).name("Alice").department("IT")
                .averageTaskDuration(30).totalTasksCompleted(5).build();

        Mockito.when(personService.getPersonById(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/persons/1/bill"))
                .andExpect(status().isOk())
                .andExpect(content().string("Person: Alice\n" +
                        "Department: IT\n" +
                        "Average Task Duration: 30.0\n" +
                        "Total Tasks Completed: 5"));
    }

    @Test
    void testGetBill_NotFound() throws Exception {
        Mockito.when(personService.getPersonById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/persons/99/bill"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPersonsByDepartment() throws Exception {
        List<Person> people = List.of(
            Person.builder().id(1L).name("Alice").department("IT").build(),
            Person.builder().id(2L).name("Bob").department("HR").build(),
            Person.builder().id(3L).name("Charlie").department("IT").build()
        );

        Mockito.when(personService.getAllPersons()).thenReturn(people);

        mockMvc.perform(get("/api/persons/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].name").value("Charlie"));
    }

    @Test
    void testGetPersonsByDepartment_NotFound() throws Exception {
        List<Person> people = List.of(
            Person.builder().id(1L).name("Alice").department("IT").build(),
            Person.builder().id(2L).name("Bob").department("HR").build()
        );

        Mockito.when(personService.getAllPersons()).thenReturn(people);

        mockMvc.perform(get("/api/persons/department/Finance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetPersonsByDepartment_EmptyList() throws Exception {
        Mockito.when(personService.getAllPersons()).thenReturn(List.of());

        mockMvc.perform(get("/api/persons/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetPersonsByDepartment_InvalidDepartment() throws Exception {
        Mockito.when(personService.getAllPersons()).thenReturn(List.of());

        mockMvc.perform(get("/api/persons/department/Invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}