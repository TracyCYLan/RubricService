package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rating;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.dao.CriterionDao;
@Repository
public class CriterionDaoImpl implements CriterionDao{

	
	 @PersistenceContext
	 private EntityManager entityManager;

	@Override
	public Criterion getCriterion(Long id) {
		return entityManager.find(Criterion.class, id);
	}

	@Override
	@Transactional
	public Criterion saveCriterion(Criterion criterion) {
		return entityManager.merge( criterion );
	}

	//get all criteria in the db
	@Override
	public List<Criterion> getAllCriteria() {
		return entityManager.createQuery("from Criterion", Criterion.class)
                .getResultList();
	}

	@Override
	public Rating getRating(Long id) {
		return entityManager.find(Rating.class, id);
	}

	@Override
	@Transactional
	public Rating saveRating(Rating rating) {
		return entityManager.merge( rating );
	}

	//get ratings of this criterion
	@Override
	public List<Rating> getRatings(Criterion criterion) {
		String query = "from Rating where criterion = :criterion order by value asc";

        return entityManager.createQuery( query, Rating.class )
            .setParameter( "criterion", criterion )
            .getResultList();
	}

}
