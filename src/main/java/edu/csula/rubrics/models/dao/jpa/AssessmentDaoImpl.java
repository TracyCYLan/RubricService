package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.AssessmentGroup;
import edu.csula.rubrics.models.Rubric;
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

	@Override
	public AssessmentGroup getAssessmentGroup(Long id) {
		return entityManager.find( AssessmentGroup.class, id );
	}

	//add a new Assessment Group
	@Override
	@Transactional
	public AssessmentGroup saveAssessmentGroup(AssessmentGroup assessmentGroup) {
		return entityManager.merge( assessmentGroup );
	}
	
	@Override
	public List<AssessmentGroup> getAssessmentGroups() {
		// order by lastUpdatedDate desc, publishDate desc
		return entityManager.createQuery("from AssessmentGroup", AssessmentGroup.class).getResultList();
	}
}
