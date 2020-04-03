package edu.csula.rubrics.web;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.csula.rubrics.models.Task;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.AssessmentDao;
import edu.csula.rubrics.models.dao.TaskDao;
import edu.csula.rubrics.models.dao.UserDao;

@RestController
@RequestMapping("/task")
public class TaskController {

	@Autowired
	AssessmentDao assessmentDao;

	@Autowired
	TaskDao taskDao;

	@Autowired
	UserDao userDao;

	// get certain task
	@GetMapping("/{id}")
	public Task getTask(@PathVariable Long id) {
		Task task = taskDao.getTask(id);
		if (task == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
		return task;
	}

	// create an task
	/*
	 * { "type": "INSTRUCTOR", "name": "Evaluation for SC432", "evaluator": { "id":
	 * 1001 }, "rubric": { "id": 3 }, "dueDate": "2020-02-10T08:00:00.000+0000" }
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long addTask(@RequestBody Task task) {
		task = taskDao.saveTask(task);
		return task.getId();
	}

	// get tasks belongs to this user
	@GetMapping("/user/{id}")
	public List<Task> getTasks(@PathVariable Long id) {
		User user = userDao.getUser(id);
		return taskDao.getPersonalTask(user);
	}
}
