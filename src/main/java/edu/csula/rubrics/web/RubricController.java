package edu.csula.rubrics.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.Tag;
import edu.csula.rubrics.models.dao.CriterionDao;
import edu.csula.rubrics.models.dao.RubricDao;

@CrossOrigin(origins = "*", maxAge = 3600)
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
		rubric = rubricDao.saveRubric(rubric);
		return rubric.getId();
	}

	// add an existing criterion under this rubric
	/*
	 * {"id":2} => existing criterion_id
	 */
	@PostMapping("/{rid}/criterion/{cid}")
	public void addCriterionOfRubric(@PathVariable long rid, @PathVariable long cid) {
		Rubric rubric = rubricDao.getRubric(rid);
		if (rubric == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such rubric");
		List<Criterion> criteria = rubric.getCriteria();
		criteria.add(criterionDao.getCriterion(cid));
		rubric.setLastUpdatedDate(new Date());
		rubricDao.saveRubric(rubric);
	}
	@DeleteMapping("/{rid}/criterion/{cid}")
	public void removeCriterionOfRubric(@PathVariable long rid, @PathVariable long cid) {
		Rubric rubric = rubricDao.getRubric(rid);
		if (rubric == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such rubric");
		List<Criterion> criteria = rubric.getCriteria();
		
		criteria.remove(criterionDao.getCriterion(cid));
		rubric.setLastUpdatedDate(new Date());
		rubricDao.saveRubric(rubric);
	}
	//changeOrderOfCriterionInRubric
	@PatchMapping("/{rid}/criteria/{order1}/{order2}")
	public void changeCriteriaOrderInRubric(@PathVariable long rid,@PathVariable int order1,@PathVariable int order2) {
		Rubric rubric = rubricDao.getRubric(rid);
		if (rubric == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such rubric");
		List<Criterion> criteria = rubric.getCriteria();
		Collections.swap(criteria, order1, order2);
		rubricDao.saveRubric(rubric);
	}
	// send an array of criteria id and add them to rubric
	@PostMapping("/{rid}/criteria")
	public void addCriteriaUnderRubric(@PathVariable long rid, @RequestBody long[] cids) {
		Rubric rubric = rubricDao.getRubric(rid);
		if (rubric == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such rubric");
		List<Criterion> criteria = rubric.getCriteria();
		for (long cid : cids)
			criteria.add(criterionDao.getCriterion(cid));
		rubricDao.saveRubric(rubric);
	}

	// create a criterion
	/*
	 * { "description":"Program Ability" }
	 */
	@PostMapping("/criterion")
	@ResponseStatus(HttpStatus.CREATED)
	public Long addCriterion(@RequestBody Criterion criterion) {
		List<Rating> ratings = criterion.getRatings();
		List<Tag> tags = criterion.getTags();
		criterion.setTags(new ArrayList<Tag>());// empty the tag first since we want to create it manually
		criterion = criterionDao.saveCriterion(criterion);
		long cid = criterion.getId();

		// add all ratings in criterion
		addRatingsOfCriterion(cid, ratings);

		// create tag manually and add them into tags list of criterion
		List<Tag> newTags = new ArrayList<>();
		for (Tag tag : tags)
			newTags.add(createTag(tag));

		criterion.setTags(newTags);
		criterionDao.saveCriterion(criterion);
		return criterion.getId();
	}

	// we send a list of ratings and we are going to add all ratings under this
	// criterion
	public void addRatingsOfCriterion(long cid, List<Rating> ratings) {
		Criterion criterion = getCriterion(cid);
		List<Rating> newRatings = criterion.getRatings();
		for (Rating rating : ratings) {
			// first create a rating
			rating.setCriterion(criterion);
			rating = criterionDao.saveRating(rating);
			// then add this under certain criterion
			newRatings.add(rating);
		}
	}

	public Tag createTag(Tag tag) {
		if (tag.getValue() == null || tag.getValue().length() == 0)
			return null;
		// first see if tag exists in the table already
		long tagId = criterionDao.findTag(tag.getValue());
		if (tagId < 0) // create a new tag
		{
			tag = criterionDao.saveTag(tag);
		} else // fetch the existing tag from db
		{
			tag = criterionDao.getTag(tagId);
		}
		tag.setCount(tag.getCount() + 1);
		tag = criterionDao.saveTag(tag);
		return tag;
	}

	// get ALL criteria
	@GetMapping("/criterion")
	public List<Criterion> getAllCriteria(ModelMap models) {
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

	// edit properties of rubric
	@PatchMapping("/{id}")
	public void editRubric(@PathVariable Long id, @RequestBody Map<String, Object> update) {
		Rubric rubric = rubricDao.getRubric(id);
		for (String key : update.keySet()) {
			switch (key) {
			case "name":
				rubric.setName((String) update.get(key));
				break;
			case "description":
				rubric.setDescription((String) update.get(key));
				break;
			case "publishDate":
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String dateInString = (String) update.get(key);
					if(dateInString==null)
					{
						rubric.setPublishDate(null);
					}
					else
					{
						Date date = sdf.parse(dateInString);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						rubric.setPublishDate(calendar);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					break;
				}
			default:
			}
		}
		rubric.setLastUpdatedDate(new Date());
		rubricDao.saveRubric(rubric);
	}

	// edit properties of criteria.
	@PatchMapping("/criterion/{id}")
	public void editCriterion(@PathVariable Long id, @RequestBody Criterion updatedCriterion) {
		Criterion criterion = criterionDao.getCriterion(id);
		// empty ratings and tags
		List<Rating> ratings = criterion.getRatings();
		while (ratings.size() > 0) {
			Rating r = ratings.get(0);
			r.setCriterion(null);
			ratings.remove(0);
		}
		List<Tag> tags = criterion.getTags();
		while (tags.size() > 0) {
			Tag tag = tags.get(0);
			int count = tag.getCount();
			if (count > 0)
				tag.setCount(count - 1);
			tags.remove(0);
		}
		criterion.setName(updatedCriterion.getName());
		criterion.setDescription(updatedCriterion.getDescription());
		criterion.setPublishDate(updatedCriterion.getPublishDate());

		addRatingsOfCriterion(id, updatedCriterion.getRatings());

		// create tag manually and add them into tags list of criterion
		List<Tag> newTags = new ArrayList<>();
		for (Tag tag : updatedCriterion.getTags())
			newTags.add(createTag(tag));
		criterion.setTags(newTags);
	
		criterionDao.saveCriterion(criterion);
	}

	// delete this rubric
	@DeleteMapping("/delete/{id}")
	public void deleteRubric(@PathVariable Long id) {
		Rubric rubric = rubricDao.getRubric(id);
		rubric.setDeleted(true);
		rubricDao.saveRubric(rubric);
	}

	// delete this criterion
	@DeleteMapping("/criterion/delete/{id}")
	public void deleteCriterion(@PathVariable Long id) {
		Criterion criterion = criterionDao.getCriterion(id);
		criterion.setDeleted(true);
		criterionDao.saveCriterion(criterion);
	}

	// search rubrics
	@GetMapping("/search/{text}")
	public List<Rubric> searchRubrics(@RequestParam String text) {
		List<Rubric> rubrics = null;
		if (text != null)
			rubrics = rubricDao.searchRubrics(text);

		return rubrics;
	}

	// search criteria
	@GetMapping("/criterion/search/{text}")
	public List<Criterion> searchCriteria(@RequestParam String text) {
		List<Criterion> criteria = null;
		if (text != null)
			criteria = criterionDao.searchCriteria(text);

		return criteria;
	}

	// add Tag on Criterion
	@PostMapping("/criterion/{id}/tag")
	@ResponseStatus(HttpStatus.CREATED)
	public Long addTagOfCriterion(@PathVariable long id, @RequestBody Tag tag) {
		if (tag.getValue() == null || tag.getValue().length() == 0)
			return (long) -1;
		Criterion criterion = getCriterion(id);
		// first see if tag exists in the table already
		long tagId = criterionDao.findTag(tag.getValue());
		if (tagId < 0) // create a new tag
		{
			tag = criterionDao.saveTag(tag);
		} else // fetch the existing tag from db
		{
			tag = criterionDao.getTag(tagId);
		}
		tag.setCount(tag.getCount() + 1);
		// then add this tag under certain criterion
		List<Tag> tags = criterion.getTags();
		tags.add(tag);
		criterionDao.saveCriterion(criterion);
		return tag.getId();
	}

	// get ALL tags
	@GetMapping("/tag")
	public List<Tag> getTags() {
		return criterionDao.getAllTags();
	}

	// get certain tag
	// get this criterion
	@GetMapping("/tag/{id}")
	public Tag getTag(@PathVariable Long id) {
		Tag tag = criterionDao.getTag(id);
		if (tag == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag not found");

		return tag;
	}

	// get all criteria use this tag
	@GetMapping("/criterion/tag/{tagvalue}")
	public List<Criterion> getAllCriteriaByTag(@PathVariable String tagvalue) {
		return criterionDao.getAllCriteriaByTag(tagvalue);
	}

	// search tag
	@GetMapping("/tag/search/{text}")
	public List<Tag> searchTag(@RequestParam String text) {
		List<Tag> tag = null;
		if (text != null)
			tag = criterionDao.searchTag(text);

		return tag;
	}
}
