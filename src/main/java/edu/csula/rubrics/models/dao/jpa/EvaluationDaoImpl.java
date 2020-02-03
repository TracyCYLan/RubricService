package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


import edu.csula.rubrics.models.Evaluation;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.EvaluationDao;


@Repository
public class EvaluationDaoImpl implements EvaluationDao {

    @PersistenceContext
    private EntityManager entityManager;

    //get a evaluation by id
    @Override
    public Evaluation getEvaluation( Long id )
    {
        return entityManager.find( Evaluation.class, id );
    }


    //add a new evaluation
    @Override
    @Transactional
    public Evaluation saveEvaluation( Evaluation evaluation )
    {
        return entityManager.merge( evaluation );
    }

    //get all evaluations of this user
	@Override
	public List<Evaluation> getEvaluationsOfEvaluatee(User evaluatee) {
		String query = "from Evaluation where evaluatee = :evaluatee and deleted = false";

        return entityManager.createQuery( query, Evaluation.class )
            .setParameter( "evaluatee", evaluatee )
            .getResultList();
	}


	//get all evaluations with certain rubric and evaluatee
	@Override
	public List<Evaluation> getEvaluationByRubricEvaluatee(Rubric rubric, User evaluatee) {
		String query = "from Evaluation where rubric =:rubric and evaluatee = :evaluatee and deleted = false";

        return entityManager.createQuery( query, Evaluation.class )
            .setParameter( "rubric", rubric )
            .setParameter("evaluatee", evaluatee)
            .getResultList();
	}

}
