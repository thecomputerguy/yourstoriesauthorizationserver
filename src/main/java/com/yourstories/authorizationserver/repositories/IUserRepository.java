package com.yourstories.authorizationserver.repositories;

import com.yourstories.authorizationserver.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IUserRepository extends MongoRepository<User,String>{

	List<User> getAllUsers();
	User getUser(String id);
	User createUser(User author);
	User updateUser(User author);
	void deleteUser(User author);
	void deleteUser(String id);
	User getByUsername(String username);
	User save(User user);
}
