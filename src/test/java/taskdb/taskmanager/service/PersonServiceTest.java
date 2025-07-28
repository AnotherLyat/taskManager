package taskdb.taskmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import taskdb.taskmanager.entity.Person;
import taskdb.taskmanager.repository.PersonRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @InjectMocks
    private PersonService personService;

    @Test
    void testGetAllPersons() {
        List<Person> people = Arrays.asList(
                Person.builder().id(1L).name("Alice").build(),
                Person.builder().id(2L).name("Bob").build()
        );

        when(personRepository.findAll()).thenReturn(people);

        List<Person> result = personService.getAllPersons();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
    }

    @Test
    void testGetPersonById_Found() {
        Person person = Person.builder().id(1L).name("Alice").build();
        when(personRepository.findById(1L)).thenReturn(Optional.of(person));

        Optional<Person> result = personService.getPersonById(1L);
        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getName());
    }

    @Test
    void testGetPersonById_NotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Person> result = personService.getPersonById(1L);
        assertFalse(result.isPresent());
    }

    @Test
    void testSavePerson() {
        Person person = Person.builder().name("Alice").build();
        Person saved = Person.builder().id(1L).name("Alice").build();

        when(personRepository.save(person)).thenReturn(saved);

        Person result = personService.savePerson(person);
        assertEquals(1L, result.getId());
    }

    @Test
    void testDeletePerson() {
        personService.deletePerson(1L);
        verify(personRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAlterData_Found() {
        Person existing = Person.builder().id(1L).name("Old").department("OldDept").build();
        Person update = Person.builder().name("New").department("NewDept").build();
        Person saved = Person.builder().id(1L).name("New").department("NewDept").build();

        when(personRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(personRepository.save(any(Person.class))).thenReturn(saved);

        Optional<Person> result = personService.alterData(1L, update);

        assertTrue(result.isPresent());
        assertEquals("New", result.get().getName());
    }

    @Test
    void testAlterData_NotFound() {
        when(personRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<Person> result = personService.alterData(1L, Person.builder().name("X").build());

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByDepartment() {
        List<Person> people = List.of(Person.builder().id(1L).name("Alice").department("IT").build());
        when(personRepository.findByDepartment("IT")).thenReturn(people);

        List<Person> result = personService.findByDepartment("IT");

        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getDepartment());
    }
}
