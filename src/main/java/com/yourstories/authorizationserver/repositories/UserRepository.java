package com.yourstories.authorizationserver.repositories;

import com.yourstories.authorizationserver.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository implements IUserRepository {

	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User getUser(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User createUser(User author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public User updateUser(User author) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteUser(User author) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public User getByUsername(String username) {
		return null;
	}

	@Override
	public User save(User user) {
		return null;
	}
}
