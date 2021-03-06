package edu.csula.rubrics.models.dao.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Assessment;
import edu.csula.rubrics.models.AssessmentGroup;
import edu.csula.rubrics.models.Comment;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.dao.AssessmentDao;
import edu.csula.rubrics.models.dao.RubricDao;


@Repository
public class AssessmentDaoImpl implements AssessmentDao {

    @PersistenceContext
    private EntityManager entityManager;

	@Autowired
	RubricDao rubricDao;
	
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
		return entityManager.createQuery("from AssessmentGroup", AssessmentGroup.class).getResultList();
	}
	
	@Override
	public Comment getComment(Long id) {
		return entityManager.find( Comment.class, id );
	}

	//add a new comment
	@Override
	@Transactional
	public Comment saveComment(Comment comment) {
		return entityManager.merge( comment );
	}
	
	@Override
	public List<AssessmentGroup> getAssessmentGroupsByRubric(Long rid) {
		Rubric rubric = rubricDao.getRubric(rid);
		if(rubric==null)
			return new ArrayList<>();
		
		String query = "from AssessmentGroup where rubric = :rubric";

		return entityManager.createQuery(query, AssessmentGroup.class).setParameter("rubric", rubric).getResultList();
	}
	
	@Override
	public List<AssessmentGroup> searchAssessmentGroups(String text) {
		if (text == null || text.trim().length() == 0)
			return null;
		text = text.trim();
		String query = "SELECT a.* FROM assessment_group a "
				+ "WHERE MATCH (a.name, a.description) AGAINST (:text IN BOOLEAN MODE) "
				+ "GROUP BY a.id";
		text = text.replace(" ", "*");
		return entityManager.createNativeQuery(query, AssessmentGroup.class).setParameter("text", text + "*").getResultList();
	}
}
