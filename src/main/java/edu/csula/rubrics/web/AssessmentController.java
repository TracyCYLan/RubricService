package edu.csula.rubrics.web;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.AssessmentDao;
import edu.csula.rubrics.models.dao.RubricDao;
import edu.csula.rubrics.models.dao.UserDao;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

	@Autowired
	AssessmentDao assessmentDao;

	@Autowired
	UserDao userDao;
	
	@Autowired
	RubricDao rubricDao;
	
	// get certain evaluation
	@GetMapping("/{id}")
	public Assessment getAssessment(@PathVariable Long id) {
		Assessment assessment = assessmentDao.getAssessment(id);
		if (assessment == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found");
		return assessment;
	}

//	// create an evaluation. Do we need to think about how to deal with Task status?
//	/*
//	 * { "comments": "Average", 
//	 *   "completed": true, 
//	 *   "deleted": true, 
//	 *   "evaluatee": {
//	 *   "id": 1002 }, 
//	 *   "evaluator": { 
//	 *   "id": 1001 }, 
//	 *   "ratings": [ { "id": 2 }, { "id": 6 } ], 
//	 *   "rubric": { "id": 2 }, 
//	 *   "task": { "id": 900 } }
//	 */
//	@PostMapping
//	@ResponseStatus(HttpStatus.CREATED)
//	public Long addEvaluation(@RequestBody Evaluation evaluation) {
//		evaluation.setDate(new Date());
//		evaluation = evaluationDao.saveEvaluation(evaluation);
//		return evaluation.getId();
//	}
	

}
