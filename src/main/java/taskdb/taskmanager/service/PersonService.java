package taskdb.taskmanager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import taskdb.taskmanager.entity.Person;
import taskdb.taskmanager.repository.PersonRepository;

import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    @Autowired
    private PersonRepository personRepository;

    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    public Optional<Person> getPersonById(Long id) {
        return personRepository.findById(id);
    }

    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public Optional<Person> alterData(Long id, Person person) {
        return personRepository.findById(id).map(existingPerson -> {
            existingPerson.setName(person.getName());
            existingPerson.setDepartment(person.getDepartment());
            return personRepository.save(existingPerson);
        });
    }

    public List<Person> findByDepartment(String department) {
        return personRepository.findByDepartment(department);
    }

}