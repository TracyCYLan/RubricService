package edu.csula.rubrics.models.dao.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.CriterionRepository;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Tag;
import edu.csula.rubrics.models.dao.CriterionDao;


@Repository
public class CriterionDaoImpl implements CriterionDao {

	@PersistenceContext
	private EntityManager entityManager;

//    @Autowired
//    private CriterionRepository criterionRepository;
    
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
		Query query = entityManager.createNativeQuery("select * FROM criteria "
				+ "WHERE MATCH(name,description) AGAINST(:text IN NATURAL LANGUAGE MODE) " 
				+ "AND deleted = false", Criterion.class);
		query.setParameter("text", text);
		Iterator iterator = query.getResultList().iterator();
		List<Criterion> result = new ArrayList<>();
		while (iterator.hasNext()) {
			result.add((Criterion) iterator.next());
		}
		return result;
//		MatchQueryBuilder searchByName = QueryBuilders.matchQuery("name", text);
//        return this.criterionRepository.search(searchByName);
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
	public Long findTag(String name) {
		String query = "from Tag where name = :name";
		List<Tag> tags = entityManager.createQuery(query, Tag.class).setParameter("name", name).getResultList();
		if (tags.size() == 1)
			return tags.get(0).getId();
		else // not found (or normally impossible to find more than one result)
			return (long) -1;
	}
	
	@Override
	public List<Tag> getAllTags(){
		return entityManager.createQuery("select t from Tag as t where t.count>0 order by t.count desc", Tag.class).getResultList();
	}
}
