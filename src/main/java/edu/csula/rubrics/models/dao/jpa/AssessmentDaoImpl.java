package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.dao.AssessmentDao;


@Repository
public class AssessmentDaoImpl implements AssessmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    //get a evaluation by id
	@Override
	public Assessment getAssessment(Long id) {
		return entityManager.find( Assessment.class, id );
	}

	//add a new assessment
	@Override
	@Transactional
	public Assessment saveAssessment(Assessment assessment) {
		return entityManager.merge( assessment );
	}

}
