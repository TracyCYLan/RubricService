package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;

public interface CriterionDao {
	
	  Criterion getCriterion(Long id);
	  
	  Criterion saveCriterion(Criterion criterion);
	  
	  List<Criterion> getAllCriteria();
	  
	  Rating getRating(Long id);
	  
	  Rating saveRating(Rating rating);
	  
	  List<Rating> getRatings(Criterion criterion);
	  
	  List<Criterion> searchCriteria( String text );
}
