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

	private String readProp(String file, String name) {
		String url = "";
		try (InputStream input = new FileInputStream("src/main/resources/" + file)) {
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
		String path = readProp("application.properties", "canvas.downloadpath") + artifact.getPath() + "\\" + fileName;
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

	//if extension is not in the default file, means we need download the file instead just read it
	@GetMapping("/artifact/download/{extension}")
	public boolean checkDownloadNeeded(@PathVariable String extension) {
		String contentType = readProp("types.properties", extension.toLowerCase());
		return contentType==null;
	}
	// download file to user local ---
	@GetMapping("/artifact/{id}/download")
	public void download(@PathVariable Long id, HttpServletResponse response) throws IOException {
		Artifact artifact = artifactDao.getArtifact(id);
		String fileName = artifact.getName();
		String extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
		String contentType = readProp("types.properties", extension.toLowerCase());
		if (contentType == null)
			contentType = artifact.getContentType();
		String filePath = readProp("application.properties", "canvas.downloadpath") + artifact.getPath() + "\\"
				+ fileName;

		File file = new File(filePath);

		response.setContentType(contentType);
		response.setHeader("Content-Disposition", "inline;filename=\"" + file.getName() + "\"");
		BufferedInputStream inStrem = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());

		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		while ((bytesRead = inStrem.read(buffer)) != -1) {
			outStream.write(buffer, 0, bytesRead);
		}
		outStream.flush();
		inStrem.close();
		
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
