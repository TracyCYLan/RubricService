package edu.csula.rubrics.web;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.csula.rubrics.models.Artifact;
import edu.csula.rubrics.models.Association;
import edu.csula.rubrics.models.Task;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.AssessmentDao;
import edu.csula.rubrics.models.dao.TaskDao;
import edu.csula.rubrics.models.dao.UserDao;
import edu.csula.rubrics.models.dao.AssociationDao;
import edu.csula.rubrics.models.dao.ArtifactDao;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/")
public class TaskController {

	@Autowired
	AssessmentDao assessmentDao;

	@Autowired
	TaskDao taskDao;

	@Autowired
	UserDao userDao;

	@Autowired
	AssociationDao associationDao;

	@Autowired
	ArtifactDao artifactDao;

	// get certain task
	@GetMapping("task/{id}")
	public Task getTask(@PathVariable Long id) {
		Task task = taskDao.getTask(id);
		if (task == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
		return task;
	}

	// create an task. we will get Task(name, dueDate, type) and userId and
	// associationId from front end
	@PostMapping("task/assessor/{assessorId}/association/{associationId}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long addTask(@RequestBody Task task, @PathVariable Long assessorId, @PathVariable Long associationId) {
		User assessor = userDao.getUser(assessorId);
		if (assessor == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User doesn't exist");
		Association association = associationDao.getAssociation(associationId);
		if (association == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Association not found");
		task.setAssessor(assessor);
		task.setAssociation(association);
		task = taskDao.saveTask(task);
		return task.getId();
	}

	// get tasks belongs to this user
	@GetMapping("task/user/{id}")
	public List<Task> getTasks(@PathVariable Long id) {
		User user = userDao.getUser(id);
		return taskDao.getPersonalTask(user);
	}

	// get all associations
	@GetMapping("association")
	public List<Association> getAssociations() {
		return associationDao.getAllAssociations();
	}

	// get all artifacts
	@GetMapping("artifact")
	public List<Artifact> getArtifacts() {
		return artifactDao.getAllArtifacts();
	}
}
