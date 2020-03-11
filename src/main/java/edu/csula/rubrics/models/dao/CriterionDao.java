package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.Tag;

public interface CriterionDao {
	
	  Criterion getCriterion(Long id);
	  
	  Criterion saveCriterion(Criterion criterion);
	  
	  List<Criterion> getAllCriteria();
	  
	  Rating getRating(Long id);
	  
	  Rating saveRating(Rating rating);
	  
	  List<Rating> getRatings(Criterion criterion);
	  
	  List<Criterion> searchCriteria( String text );
	  
	  List<Criterion> getAllCriteriaByTag( String tagvalue );
	  
	  Tag getTag(Long id);
	  
	  Tag saveTag(Tag tag);
	  
	  List<Tag> searchTag (String text);
	  
	  Long findTag(String value);
	  
	  List<Tag> getAllTags();
}
