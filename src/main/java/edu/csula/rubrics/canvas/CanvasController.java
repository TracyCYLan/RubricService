package edu.csula.rubrics.canvas;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
public class CanvasController {

	@Autowired
	RubricDao rubricDao;

	@Autowired
	CriterionDao criterionDao;

	@Autowired
	CanvasDao canvasDao;

	final String EXTSOURCE = "Canvas";

	// get ALL courses
	@RequestMapping(value = "/courses", method = RequestMethod.GET, produces = "application/json")
	public List<String> getCourses() throws IOException {
		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/courses");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		String token = "11590~VWZMtWiJtlWmE8St8vW8UBmQOoLpX0nhjSUMXZhbPC8eXNE5Pk63FuvNLzVRNYbh";

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
			System.out.println("GET NOT WORKED " + responseCode);
		}
		return res;
	}

	// get rubrics under certain course
	// GET /v1/courses/{course_id}/rubrics
	@RequestMapping(value = "/courses/{cid}/rubrics", method = RequestMethod.GET, produces = "application/json")
	public List<String> getRubrics(@PathVariable long cid) throws IOException {
		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/rubrics");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		String token = "11590~VWZMtWiJtlWmE8St8vW8UBmQOoLpX0nhjSUMXZhbPC8eXNE5Pk63FuvNLzVRNYbh";

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
			System.out.println("GET NOT WORKED " + responseCode);
		}
		return res;
	}

	// Get certain rubric under certain course and import it to our db
	// GET /v1/courses/{course_id}/rubrics/{id}
	@PostMapping("/courses/{cid}/rubrics/{rid}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long importRubric(@PathVariable long cid, @PathVariable long rid) throws IOException, ParseException {
		URL urlForGetRequest = new URL(
				"https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/rubrics/" + rid);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		String token = "11590~VWZMtWiJtlWmE8St8vW8UBmQOoLpX0nhjSUMXZhbPC8eXNE5Pk63FuvNLzVRNYbh";

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
			else 
			{
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
			System.out.println("GET NOT WORKED " + responseCode);
		}

		return rubric.getId();
	}

	// get rubrics under certain course
	// GET /v1/courses/{course_id}/outcome_group_links
	@RequestMapping(value = "/courses/{cid}/criteria", method = RequestMethod.GET, produces = "application/json")
	public List<String> getCriteria(@PathVariable long cid) throws IOException {
		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL(
				"https://calstatela.instructure.com:443/api/v1/courses/" + cid + "/outcome_group_links");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		String token = "11590~VWZMtWiJtlWmE8St8vW8UBmQOoLpX0nhjSUMXZhbPC8eXNE5Pk63FuvNLzVRNYbh";

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
			System.out.println("GET NOT WORKED " + responseCode);
		}
		return res;
	}

	// Get certain rubric under certain course and import it to our db
	// GET /api/v1/outcomes/:id
	@PostMapping("/criterion/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long importCriterion(@PathVariable long id) throws IOException, ParseException {
		URL urlForGetRequest = new URL("https://calstatela.instructure.com:443/api/v1/outcomes/" + id);
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		String token = "11590~VWZMtWiJtlWmE8St8vW8UBmQOoLpX0nhjSUMXZhbPC8eXNE5Pk63FuvNLzVRNYbh";

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
			long duplId = canvasDao.checkCriterionExists(EXTSOURCE, criterion_extid); // the id which has this imported c
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
			System.out.println("GET NOT WORKED " + responseCode);
		}

		return criterion.getId();
	}
}
