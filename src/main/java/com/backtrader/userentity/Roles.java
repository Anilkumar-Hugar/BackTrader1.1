package com.backtrader.userentity;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;

//import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@SuppressWarnings("serial")
@Entity(name = "userroles")
@Getter
@Setter
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
public class Roles implements GrantedAuthority {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	private String role;

	@Override
	public String getAuthority() {
		return role;
	}
}
