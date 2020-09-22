package edu.csula.rubrics.canvas;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.csula.rubrics.models.Artifact;
import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.AssessmentGroup;
import edu.csula.rubrics.models.Comment;
import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.External;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.ArtifactDao;
import edu.csula.rubrics.models.dao.AssessmentDao;
import edu.csula.rubrics.models.dao.CriterionDao;
import edu.csula.rubrics.models.dao.ExternalDao;
import edu.csula.rubrics.models.dao.RubricDao;
import edu.csula.rubrics.models.dao.UserDao;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/canvas")
public class CanvasRestController {

	@Autowired
	RubricDao rubricDao;

	@Autowired
	CriterionDao criterionDao;

	@Autowired
	ExternalDao externalDao;

	@Autowired
	AssessmentDao assessmentDao;

	@Autowired
	ArtifactDao artifactDao;

	@Autowired
	UserDao userDao;

	final String EXTSOURCE = "Canvas";

	private String readProp(String name) {
		String url = "";
		try (InputStream input = new FileInputStream("src/main/resources/application.properties")) {
			Properties prop = new Properties();
			prop.load(input);
			url = prop.getProperty(name);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return url;
	}

	// get sub from access_token
	private String getSub(HttpServletRequest request) throws ParseException {
		if (request.getHeader("Authorization") == null || request.getHeader("Authorization").length() == 0)
			return "";
		String token = request.getHeader("Authorization").split(" ")[1]; // get jwt from header
		String encodedPayload = token.split("\\.")[1]; // get second encoded part in jwt
		Base64 base64Url = new Base64(true);
		String payload = new String(base64Url.decode(encodedPayload));

		JSONParser parser = new JSONParser();
		JSONObject claimsObj = (JSONObject) parser.parse(payload);
		return claimsObj.get("sub").toString();
	}

	// get ALL courses via given canvasToken
	// calling url:GET|/api/v1/courses
	@RequestMapping(value = "/course/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getCourses(@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException {

		if (token.length() == 0)
			return null;

		List<String> res = new ArrayList<>();
		String canvasURL = readProp("canvas.url") + "api/v1/";
		URL urlForGetRequest = new URL(canvasURL + "courses");
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
			throw new AccessDeniedException("403 returned");
		}
		return res;
	}

