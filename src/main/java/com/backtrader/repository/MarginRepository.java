package com.backtrader.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backtrader.userentity.Margin;
import com.backtrader.userentity.User;

public interface MarginRepository extends JpaRepository<Margin, Integer> {
	Margin findByUser(User user);
}
