package edu.csula.rubrics.models.dao.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.csula.rubrics.models.Rubric;
import edu.csula.rubrics.models.User;
import edu.csula.rubrics.models.dao.UserDao;

@Repository
public class UserDaoImpl implements UserDao {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	@Transactional
	public User saveUser(User user) {
		return entityManager.merge(user);
	}

	@Override
	public User getUser(Long id) {
		return entityManager.find(User.class, id);
	}

	@Override
	public List<User> getAllUsers() {
		return entityManager.createQuery("from User", User.class).getResultList();
	}

	@Override
	public User getUserByCin(String cin) {
		List<User> users = entityManager.createQuery("from User where cin = :cin", User.class).setParameter("cin", cin)
				.getResultList();
		return users.size() == 0 ? null : users.get(0);
	}

	@Override
	public User getUserByUsername(String username) {
		String query = "from User where username = :username";

		List<User> users = entityManager.createQuery(query, User.class)
				.setParameter("username", username.toLowerCase())
				.getResultList();
		return users.size() == 0 ? null : users.get(0);
	}

	@Override
	public User getUserBySub(String sub) {
		String query = "from User where sub = :sub";

		List<User> users = entityManager.createQuery(query, User.class)
				.setParameter("sub", sub.toLowerCase())
				.getResultList();
		return users.size() == 0 ? null : users.get(0);
	}

	@Override
	public List<User> getUsers(Long ids[]) {
		if (ids == null || ids.length == 0)
			return new ArrayList<User>();

		String query = "from User where id in (:ids) " + "order by lastName asc, firstName asc";

		return entityManager.createQuery(query, User.class).setParameter("ids", Arrays.asList(ids)).getResultList();
	}

	@Override
	public List<User> getUsers(String lastName, String firstName) {
		String query = "from User where lower(lastName) = :lastName " + "and lower(firstName) = :firstName";

		return entityManager.createQuery(query, User.class).setParameter("lastName", lastName.toLowerCase())
				.setParameter("firstName", firstName.toLowerCase()).getResultList();
	}

	@Override
	public List<User> searchUsers(String text) {
		text = text.toLowerCase();
		String query = "from User where cin = :text or lower(username) = :text "
				+ "or lower(firstName) = :text or lower(lastName) = :text "
				+ "or lower(firstName || ' ' || lastName) = :text "
				+ "or lower(primaryEmail) = :text order by firstName asc";

		return entityManager.createQuery(query, User.class).setParameter("text", text).getResultList();
	}

	@Override
	public List<User> searchUsersByPrefix(String text, int maxResults) {
		text = text.toLowerCase();
		String query = "from User where cin like :text || '%' " + "or lower(username) like :text || '%' "
				+ "or lower(firstName) like :text || '%' " + "or lower(lastName) like :text || '%' "
				+ "or lower(firstName || ' ' || lastName) like :text || '%' "
				+ "or lower(primaryEmail) like :text || '%' " + "order by firstName asc";

		return entityManager.createQuery(query, User.class).setParameter("text", text).setMaxResults(maxResults)
				.getResultList();
	}

	@Override
	public List<User> searchUsersByStanding(String dept, String symbol) {
		return entityManager.createNamedQuery("user.search.by.standing", User.class).setParameter("dept", dept)
				.setParameter("symbol", symbol.toUpperCase()).getResultList();
	}

}
