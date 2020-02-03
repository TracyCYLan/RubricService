package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;


public interface RubricDao {

    Rubric getRubric( Long id );

    List<Rubric> getPersonalRubrics( User creator );

    List<Rubric> getPublishedPersonalRubrics( User creator );

    List<Rubric> searchRubrics( String text, int maxResults );

    Rubric saveRubric( Rubric rubric );

    List<Rubric> getAllRubrics();
}

