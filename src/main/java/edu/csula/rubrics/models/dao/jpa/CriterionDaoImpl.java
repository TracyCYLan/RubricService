package edu.csula.rubrics.models.dao.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
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
	//only show the criterion which is not deleted and can be reused here.
	@Override
	public List<Criterion> getAllCriteria() {
		return entityManager.createQuery("from Criterion where deleted = false and reusable = true", Criterion.class).getResultList();
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

	// search criteria
	@Override
	public List<Criterion> searchCriteria(String text) {
		if (text == null || text.trim().length() == 0)
			return null;
		text = text.trim();
		String query = "select c.* from criteria c " + "inner join ratings r on r.criterion_id = c.id "
				+ "inner join criterion_tags ct on ct.criterion_id = c.id " + "inner join tags t on t.id = ct.tag_id "
				+ "where match(c.name,c.description) against(:text in boolean mode) "
				+ "or match(t.value) against(:text IN BOOLEAN MODE) "
				+ "or match(r.description) against (:text IN BOOLEAN MODE) " + "and c.deleted = false \r\n"
				+ "group by c.id";
		text = text.replace(" ", "*");
		return entityManager.createNativeQuery(query, Criterion.class).setParameter("text", text + "*").getResultList();

	}

	@Override
	public List<Criterion> getAllCriteriaByTag(String tagvalue) {
		Long id = findTag(tagvalue);
		if (id == -1)
			return new ArrayList<>();
		String query = "select c from Criterion c inner join c.tags ct on ct.id=:id where ct.value=:value and c.deleted= false";

		return entityManager.createQuery(query, Criterion.class).setParameter("id", id).setParameter("value", tagvalue)
				.getResultList();

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
	public Long findTag(String value) {
		String query = "from Tag where value = :value";
		List<Tag> tags = entityManager.createQuery(query, Tag.class).setParameter("value", value).getResultList();
		if (tags.size() == 1)
			return tags.get(0).getId();
		else // not found (or normally impossible to find more than one result)
			return (long) -1;
	}

	// search tag
	@Override
	public List<Tag> searchTag(String text) {
		if (text == null || text.trim().length() == 0)
			return null;
		text = text.trim();
		String query = "from Tag where value like :text";
		return entityManager.createQuery(query, Tag.class).setParameter("text", "%"+text+"%").getResultList();
	}

	@Override
	public List<Tag> getAllTags() {
		return entityManager.createQuery("select t from Tag as t where t.count>0 order by t.count desc", Tag.class)
				.getResultList();
	}
}
