package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.Assessment;

public interface AssessmentDao {

	Assessment getAssessment(Long id);

	Assessment saveAssessment(Assessment assessment);

}
