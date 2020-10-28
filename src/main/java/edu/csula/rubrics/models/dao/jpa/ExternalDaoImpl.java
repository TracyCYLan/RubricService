package edu.csula.rubrics.models.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Criterion;
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
	
	@Override
	public long checkExists(String extsource, String extid, String type) {
		String query = "from External where source = :extsource AND external_id = :extid AND type = :type";
		List<External> externals = entityManager.createQuery(query, External.class)
								.setParameter("extsource", extsource)
								.setParameter("extid", extid)
								.setParameter("type", type)
								.getResultList();
		if(externals.size() > 0) //return existed ID
		{
			if(type.equals("rubric"))
				return externals.get(0).getRubric().getId();
			else if(type.equals("criterion"))
				return externals.get(0).getCriterion().getId();
		}
		// not found (or normally impossible to find more than one result)
		return (long) -1;
	}
	
}
