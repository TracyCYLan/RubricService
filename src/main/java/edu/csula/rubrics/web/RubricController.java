package edu.csula.rubrics.web;

import java.util.Calendar;
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

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.dao.CriterionDao;
import edu.csula.rubrics.models.dao.RubricDao;

@RestController
@RequestMapping("/rubric")
public class RubricController {

	@Autowired
	RubricDao rubricDao;

	@Autowired
	CriterionDao criterionDao;

	// get ALL rubrics
	@GetMapping
	public List<Rubric> getRubrics(ModelMap models) {
		return rubricDao.getAllRubrics();
	}

	// get this rubric
	@GetMapping("/{id}")
	public Rubric getRubric(@PathVariable Long id) {
		Rubric rubric = rubricDao.getRubric(id);
		if (rubric == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rubric not found");
		return rubric;
	}

	// create a rubric
	/*
	 * { "creator": { "id":1002 }, "deleted": false, "description":
	 * "using swagger to add this rubric", "name": "swagger test rubric",
	 * "obsolete": false, "public": true, "published": true }
	 */
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long addRubric(@RequestBody Rubric rubric) {
		rubric.setPublishDate(Calendar.getInstance());
		rubric = rubricDao.saveRubric(rubric);
		return rubric.getId();
	}

	// add an existing criterion under this rubric
	/*
	 * {"id":2} => existing criterion_id
	 */
	@PostMapping("/{id}/addCriterion")
	public void addCriterionOfRubric(@PathVariable long id, @RequestBody Criterion criterion) {
		Rubric rubric = getRubric(id);
		List<Criterion> criteria = rubric.getCriteria();
		criteria.add(criterionDao.getCriterion(criterion.getId()));
		rubricDao.saveRubric(rubric);
	}

	// create a criterion
	/*
	 * { "description":"Program Ability" }
	 */
	@PostMapping("/criterion")
	@ResponseStatus(HttpStatus.CREATED)
	public Long addCriterion(@RequestBody Criterion criterion) {
		criterion = criterionDao.saveCriterion(criterion);
		return criterion.getId();
	}

	// get ALL criteria
	@GetMapping("/criterion")
	public List<Criterion> getCriteria(ModelMap models) {
		return criterionDao.getAllCriteria();
	}

	// get this criterion
	@GetMapping("/criterion/{id}")
	public Criterion getCriterion(@PathVariable Long id) {
		Criterion criterion = criterionDao.getCriterion(id);
		if (criterion == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Criterion not found");
		return criterion;
	}

	// add rating under certain Criteria
	/*
	 * { "description": "bad coding ability", "value": 0.5 }
	 */
	@PostMapping("/criterion/{id}/rating")
	@ResponseStatus(HttpStatus.CREATED)
	public Long addRatingOfCriterion(@PathVariable long id, @RequestBody Rating rating) {
		Criterion criterion = getCriterion(id);
		// first create a rating
		rating.setCriterion(criterion);
		rating = criterionDao.saveRating(rating);
		// then add this under certain criterion
		List<Rating> ratings = criterion.getRatings();
		ratings.add(rating);
		criterionDao.saveCriterion(criterion);
		return rating.getId();
	}

}
