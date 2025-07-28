package taskdb.taskmanager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import taskdb.taskmanager.dto.PersonDTO;
import taskdb.taskmanager.mapper.PersonMapper;
import taskdb.taskmanager.service.PersonService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("/all")
    public List<PersonDTO> getAllPersons() {
        return personService.getAllPersons().stream()
                .map(PersonMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        return personService.getPersonById(id)
                .map(PersonMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public PersonDTO savePerson(@RequestBody PersonDTO personDTO) {
        return PersonMapper.toDTO(personService.savePerson(PersonMapper.toEntity(personDTO)));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDTO> updatePerson(@PathVariable Long id, @RequestBody PersonDTO personDTO) {
        personDTO.setId(null);

        return personService.alterData(id, PersonMapper.toEntity(personDTO))
                .map(PersonMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("{id}/bill")
    public ResponseEntity<String> getBill(@PathVariable Long id) {
        return personService.getPersonById(id)
                .map(person -> {
                    String bill = "Person: " + person.getName() + "\n" +
                                  "Department: " + person.getDepartment() + "\n" +
                                  "Average Task Duration: " + person.getAverageTaskDuration() + "\n" +
                                  "Total Tasks Completed: " + person.getTotalTasksCompleted();
                    return ResponseEntity.ok(bill);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{department}")
    public List<PersonDTO> getPersonsByDepartment(@PathVariable String department) {
        return personService.getAllPersons().stream()
                .filter(person -> department.equalsIgnoreCase(person.getDepartment()))
                .map(PersonMapper::toDTO)
                .collect(Collectors.toList());
    }
}