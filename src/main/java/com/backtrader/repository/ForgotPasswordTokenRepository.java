package com.backtrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtrader.userentity.ForgotPasswordToken;

public interface ForgotPasswordTokenRepository extends JpaRepository<ForgotPasswordToken, Integer>{
	public ForgotPasswordToken findByToken(String token);
}
