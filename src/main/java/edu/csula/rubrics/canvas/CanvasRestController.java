package edu.csula.rubrics.canvas;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.csula.rubrics.canvas.dao.CanvasDao;
import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.Tag;
import edu.csula.rubrics.models.dao.CriterionDao;
import edu.csula.rubrics.models.dao.RubricDao;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/canvas")
public class CanvasRestController {

	@Autowired
	RubricDao rubricDao;

	@Autowired
	CriterionDao criterionDao;

	@Autowired
	CanvasDao canvasDao;

	final String EXTSOURCE = "Canvas";

	// get ALL courses via given canvasToken
	// calling url:GET|/api/v1/courses
	@RequestMapping(value = "/courses/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getCourses(@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException {

		if (token.length() == 0)
			return null;

		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/courses");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			res.add(response.toString());
		} else {
			System.out.println("GET NOT WORKED - url:GET|/api/v1/courses due to " + responseCode);
		}
		return res;
	}

	// get ALL rubrics under certain course
	// calling url:GET|/v1/courses/{course_id}/rubrics
	@RequestMapping(value = "courses/{cid}/rubrics/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getRubrics(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token) throws IOException {

		if (token.length() == 0)
			return null;
		int pageNum = 1;
		StringBuilder sb = new StringBuilder();
		List<String> res = new ArrayList<>();
		while (pageNum >= 1 && pageNum < 10) // for now I set limitation at most we can have 500 rubrics
		{
			URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/courses/" + cid
					+ "/rubrics?page=" + pageNum + "&per_page=50");
			String readLine = null;
			HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					response.append(readLine);
				}
				in.close();
				// empty array -- means no data in this page:
				if (response.toString().equals("[]")) {
					sb.deleteCharAt(sb.length() - 1); // remove ,
					sb.append("]");
					break;
				}
				// if it's not the first page, we shall remove front bracket
				if (pageNum != 1) {
					response.deleteCharAt(0); // remove [
				}
				// no matter which page we are, we shall remove ] and changed to ,
				response.deleteCharAt(response.length() - 1);
				response.append(",");

				sb.append(response.toString());
				pageNum++;
			} else {
				System.out.println("GET NOT WORKED - /v1/courses/{course_id}/rubrics due to " + responseCode);
			}
		}
		res.add(sb.toString());
		return res;
	}

	// Get certain rubric under certain course and import it to our db
	// calling url:GET|/v1/courses/{course_id}/rubrics/{id}
	@PostMapping("/courses/{cid}/rubrics/{rid}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long importRubric(@PathVariable long cid, @PathVariable long rid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException {

		if (token.length() == 0)
			return (long) -1;

		URL urlForGetRequest = new URL(
				"https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/rubrics/" + rid);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		Rubric rubric = new Rubric();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();

			// create rubric
			JSONParser parser = new JSONParser();
			JSONObject rubricJson = (JSONObject) parser.parse(response.toString());
			String rubric_name = rubricJson.get("title").toString();
			// first check if this rubric is imported before ...
			String rubric_extid = rubricJson.get("id").toString();
			long duplId = canvasDao.checkRubricExists(EXTSOURCE, rubric_extid); // the id which has this imported rubric
			if (duplId > -1) // -1 means never import
				return duplId;
			else {
				rubric.setName(rubric_name);
				rubric.setExternalSource(EXTSOURCE);
				rubric.setExternalId(rubric_extid);
				// here is to get current user id
				// then we can uncomment below
//				rubric.setCreator(ID); //I think creator type should be also extUserId and extSource? 
				rubric = rubricDao.saveRubric(rubric);
				List<Criterion> criteria = rubric.getCriteria();
				// create criteria under rubric
				JSONArray criteriaArray = (JSONArray) rubricJson.get("data");
				for (int i = 0; i < criteriaArray.size(); i++) {
					JSONObject criterionJson = (JSONObject) parser.parse(criteriaArray.get(i).toString());
					String criterion_name = criterionJson.get("description").toString();
					String criterion_desc = criterionJson.get("long_description").toString();
					Criterion criterion = new Criterion();
					// criterion inside rubric is ok to be duplicated, so no need to set extsource
					// and extid here
					criterion.setName(criterion_name);
					criterion.setDescription(criterion_desc);
					criterion.setReusable(false);
					criterion = criterionDao.saveCriterion(criterion);
					criteria.add(criterion);

					// create ratings under criterion
					JSONArray ratingsArray = (JSONArray) criterionJson.get("ratings");
					for (int j = 0; j < ratingsArray.size(); j++) {
						JSONObject ratingJson = (JSONObject) parser.parse(ratingsArray.get(j).toString());
						String rating_desc = ratingJson.get("description").toString();
						double rating_value = Double.parseDouble(ratingJson.get("points").toString());
						Rating rating = new Rating();
						rating.setCriterion(criterion);
						rating.setDescription(rating_desc);
						rating.setValue(rating_value);

						rating = criterionDao.saveRating(rating);
					}
				}
			}

		} else {
			System.out.println("GET NOT WORKED - /v1/courses/{course_id}/rubrics/{id} due to " + responseCode);
			return (long) -1;
		}

		return rubric.getId();
	}

	// get ALL outcomes under certain course
	// GET /v1/courses/{course_id}/outcome_group_links
	@RequestMapping(value = "/courses/{cid}/criteria/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getCriteria(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token) throws IOException {
		if (token.length() == 0)
			return null;
		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL(
				"https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/outcome_group_links");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			res.add(response.toString());
		} else {
			System.out.println("GET NOT WORKED - /v1/courses/{course_id}/outcome_group_links due to " + responseCode);
		}
		return res;
	}

	// Get certain criterion under certain course and import it to our db
	// GET /api/v1/outcomes/:id
	@PostMapping("/criterion/{id}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long importCriterion(@PathVariable long id,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException {

		// check if contains Canvas Token
		if (token.length() == 0)
			return (long) -1;

		// get certain canvas outcome
		URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/outcomes/" + id);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		Criterion criterion = new Criterion();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();
			// create criterion
			JSONParser parser = new JSONParser();
			JSONObject criterionJson = (JSONObject) parser.parse(response.toString());
			String criterion_name = criterionJson.get("title").toString();
			String criterion_desc = criterionJson.get("description").toString();
			// first check if we import this criterion before:
			String criterion_extid = criterionJson.get("id").toString();
			long duplId = canvasDao.checkCriterionExists(EXTSOURCE, criterion_extid); // the id which has this imported
																						// c
			if (duplId > -1)
				return duplId;
			else {
				criterion.setName(criterion_name);
				criterion.setDescription(criterion_desc);
				criterion.setExternalId(criterion_extid);
				criterion.setExternalSource(EXTSOURCE);
				criterion.setReusable(true); // since the outcome we can import from Canvas is definitely reusable
				criterion = criterionDao.saveCriterion(criterion);
				// create ratings under criterion
				JSONArray ratingsArray = (JSONArray) criterionJson.get("ratings");
				for (int j = 0; j < ratingsArray.size(); j++) {
					JSONObject ratingJson = (JSONObject) parser.parse(ratingsArray.get(j).toString());
					String rating_desc = ratingJson.get("description").toString();
					double rating_value = Double.parseDouble(ratingJson.get("points").toString());
					Rating rating = new Rating();
					rating.setCriterion(criterion);
					rating.setDescription(rating_desc);
					rating.setValue(rating_value);

					rating = criterionDao.saveRating(rating);
				}
			}

		} else {
			System.out.println("GET NOT WORKED - /api/v1/outcomes/:id due to " + responseCode);
		}

		return criterion.getId();
	}

	// get criterion Id from RubricService, and the course Id, then convert it to
	// JSON object then push it into Canvas DB
	// url:POST|/api/v1/courses/:course_id/outcome_groups/:id/outcomes
	@PostMapping("/criterion/{id}/export/course/{courseId}/outcome_groups/{outcome_group_Id}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public void exportCriterion(@PathVariable long courseId, @PathVariable long outcome_group_Id, @PathVariable long id,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException {

		if (token.length() == 0)
			return;

		// 1. convert Criterion to JSON Object that we planning to export to Canvas
		Criterion c = criterionDao.getCriterion(id);
		JSONObject outcome = new JSONObject();
		outcome.put("title", c.getName());
		outcome.put("description", c.getDescription());

		JSONArray ratings = new JSONArray();

		for (Rating r : c.getRatings()) {
			JSONObject rating = new JSONObject();
			rating.put("description", r.getDescription());
			rating.put("points", r.getValue());
			ratings.add(rating);
		}

		outcome.put("ratings", ratings);

		// 2. use url:POST|/api/v1/courses/:course_id/outcome_groups/:id/outcomes to ADD
		// OUTCOME to Canvas
		try {
			URL url = new URL("https://calstatela.instructure.com:443/api/v1/courses/" + courseId + "/outcome_groups/"
					+ outcome_group_Id + "/outcomes");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			String jsonInputString = outcome.toString();

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed to export Outcome: HTTP error code : " + conn.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// return outcome groups under certain course
	// calling url:GET|/api/v1/courses/:course_id/outcome_groups
	@RequestMapping(value = "/courses/{cid}/outcome_groups/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getOutcome_Groups(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException {
		if (token.length() == 0)
			return null;

		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL(
				"https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/outcome_groups");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			res.add(response.toString());
		} else {
			System.out.println(
					"GET NOT WORKED - url:GET|/api/v1/courses/:course_id/outcome_groups due to " + responseCode);
		}
		return res;
	}

	// get rubric Id from RubricService, and the course Id, then convert it to JSON
	// object then push it into Canvas DB
	// url:POST|/api/v1/courses/:course_id/rubrics
	@PostMapping("/rubric/{id}/export/course/{courseId}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public void exportRubric(@PathVariable long courseId, @PathVariable long id,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException {
		if (token.length() == 0)
			return;

		// 1. convert Rubric to JSON Object that we planning to export to Canvas
		Rubric r = rubricDao.getRubric(id);
		JSONObject object = new JSONObject();

		JSONObject rubric = new JSONObject();
		rubric.put("title", r.getName());
		JSONObject criteria = new JSONObject(); // is a HASHMAP, not ARRAY!
		int criteria_index = 0;
		for (Criterion c : r.getCriteria()) {
			JSONObject criterion = new JSONObject();
			criterion.put("description", c.getName());
			// Rubric on Canvas doesn't support html format, so we remove html tags in
			// criterion description
			String desc = c.getDescription();
			desc = desc.replaceAll("\\<.*?\\>", "");
			criterion.put("long_description", desc);
			int rating_index = 0;
			JSONObject ratings = new JSONObject(); // another HASHMAP
			for (Rating rt : c.getRatings()) {
				JSONObject rating = new JSONObject();
				rating.put("description", rt.getDescription());
				rating.put("points", rt.getValue());
				ratings.put(String.valueOf(rating_index++), rating);
			}
			criterion.put("ratings", ratings);
			criteria.put(String.valueOf(criteria_index++), criterion);
		}
		rubric.put("criteria", criteria);

		// 2. link the rubric with RubricAssociation
		JSONObject rubric_association = new JSONObject();
		rubric_association.put("association_id", courseId);
		rubric_association.put("association_type", "Course");

		// 3. link Rubric obj and RubricAssociation into request object
		object.put("rubric", rubric);
		object.put("rubric_association", rubric_association);

		// 4. use url:POST|/api/v1/courses/:course_id/rubrics to add rubric in Canvas
		try {
			URL url = new URL("https://calstatela.instructure.com:443/api/v1/courses/" + courseId + "/rubrics");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			String jsonInputString = object.toString();

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed to export rubric: HTTP error code : " + conn.getResponseCode());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// get all assignments from certain course
	// url:GET|/api/v1/courses/:course_id/assignments
	@RequestMapping(value = "courses/{cid}/assignments/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getAssignments(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token) throws IOException {

		if (token.length() == 0)
			return null;

		int pageNum = 1;
		StringBuilder sb = new StringBuilder();
		List<String> res = new ArrayList<>();
		while (pageNum >= 1 && pageNum < 10) // for now I set limitation at most we can have 500 assignments
		{
			URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/courses/" + cid
					+ "/assignments?page=" + pageNum + "&per_page=50");
			String readLine = null;
			HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

			connection.setRequestProperty("Authorization", "Bearer " + token);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestMethod("GET");
			int responseCode = connection.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuffer response = new StringBuffer();
				while ((readLine = in.readLine()) != null) {
					response.append(readLine);
				}
				in.close();
				// empty array -- means no data in this page:
				if (response.toString().equals("[]")) {
					sb.deleteCharAt(sb.length() - 1); // remove ,
					sb.append("]");
					break;
				}
				// if it's not the first page, we shall remove front bracket
				if (pageNum != 1) {
					response.deleteCharAt(0); // remove [
				}
				// no matter which page we are, we shall remove ] and changed to ,
				response.deleteCharAt(response.length() - 1);
				response.append(",");

				sb.append(response.toString());
				pageNum++;
			} else {
				System.out.println("GET NOT WORKED - /v1/courses/{course_id}/assignments due to " + responseCode);
			}
		}
		res.add(sb.toString());
		return res;
	}

	// get all assessments from certain course, assignment, rubric
	// url:GET|/api/v1/courses/:course_id/rubrics/:rubric_id?include[]=assessments&style=full
	@PostMapping("/courses/{cid}/assignments/{aid}/rubrics/{rid}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public void importAssessments(@PathVariable long cid, @PathVariable long rid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException 
	{

		if (token.length() == 0)
			return;

		URL urlForGetRequest = new URL(
				"https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/rubrics/" + rid+"?include[]=assessments&style=full");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();
		Rubric rubric = new Rubric();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer response = new StringBuffer();
			while ((readLine = in.readLine()) != null) {
				response.append(readLine);
			}
			in.close();

			// create rubric
			JSONParser parser = new JSONParser();
			JSONObject rubricJson = (JSONObject) parser.parse(response.toString());
			String rubric_name = rubricJson.get("title").toString();

			rubric.setName(rubric_name);
			rubric.setExternalSource(EXTSOURCE);
			// here is to get current user id
			// then we can uncomment below
//				rubric.setCreator(ID); //I think creator type should be also extUserId and extSource? 
			rubric = rubricDao.saveRubric(rubric);
			List<Criterion> criteria = rubric.getCriteria();
			// create criteria under rubric
			JSONArray criteriaArray = (JSONArray) rubricJson.get("data");
			for (int i = 0; i < criteriaArray.size(); i++) {
				JSONObject criterionJson = (JSONObject) parser.parse(criteriaArray.get(i).toString());
				String criterion_name = criterionJson.get("description").toString();
				String criterion_desc = criterionJson.get("long_description").toString();
				Criterion criterion = new Criterion();
				// criterion inside rubric is ok to be duplicated, so no need to set extsource
				// and extid here
				criterion.setName(criterion_name);
				criterion.setDescription(criterion_desc);
				criterion.setReusable(false);
				criterion = criterionDao.saveCriterion(criterion);
				criteria.add(criterion);

				// create ratings under criterion
				JSONArray ratingsArray = (JSONArray) criterionJson.get("ratings");
				for (int j = 0; j < ratingsArray.size(); j++) {
					JSONObject ratingJson = (JSONObject) parser.parse(ratingsArray.get(j).toString());
					String rating_desc = ratingJson.get("description").toString();
					double rating_value = Double.parseDouble(ratingJson.get("points").toString());
					Rating rating = new Rating();
					rating.setCriterion(criterion);
					rating.setDescription(rating_desc);
					rating.setValue(rating_value);

					rating = criterionDao.saveRating(rating);
				}

			}

		} else {
			System.out.println("GET NOT WORKED - /v1/courses/{course_id}/rubrics/{id} due to " + responseCode);
		}

	}

}
