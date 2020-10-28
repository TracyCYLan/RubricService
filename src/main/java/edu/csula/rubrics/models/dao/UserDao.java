package edu.csula.rubrics.models.dao;

import java.util.List;

import edu.csula.rubrics.models.User;

public interface UserDao {

	User getUser(Long id);

	List<User> getAllUsers();

	User getUserByCin(String cin);

	User getUserByUsername(String username);

	User getUserBySub(String sub);

	List<User> getUsers(Long ids[]);

	List<User> getUsers(String lastName, String firstName);

	List<User> searchUsers(String text);

	List<User> searchUsersByPrefix(String text, int maxResults);

	List<User> searchUsersByStanding(String dept, String sybmol);

	User saveUser(User user);

}
