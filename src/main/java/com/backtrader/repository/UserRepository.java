package com.backtrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backtrader.userentity.Users;
@Repository
public interface UserRepository extends JpaRepository<Users, Integer>{
	public Users findByEmail(String email);

}