	// get ALL rubrics under certain course
	// calling url:GET|/v1/courses/{course_id}/rubrics
	@RequestMapping(value = "course/{cid}/rubric/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getRubrics(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token) throws IOException {

		if (token.length() == 0)
			return null;
		int pageNum = 1;
		StringBuilder sb = new StringBuilder();
		List<String> res = new ArrayList<>();
		String canvasURL = readProp("canvas.url") + "api/v1/";
		while (pageNum >= 1 && pageNum < 10) // for now I set limitation at most we can have 500 rubrics
		{
			URL urlForGetRequest = new URL(canvasURL + "courses/" + cid + "/rubrics?page=" + pageNum + "&per_page=50");
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
				throw new AccessDeniedException("403 returned");
			}
		}
		res.add(sb.toString());
		return res;
	}

	// Get certain rubric under certain course and import it to our db
	// calling url:GET|/v1/courses/{course_id}/rubrics/{id}
	@PostMapping("/course/{cid}/rubric/{rid}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long importRubric(@PathVariable long cid, @PathVariable long rid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token, HttpServletRequest request)
			throws IOException, ParseException {

		String sub = getSub(request);
		if (token.length() == 0 || sub == null || sub.length() == 0)
			throw new AccessDeniedException("403 returned");

		User user = userDao.getUserBySub(sub);
		if (user == null) {
			user = new User();
			user.setSub(sub);
			user = userDao.saveUser(user);
		}

		String canvasURL = readProp("canvas.url") + "api/v1/";
		URL urlForGetRequest = new URL(canvasURL + "courses/" + cid + "/rubrics/" + rid);

		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();

		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			System.out.println("GET NOT WORKED - /v1/courses/{course_id}/rubrics/{id} due to " + responseCode);
			throw new AccessDeniedException("403 returned");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer response = new StringBuffer();
		while ((readLine = in.readLine()) != null) {
			response.append(readLine);
		}
		in.close();

		return importRubricHelper(response.toString(), user);

	}

	private long importRubricHelper(String response, User user) throws ParseException {

		// 1. create rubric
		Rubric rubric = new Rubric();
		JSONParser parser = new JSONParser();
		JSONObject rubricJson = (JSONObject) parser.parse(response);
		// before create, first check if this rubric is imported before ...
		String rubric_extid = rubricJson.get("id").toString();
		long duplId = externalDao.checkExists(EXTSOURCE, rubric_extid, "rubric"); // the rubric that imported this
																					// rubric before
		if (duplId > -1) // -1 means never import
			return duplId;

		// automatically publish the rubric while import it.
		String rubric_name = rubricJson.get("title").toString();
		rubric.setName(rubric_name);
		rubric.setPublishDate(Calendar.getInstance());
		rubric.setCreator(user);
		rubric = rubricDao.saveRubric(rubric);

		// 2. bind rubric with ID in external source (Canvas in this case)
		External externalr = new External(EXTSOURCE, rubric_extid, "rubric");
		externalr.setRubric(rubric);
		externalr = externalDao.saveExternal(externalr);
		rubric.getExternals().add(externalr);

		// 3. create criteria under rubric
		List<Criterion> criteria = rubric.getCriteria();
		JSONArray criteriaArray = (JSONArray) rubricJson.get("data");
		for (int i = 0; i < criteriaArray.size(); i++) {
			JSONObject criterionJson = (JSONObject) parser.parse(criteriaArray.get(i).toString());
			String criterion_name = criterionJson.get("description").toString();
			String criterion_desc = criterionJson.get("long_description").toString();
			String criterion_extid = criterionJson.get("id").toString();
			Criterion criterion = new Criterion();
			criterion.setCreator(user);
			criterion.setName(criterion_name);
			criterion.setDescription(criterion_desc);
			criterion.setReusable(false);
			criterion = criterionDao.saveCriterion(criterion);
			criteria.add(criterion);
			// 4. bind outcome with ID in external source (Canvas in this case)
			External externalc = new External(EXTSOURCE, criterion_extid, "criterion");
			externalc.setCriterion(criterion);
			externalc = externalDao.saveExternal(externalc);
			criterion.getExternals().add(externalc);

			// 5. create ratings under criterion
			JSONArray ratingsArray = (JSONArray) criterionJson.get("ratings");
			List<Rating> ratings = new ArrayList<>();
			for (int j = 0; j < ratingsArray.size(); j++) {
				JSONObject ratingJson = (JSONObject) parser.parse(ratingsArray.get(j).toString());
				String rating_desc = ratingJson.get("description").toString();
				double rating_value = Double.parseDouble(ratingJson.get("points").toString());
				Rating rating = new Rating();
				rating.setCriterion(criterion);
				rating.setDescription(rating_desc);
				rating.setValue(rating_value);
				rating = criterionDao.saveRating(rating);
				ratings.add(rating);
			}
			criterion.setRatings(ratings);
		}

		return rubric.getId();
	}

	// get ALL outcomes under certain course
	// GET /v1/courses/{course_id}/outcome_group_links
	@RequestMapping(value = "/course/{cid}/criterion/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getCriteria(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token) throws IOException {
		if (token.length() == 0)
			return null;
		List<String> res = new ArrayList<>();
		String canvasURL = readProp("canvas.url") + "api/v1/";
		URL urlForGetRequest = new URL(canvasURL + "courses/" + cid + "/outcome_group_links");
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
			throw new AccessDeniedException("403 returned");
		}
		return res;
	}

	// Get certain criterion under certain course and import it to our db
	// GET /api/v1/outcomes/:id
	@PostMapping("/criterion/{id}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public Long importCriterion(@PathVariable long id,
			@RequestParam(value = "token", required = true, defaultValue = "") String token, HttpServletRequest request)
			throws IOException, ParseException {

		String sub = getSub(request);
		if (token.length() == 0 || sub == null || sub.length() == 0)
			throw new AccessDeniedException("403 returned");

		User user = userDao.getUserBySub(sub);
		if (user == null) {
			user = new User();
			user.setSub(sub);
			user = userDao.saveUser(user);
		}

		// get certain canvas outcome
		String canvasURL = readProp("canvas.url") + "api/v1/";
		URL urlForGetRequest = new URL(canvasURL + "outcomes/" + id);
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
			long duplId = externalDao.checkExists(EXTSOURCE, criterion_extid, "criterion"); // id of existed outcome in
																							// RS
			if (duplId > -1)
				return duplId;
			else {
				criterion.setName(criterion_name);
				criterion.setDescription(criterion_desc);
				criterion.setCreator(user);
				criterion.setReusable(true); // since the outcome we can import from Canvas is definitely reusable
				criterion = criterionDao.saveCriterion(criterion);

				External external = new External(EXTSOURCE, criterion_extid, "criterion");
				external.setCriterion(criterion);
				external = externalDao.saveExternal(external);
				criterion.getExternals().add(external);

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
			throw new AccessDeniedException("403 returned");
		}

		return criterion.getId();
	}

	// get criterion Id from RubricService, and the course Id, then convert it to
	// JSON object then push it into Canvas DB
	// url:POST|/api/v1/courses/:course_id/outcome_groups/:id/outcomes
	@PostMapping("/criterion/{id}/export/course/{courseId}/outcome_group/{outcome_group_Id}/{token}")
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
		String canvasURL = readProp("canvas.url") + "api/v1/";
		try {
			URL url = new URL(canvasURL + "courses/" + courseId + "/outcome_groups/" + outcome_group_Id + "/outcomes");
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

			// 3. get Response Body and record the external outcome ID into DB
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				// convert response body to JSONObject
				JSONParser parser = new JSONParser();
				JSONObject responseJson = (JSONObject) parser.parse(response.toString());
				JSONObject outcomeJson = (JSONObject) responseJson.get("outcome");
				String criterion_extid = outcomeJson.get("id").toString();

				// bind export outcome's ID with criterionid
				External external = new External(EXTSOURCE, criterion_extid, "criterion");
				external.setCriterion(c);
				external = externalDao.saveExternal(external);
				c.getExternals().add(external);
				c = criterionDao.saveCriterion(c);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// return outcome groups under certain course
	// calling url:GET|/api/v1/courses/:course_id/outcome_groups
	@RequestMapping(value = "/course/{cid}/outcome_group/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getOutcome_Groups(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token)
			throws IOException, ParseException {
		if (token.length() == 0)
			return null;

		String canvasURL = readProp("canvas.url") + "api/v1/";

		List<String> res = new ArrayList<>();
		URL urlForGetRequest = new URL(canvasURL + "courses/" + cid + "/outcome_groups");
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
			throw new AccessDeniedException("403 returned");
		}
		return res;
	}

	// get rubric Id from RubricService, and the course Id, then convert it to JSON
	// object then push it into Canvas DB
	// url:POST|/api/v1/courses/:course_id/rubrics
	@PostMapping("/rubric/{id}/export/course/{courseId}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public void exportRubric(@PathVariable long courseId, @PathVariable long id,
			@RequestParam(value = "token", required = true, defaultValue = "") String token,
			@RequestBody Map<String, String> assignmentInfo) throws IOException, ParseException {
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
		String canvasURL = readProp("canvas.url") + "api/v1/";
		String rubric_extid = "";
		try {
			URL url = new URL(canvasURL + "courses/" + courseId + "/rubrics");
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

			// 5. get Response Body and record the external rubric ID and criteria IDs into
			// DB
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				// convert response body to JSONObject
				JSONParser parser = new JSONParser();
				JSONObject responseJson = (JSONObject) parser.parse(response.toString());
				JSONObject rubricJson = (JSONObject) responseJson.get("rubric");
				rubric_extid = rubricJson.get("id").toString();

				// bind export rubric's ID with rubric on Rubric Service (also criteria under
				// rubric)
				External external = new External(EXTSOURCE, rubric_extid, "rubric");
				external.setRubric(r);
				external = externalDao.saveExternal(external);
				r.getExternals().add(external);
				r = rubricDao.saveRubric(r);
				List<Criterion> rscriteria = r.getCriteria(); // criteria array under Rubric on RS
				JSONArray criteriaArray = (JSONArray) rubricJson.get("data");
				for (int i = 0; i < criteriaArray.size(); i++) {
					JSONObject criterionJson = (JSONObject) parser.parse(criteriaArray.get(i).toString());
					String cid = criterionJson.get("id").toString();
					Criterion rscriterion = rscriteria.get(i); // the criterion under Rubric on Rubric Service
					External externalc = new External(EXTSOURCE, cid, "criterion");
					externalc.setCriterion(rscriterion);
					externalc = externalDao.saveExternal(externalc);
					rscriterion.getExternals().add(externalc);
					rscriterion = criterionDao.saveCriterion(rscriterion);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AccessDeniedException("403 returned");
		}
		// 6. create an assignment if needed
		String assignmentName = assignmentInfo.getOrDefault("name", "");
		String ext_assignmentId = assignmentInfo.getOrDefault("id", "");
		
		//means no need to bind rubric with any assignment at all
		if (assignmentName.length() == 0 && ext_assignmentId.length() == 0)
			return; 
		
		if(assignmentName.length()>0) //i.e., assignmentId is empty
		{
			try {
				ext_assignmentId = createAssignment(canvasURL, courseId, assignmentName, token);
			} catch (Exception e) {
				e.printStackTrace();
				throw new AccessDeniedException("403 returned");
			}
		}
		// 7. after getting assignment id, we also need to bind rubric and assignment
		try {
			bindAssignmentAndRubric(canvasURL, courseId, rubric_extid, ext_assignmentId, token);
		} catch (Exception e) {
			e.printStackTrace();
			throw new AccessDeniedException("403 returned");
		}
	}

	// calling url:POST|/api/v1/courses/:course_id/assignments
	private String createAssignment(String canvasURL, long courseId, String assignmentName, String token) {
		try {
			URL url = new URL(canvasURL + "courses/" + courseId + "/assignments");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			JSONObject object = new JSONObject();
			JSONObject assignment = new JSONObject();
			assignment.put("name", assignmentName);
			object.put("assignment", assignment);
			String jsonInputString = object.toString();

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK && conn.getResponseCode() != HttpURLConnection.HTTP_CREATED ) {
				throw new RuntimeException("Failed to create assignment: HTTP error code : " + conn.getResponseCode());
			}

			// 5. get Response Body and return the external assignment ID
			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
				StringBuilder response = new StringBuilder();
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					response.append(responseLine.trim());
				}
				// convert response body to JSONObject
				JSONParser parser = new JSONParser();
				JSONObject responseJson = (JSONObject) parser.parse(response.toString());
				return responseJson.get("id").toString();
			} catch (Exception e) {
				System.out.println("failed to get response when creating assignment: "+e.getMessage());
				throw new AccessDeniedException("403 returned");
			}

		} catch (Exception e) {
			System.out.println("failde to do POST|/api/v1/courses/:course_id/assignments: "+e.getMessage());
			throw new AccessDeniedException("403 returned");
		}
	}

	// calling url:POST|/api/v1/courses/:course_id/rubric_associations 
	private void bindAssignmentAndRubric(String canvasURL, long courseId, String rubricId, String assId, String token) {
		try {
			URL url = new URL(canvasURL + "courses/" + courseId + "/rubric_associations");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Bearer " + token);
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			
			JSONObject object = new JSONObject();
			JSONObject association = new JSONObject();
			association.put("rubric_id", Integer.valueOf(rubricId));
			association.put("association_id", Integer.valueOf(assId));
			association.put("association_type", "Assignment");
			association.put("purpose", "grading");
			object.put("rubric_association", association);
			String jsonInputString = object.toString();

			OutputStream os = conn.getOutputStream();
			os.write(jsonInputString.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("Failed to bind assignment with rubric: HTTP error code : " + conn.getResponseCode());
			}

		}catch(Exception e) {
			System.out.println("POST|/api/v1/courses/:course_id/rubric_associations failed: "+e.getMessage());
			throw new AccessDeniedException("403 returned");
		}
	}
	// get all assignments from certain course
	// url:GET|/api/v1/courses/:course_id/assignments
	@RequestMapping(value = "course/{cid}/assignment/{token}", method = RequestMethod.GET, produces = "application/json")
	public List<String> getAssignments(@PathVariable long cid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token) throws IOException {

		if (token.length() == 0)
			return null;

		int pageNum = 1;
		StringBuilder sb = new StringBuilder();
		List<String> res = new ArrayList<>();
		String canvasURL = readProp("canvas.url") + "api/v1/";
		while (pageNum >= 1 && pageNum < 10) // for now I set limitation at most we can have 500 assignments
		{
			URL urlForGetRequest = new URL(
					canvasURL + "courses/" + cid + "/assignments?page=" + pageNum + "&per_page=50");
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
				if (sb.length() > 0 && response.toString().equals("[]")) {
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
				throw new AccessDeniedException("403 returned");
			}
		}
		res.add(sb.toString());
		return res;
	}

	// get all assessments from certain course, assignment, rubric
	// url:GET|/api/v1/courses/:course_id/rubrics/:rubric_id?include[]=assessments&style=full
	@PostMapping("/course/{cid}/assignment/{assignmentId}/rubric/{rid}/{token}")
	@ResponseStatus(HttpStatus.CREATED)
	public void importAssessments(@PathVariable long cid, @PathVariable String assignmentId, @PathVariable long rid,
			@RequestParam(value = "token", required = true, defaultValue = "") String token,
			@RequestBody Map<String, Object> assessmentGroupInfo, HttpServletRequest request)
			throws IOException, ParseException {
		String sub = getSub(request);
		if (token.length() == 0 || sub == null || sub.length() == 0)
			throw new AccessDeniedException("403 returned");

		User user = userDao.getUserBySub(sub);
		if (user == null) {
			user = new User();
			user.setSub(sub);
			user = userDao.saveUser(user);
		}

		// 1. call API to get rubric with assessments
		String canvasURL = readProp("canvas.url") + "api/v1/";

		URL urlForGetRequest = new URL(
				canvasURL + "courses/" + cid + "/rubrics/" + rid + "?include[]=assessments&style=full");
		String readLine = null;
		HttpURLConnection connection = (HttpURLConnection) urlForGetRequest.openConnection();
		connection.setRequestProperty("Authorization", "Bearer " + token);
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestMethod("GET");
		int responseCode = connection.getResponseCode();

		if (responseCode != HttpURLConnection.HTTP_OK) {
			System.out.println(
					"GET NOT WORKED - /v1/courses/{course_id}/rubrics/{id}?include[]=assessments&style=full due to "
							+ responseCode);
			throw new AccessDeniedException("403 returned");
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuffer response = new StringBuffer();
		while ((readLine = in.readLine()) != null) {
			response.append(readLine);
		}
		in.close();
		// 2. import Rubric and Criterion, Ratings under it if needed
		Rubric rubric = rubricDao.getRubric(importRubricHelper(response.toString(), user));
		// 3. create AssessmentGroup
		AssessmentGroup assessmentGroup = new AssessmentGroup();
		assessmentGroup.setRubric(rubric);
		assessmentGroup.setCreator(user);
		// update assessmentgroup information
		for (String key : assessmentGroupInfo.keySet()) {
			switch (key) {
			case "name":
				assessmentGroup.setName((String) assessmentGroupInfo.get(key));
				break;
			case "description":
				assessmentGroup.setDescription((String) assessmentGroupInfo.get(key));
				break;
			case "assessDate":
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String dateInString = (String) assessmentGroupInfo.get(key);
					if (dateInString == null || dateInString.length() == 0) {
						assessmentGroup.setAssessDate(null);
					} else {
						Date date = sdf.parse(dateInString);
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(date);
						assessmentGroup.setAssessDate(calendar);
					}
				} catch (Exception e) {
					System.out.println(e.getMessage());
				} finally {
					break;
				}
			default:
			}
		}
		assessmentGroup = assessmentDao.saveAssessmentGroup(assessmentGroup);

		// 4. get Assessments from JSON
		JSONParser parser = new JSONParser();
		JSONObject rubricJson = (JSONObject) parser.parse(response.toString());

		JSONArray assessmentsArray = (JSONArray) rubricJson.get("assessments");

		for (int i = 0; i < assessmentsArray.size(); i++) {
			JSONObject assessmentJson = (JSONObject) parser.parse(assessmentsArray.get(i).toString());
			JSONObject associationJson = (JSONObject) parser.parse(assessmentJson.get("rubric_association").toString());
			String association_id = associationJson.get("association_id").toString();
			String association_type = associationJson.get("association_type").toString();

			// ignore assessments that doesn't have the same required assignment id
			if (!association_type.equals("Assignment") || !association_id.equals(assignmentId))
				continue;

			// start to create Assessment ------
			Assessment assessment = new Assessment();
			assessment.setRubric(rubric);
			assessment.setAssessmentGroup(assessmentGroup);
			assessment.setType(assessmentJson.get("assessment_type").toString()); // peer_review or grading(i.e.,
																					// instructor evaluations)

			assessment = assessmentDao.saveAssessment(assessment);

			// get ratings and add it under this assessment
			JSONArray ratingsArray = (JSONArray) assessmentJson.get("data");
			List<Criterion> criteria = rubric.getCriteria();
			List<Comment> comments = new ArrayList<>();
			for (int j = 0; j < ratingsArray.size(); j++) {
				JSONObject ratingJson = (JSONObject) ratingsArray.get(j);
				Criterion criterion = criteria.get(j);
				for (Rating r : criterion.getRatings()) {
					double points = Double.parseDouble(ratingJson.get("points").toString());
					if (r.getValue() != points)
						continue;
					Comment comment = new Comment();
					comment.setContent(ratingJson.get("comments").toString());
					comment = assessmentDao.saveComment(comment);
					comment.setRating(r);
					comment.setAssessment(assessment);
					comments.add(comment);
					break;
				}
			}
			assessment.setComments(comments);
			assessment = assessmentDao.saveAssessment(assessment);

			// if artifact type is Submission, see if we can download files of assessment
			if (assessmentJson.get("artifact_type").toString().equals("Submission")) {
				String artifact_id = assessmentJson.get("artifact_id").toString();

				// call get Submissions of this assignment
				String sub_response = getSubmissions(canvasURL, cid, assignmentId, token);
				// find submission which has id == artifact_id
				if (sub_response.length() > 0) {
					JSONArray submissions = (JSONArray) parser.parse(sub_response);
					for (int j = 0; j < submissions.size(); j++) {
						JSONObject submission = (JSONObject) parser.parse(submissions.get(j).toString());
						if (submission.get("id").toString().equals(artifact_id)) {
							if (submission.get("attachments") != null) {
								JSONArray attachments = (JSONArray) parser
										.parse(submission.get("attachments").toString());
								List<Artifact> artifacts = new ArrayList<>();
								for (int k = 0; k < attachments.size(); k++) {
									JSONObject attachment = (JSONObject) parser.parse(attachments.get(k).toString());
									Artifact artifact = new Artifact();
									String downloadUrl = attachment.get("url").toString();
									String attId = attachment.get("id").toString();
									String fileName = attId + "-" + attachment.get("display_name").toString(); // filename
									artifact.setAssessment(assessment);
									artifact.setName(fileName);
									artifact.setPath(assessmentGroup.getId() + "");
									artifact.setType("Submission");
									artifact.setContentType(attachment.get("content-type").toString());
									if (downloadFile(downloadUrl, artifact) >= 0) {
										artifact = artifactDao.saveArtifact(artifact);
										artifacts.add(artifact);
									}
								}
								assessment.setArtifacts(artifacts);
							}
							break;
						}
					}
				}

			}

			// --- end creating assessment
			// add assessment into assessmentGroup
			assessmentGroup.getAssessments().add(assessment);
		}
	}

	// get submissions
	public String getSubmissions(String canvasURL, long cid, String assignmentId, String token) throws IOException {
		URL urlForGetRequest = new URL(canvasURL + "courses/" + cid + "/assignments/" + assignmentId + "/submissions");
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
			return response.toString();
		}
		System.out.println("url failed " + urlForGetRequest.toString());
		System.out.println(
				"GET NOT WORKED - url:GET|/api/v1/courses/:course_id/assignments/:assignment_id/submissions due to "
						+ responseCode);
		throw new AccessDeniedException("403 returned");

	}

	// using the given url to download the file
	public int downloadFile(String urlStr, Artifact artifact) {
		String path = readProp("canvas.downloadpath");
		String folder = artifact.getPath();
		String fileName = artifact.getName();
		try {
			URL url = new URL(urlStr);
			BufferedInputStream bis = new BufferedInputStream(url.openStream());
			FileOutputStream fis;
			Files.createDirectories(Paths.get(path + folder));// create folder if there's no folder
			fis = new FileOutputStream(path + folder + "\\" + fileName);
			byte[] buffer = new byte[1024];
			int count = 0;
			while ((count = bis.read(buffer, 0, 1024)) != -1) {
				fis.write(buffer, 0, count);
			}
			fis.close();
			bis.close();
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

	}

}
