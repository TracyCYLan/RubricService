package edu.csula.rubrics.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import edu.csula.rubrics.models.Artifact;
import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.AssessmentGroup;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.ArtifactDao;
import edu.csula.rubrics.models.dao.AssessmentDao;
import edu.csula.rubrics.models.dao.RubricDao;
import edu.csula.rubrics.models.dao.UserDao;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/assessment")
public class AssessmentController {

	@Autowired
	AssessmentDao assessmentDao;

	@Autowired
	UserDao userDao;

	@Autowired
	ArtifactDao artifactDao;

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

	// get certain evaluation
	@GetMapping("/{id}")
	public Assessment getAssessment(@PathVariable Long id) {
		Assessment assessment = assessmentDao.getAssessment(id);
		if (assessment == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Assessment not found");
		return assessment;
	}

	// get assessmentGroup
	@GetMapping("/assessmentgroup")
	public List<AssessmentGroup> getAssessmentGroups(ModelMap models) {
		return assessmentDao.getAssessmentGroups();
	}

	@GetMapping("/assessmentgroup/{id}")
	public AssessmentGroup getAssessmentGroup(@PathVariable Long id) {
		AssessmentGroup ag = assessmentDao.getAssessmentGroup(id);
		if (ag == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "AssessmentGroup not found");
		return ag;
	}

	@GetMapping("/rubric/{id}/assessmentgroup")
	// return List<AssessmentGroup> using same Rubric
	public List<AssessmentGroup> getAssessmentGroupsByRubric(@PathVariable Long id) {
		return assessmentDao.getAssessmentGroupsByRubric(id);
	}

	@GetMapping("/assessmentgroup/search/{text}")
	public List<AssessmentGroup> searchAssessmentGroups(@RequestParam String text) {
		List<AssessmentGroup> groups = null;
		if (text != null)
			groups = assessmentDao.searchAssessmentGroups(text);

		return groups;
	}

	// view file ----
	@GetMapping("/artifact/{id}")
	public String readFile(@PathVariable Long id) throws IOException {
		Artifact artifact = artifactDao.getArtifact(id);
		String fileName = artifact.getName();
		String path = readProp("canvas.downloadpath") + artifact.getPath() + "\\" + fileName;
		// read file and return text to web page
		File file = new File(path);
		BufferedReader br = new BufferedReader(new FileReader(file));
		StringBuilder sb = new StringBuilder();
		String s;
		while ((s = br.readLine()) != null) {
			sb.append(s);
			sb.append("\n");
		}

		return sb.toString();
	}

	// download file to user local ---
	@GetMapping("/artifact/{id}/download")
	public ResponseEntity<byte[] > download(@PathVariable Long id) throws IOException {
		Artifact artifact = artifactDao.getArtifact(id);
		String fileName = artifact.getName();
		String filePath = readProp("canvas.downloadpath") + artifact.getPath() + "\\" + fileName;
		String contentType = artifact.getContentType();

		File file = new File(filePath);
//--------
		
		Path path = Paths.get(filePath);
		byte[] data = Files.readAllBytes(path);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentDispositionFormData("attachment", fileName);
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		return new ResponseEntity<>(data, headers, HttpStatus.OK);
//		ByteArrayResource resource = new ByteArrayResource(data);
//
//		return ResponseEntity.ok()
//				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
//				.contentType(MediaType.parseMediaType(contentType)).contentLength(data.length).body(resource);
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
