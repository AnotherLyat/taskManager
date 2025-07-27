package taskdb.taskmanager.mapper;

import taskdb.taskmanager.dto.PersonDTO;
import taskdb.taskmanager.entity.Person;

public class PersonMapper {

    public static PersonDTO toDTO(Person person) {
        if (person == null) {
            return null;
        }
        return PersonDTO.builder()
                .id(person.getId())
                .name(person.getName())
                .department(person.getDepartment())
                .averageTaskDuration(person.getAverageTaskDuration())
                .totalTasksCompleted(person.getTotalTasksCompleted())
                .build();
    }

    public static Person toEntity(PersonDTO personDTO) {
        if (personDTO == null) {
            return null;
        }
        return Person.builder()
                .id(personDTO.getId())
                .name(personDTO.getName())
                .department(personDTO.getDepartment())
                .averageTaskDuration(personDTO.getAverageTaskDuration())
                .totalTasksCompleted(personDTO.getTotalTasksCompleted())
                .build();
    }
}