package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Evaluation;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;

public interface EvaluationDao {

    Evaluation getEvaluation( Long id );

    Evaluation saveEvaluation( Evaluation evaluation );

    List<Evaluation> getEvaluationsOfEvaluatee(User evaluatee);
    
    List<Evaluation> getEvaluationByRubricEvaluatee(Rubric rubric, User evaluatee);
}
