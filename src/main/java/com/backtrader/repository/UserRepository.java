package com.backtrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backtrader.userentity.User;
@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	public User findByEmail(String email);

}
