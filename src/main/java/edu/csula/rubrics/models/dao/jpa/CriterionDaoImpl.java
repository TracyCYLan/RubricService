package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Tag;
import edu.csula.rubrics.models.dao.CriterionDao;

@Repository
public class CriterionDaoImpl implements CriterionDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Criterion getCriterion(Long id) {
		return entityManager.find(Criterion.class, id);
	}

	@Override
	@Transactional
	public Criterion saveCriterion(Criterion criterion) {
		return entityManager.merge(criterion);
	}

	// get all criteria in the db
	@Override
	public List<Criterion> getAllCriteria() {
		return entityManager.createQuery("from Criterion where deleted = false", Criterion.class).getResultList();
	}

	@Override
	public Rating getRating(Long id) {
		return entityManager.find(Rating.class, id);
	}

	@Override
	@Transactional
	public Rating saveRating(Rating rating) {
		return entityManager.merge(rating);
	}

	// get ratings of this criterion
	@Override
	public List<Rating> getRatings(Criterion criterion) {
		String query = "from Rating where criterion = :criterion order by value asc";

		return entityManager.createQuery(query, Rating.class).setParameter("criterion", criterion).getResultList();
	}

	// search
	@Override
	public List<Criterion> searchCriteria(String text) {
		String query = "from Criterion where name like :text or description like :text and deleted = false";

		return entityManager.createQuery(query, Criterion.class).setParameter("text", "%" + text + "%").getResultList();
	}

	@Override
	public Tag getTag(Long id) {
		return entityManager.find(Tag.class, id);
	}

	@Override
	@Transactional
	public Tag saveTag(Tag tag) {
		return entityManager.merge(tag);
	}
	
	// see if tag exist.
	@Override
	public Long findTag( String name ) {
		String query = "from Tag where name = :name";
		List<Tag> tags = entityManager.createQuery(query,Tag.class).setParameter("name", name).getResultList();
		if(tags.size()==1)
			return tags.get(0).getId();
		else //not found (or normally impossible to find more than one result)
			return (long) -1;
	}
}
