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

import edu.csula.rubrics.models.Evaluation;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.EvaluationDao;
import edu.csula.rubrics.models.dao.RubricDao;
import edu.csula.rubrics.models.dao.UserDao;

@RestController
@RequestMapping("/evaluation")
public class EvaluationController {

	@Autowired
	EvaluationDao evaluationDao;

	@Autowired
	UserDao userDao;
	
	@Autowired
	RubricDao rubricDao;
	
	// get certain evaluation
	@GetMapping("/{id}")
	public Evaluation getEvaluation(@PathVariable Long id) {
		Evaluation evaluation = evaluationDao.getEvaluation(id);
		if (evaluation == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evaluation not found");
		return evaluation;
	}

	// create an evaluation. Do we need to think about how to deal with Task status?
	/*
	 * { "comments": "Average", 
	 *   "completed": true, 
	 *   "deleted": true, 
	 *   "evaluatee": {
	 *   "id": 1002 }, 
	 *   "evaluator": { 
	 *   "id": 1001 }, 
	 *   "ratings": [ { "id": 2 }, { "id": 6 } ], 
	 *   "rubric": { "id": 2 }, 
	 *   "task": { "id": 900 } }
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long addEvaluation(@RequestBody Evaluation evaluation) {
		evaluation.setDate(new Date());
		evaluation = evaluationDao.saveEvaluation(evaluation);
		return evaluation.getId();
	}
	
	//get all evaluations of this user
	@GetMapping("/user/{id}")
	public List<Evaluation> getEvaluationsOfEvaluatee(@PathVariable Long id){
		User evaluatee = userDao.getUser(id);
		return evaluationDao.getEvaluationsOfEvaluatee(evaluatee);
	}
	
	//get all evaluations with certain rubric and evaluatee
	@GetMapping("/rubric/{rid}/user/{uid}")
	public List<Evaluation> getEvaluationsByRubricAndEvaluatee(@PathVariable Long rid,@PathVariable Long uid){
		Rubric rubric = rubricDao.getRubric(rid);
		User evaluatee = userDao.getUser(uid);
		return evaluationDao.getEvaluationByRubricEvaluatee(rubric, evaluatee);
	}
}
