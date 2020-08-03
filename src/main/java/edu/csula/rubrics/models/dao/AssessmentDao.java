package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.AssessmentGroup;

public interface AssessmentDao {

	Assessment getAssessment(Long id);

	Assessment saveAssessment(Assessment assessment);
	
	AssessmentGroup getAssessmentGroup(Long id);

	AssessmentGroup saveAssessmentGroup(AssessmentGroup assessmentGrou1p);

	List<AssessmentGroup> getAssessmentGroups();

	List<AssessmentGroup> getAssessmentGroupsByRubric(Long rid);
	
	List<AssessmentGroup> searchAssessmentGroups(String text);

}
