package edu.csula.rubrics.canvas.dao;

import java.util.List;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.Tag;

public interface CanvasDao {
	
	 long checkRubricExists(String extsource, String extid);
	 
	 long checkCriterionExists(String extsource, String extid);
	 
}
