package edu.csula.rubrics.canvas.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.canvas.dao.CanvasDao;
import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.Tag;

@Repository
public class CanvasDaoImpl implements CanvasDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public long checkRubricExists(String extsource, String extid) {
		String query = "from Rubric where externalSource = :extsource AND externalId = :extid";
		List<Rubric> rubrics = entityManager.createQuery(query, Rubric.class)
								.setParameter("extsource", extsource)
								.setParameter("extid", extid)
								.getResultList();
		if (rubrics.size() == 1)
			return rubrics.get(0).getId();
		else // not found (or normally impossible to find more than one result)
			return (long) -1;
	}
	
	@Override
	public long checkCriterionExists(String extsource, String extid) {
		String query = "from Criterion where externalSource = :extsource AND externalId = :extid";
		List<Criterion> criteria = entityManager.createQuery(query, Criterion.class)
								.setParameter("extsource", extsource)
								.setParameter("extid", extid)
								.getResultList();
		if (criteria.size() == 1)
			return criteria.get(0).getId();
		else // not found (or normally impossible to find more than one result)
			return (long) -1;
	}
}
