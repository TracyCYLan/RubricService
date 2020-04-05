package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Association;
import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.dao.AssociationDao;

@Repository
public class AssociationDaoImpl implements AssociationDao {
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Association getAssociation(Long id) {
		return entityManager.find(Association.class, id);
	}

	@Override
	@Transactional
	public Association saveAssociation(Association association) {
		return entityManager.merge(association);
	}

	@Override
	public List<Association> getAllAssociations() {
		return entityManager.createQuery("from Association", Association.class).getResultList();
	}
}
