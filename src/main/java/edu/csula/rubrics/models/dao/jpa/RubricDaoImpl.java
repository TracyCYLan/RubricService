package edu.csula.rubrics.models.dao.jpa;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Criterion;
import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.RubricDao;

@Repository
public class RubricDaoImpl implements RubricDao {

    @PersistenceContext
    private EntityManager entityManager;

    //get certain rubric by rubric_id
    @Override
    public Rubric getRubric( Long id )
    {
        return entityManager.find( Rubric.class, id );
    }

    //get all rubrics created by this user
    @Override
    public List<Rubric> getPersonalRubrics( User creator )
    {
        String query = "from Rubric creator = :creator and deleted = false order by name asc";

        return entityManager.createQuery( query, Rubric.class )
            .setParameter( "creator", creator )
            .getResultList();
    }

    //get all published rubrics created by this user
    @Override
    public List<Rubric> getPublishedPersonalRubrics( User creator )
    {
        String query = "from Rubric where department is null "
            + "and creator = :creator and publishDate is not null "
            + "and publishDate < :now and obsolete = false "
            + "and deleted = false order by name asc";

        return entityManager.createQuery( query, Rubric.class )
            .setParameter( "creator", creator )
            .setParameter( "now", Calendar.getInstance() )
            .getResultList();
    }

    @Override
    public List<Rubric> searchRubrics( String text )
    {
    	if (text == null || text.trim().length() == 0)
			return null;
		text = text.trim();
		String query = "SELECT r.* FROM rubrics r " + 
				"WHERE MATCH (r.name , r.description) AGAINST (:text IN BOOLEAN MODE) " + 
				"AND r.deleted = FALSE " + 
				"GROUP BY r.id";
		text = text.replace(" ", "*");
		return entityManager.createNativeQuery(query, Rubric.class).setParameter("text", text + "*").getResultList();
    }

    //create a new rubric
    @Override
    @Transactional
    public Rubric saveRubric( Rubric rubric )
    {
        return entityManager.merge( rubric );
    }

	@Override
	public List<Rubric> getAllRubrics() {
		 return entityManager.createQuery("from Rubric where deleted = false", Rubric.class)
	                .getResultList();
	}

}
