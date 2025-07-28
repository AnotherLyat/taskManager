API for task management using Spring Framework

# Commands

## Persons

### Creating individual (post)
```
/api/persons
```
Will take a raw Json  and save it as a person, with name and department as required fields.
Exemple:
```
{
    "name": "Lyat",
    "department": "TI"
}
```

### update individual (put)
```
/api/persons/{id}
```
send a raw Json to update the name or department from the specified id 

### List all (get)
```
/api/persons/all
```
will return the information of all saved persons

### Search by id (get)
```
/api/persons/{id}
```
will search in the database for the person with the specified id and return their saved information

### Delete (Delete)
```
/api/persons/{id}/delete
```
will delete from the database the specified person with the specified id

### billable (get)
```
/api/persons/{id}/bill
```
return part of the value from the specified person, like name, department, average task duration and total task completed, as string.
It's my interpertation of one requirement cited, will need to clarify later if possible. if not possible it will stay here.

### List by department
```
/api/persons/department/{department}
```
will list all persons in the specified department

## Tasks

### Creating task (post)
```
/api/task
```
Will take a raw Json. Exemple:
```
{
  "title": "API dev",
  "description": "make a README",
  "deadline": "2025-08-01",
  "department": "TI",
  "duration": 4,
  "assignedPersonId": null
}
```
all tasks start on the IDLE state by default

### List all (get)
```
/api/tasks/all
```
will return the information of all saved tasks

### Search by id (get)
```
/api/tasks/{taskId}
```
will search in the database for the task with the specified id and return their saved information

### Delete (Delete)
```
/api/tasks/{TaskId}/delete
```
will delete from the database the specified person with the specified id

### Assign task (put)
```
/api/tasks/{taskId}/assign/{personId}
```
assign person of the specified ID to the task of the specified ID

### Change Status
```
/api/tasks/{taskId}/status/{status}
```
change the status of the task between IDLE, ACTIVE, COMPLETED and CANCELLED. 
It will save the time when the state changes to Active and when the state changes to COMPLETED or CANCELLED, then make an average of the time it takes for the person assigned to the task to complete any task.

ps: i do see some loopholes in the logic, like setting to active twice to make the average smaller or how a task can be 'remembered' as completed multiple times, will need to solve those later

### List IDLE
```
/api/tasks/pending
```
Will list all tasks in the IDLE state

### List 3 IDLE
```
/api/tasks/pending/rand
```
Will list 3 tasks in the IDLE state at random. I did not understand why it was asked, since number displayed could be controlled on the front part (which is not what i'm doing), but i did it 

### List by department
```
/api/tasks/department/{department}
```
will list all tasks in the specified department
