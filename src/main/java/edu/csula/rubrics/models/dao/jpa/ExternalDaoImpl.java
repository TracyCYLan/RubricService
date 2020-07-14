package edu.csula.rubrics.models.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.External;
import edu.csula.rubrics.models.dao.ExternalDao;

@Repository
public class ExternalDaoImpl implements ExternalDao{
	
	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public External getExternal(Long id) {
		return entityManager.find(External.class, id);
	}

	@Override
	@Transactional
	public External saveExternal(External external) {
		return entityManager.merge(external);
	}
}
