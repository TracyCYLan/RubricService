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
import org.springframework.web.bind.annotation.PutMapping;
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
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.CriterionDao;
import edu.csula.rubrics.models.dao.RubricDao;
import edu.csula.rubrics.models.dao.UserDao;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserDao userDao;

	// get ALL rubrics
	@GetMapping
	public List<User> getUsers(ModelMap models) {
		return userDao.getAllUsers();
	}

//	// get this rubric
//	@GetMapping("/{id}")
//	public Rubric getRubric(@PathVariable Long id) {
//		Rubric rubric = rubricDao.getRubric(id);
//		if (rubric == null)
//			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rubric not found");
//		return rubric;
//	}

}
