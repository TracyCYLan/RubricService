package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Task;
import edu.csula.rubrics.models.User;

public interface TaskDao {
	
	Task getTask(Long id);
	
	Task saveTask(Task task);
	
	List<Task> getPersonalTask(User evaluator);
}
