package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import edu.csula.rubrics.models.Task;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.TaskDao;


@Repository
public class TaskDaoImpl implements TaskDao {

    @PersistenceContext
    private EntityManager entityManager;

    //get a task by id
    @Override
    public Task getTask( Long id )
    {
        return entityManager.find( Task.class, id );
    }


    //add a new task
    @Override
    @Transactional
    public Task saveTask( Task task )
    {
        return entityManager.merge( task );
    }


    //get all tasks which this user should do or already did.
	@Override
	public List<Task> getPersonalTask(User evaluator) {
		String query = "from Task where evaluator = :evaluator order by name asc";

        return entityManager.createQuery( query, Task.class )
            .setParameter( "evaluator", evaluator )
            .getResultList();
	}

}
