package com.backtrader.userentity;

import java.io.Serializable;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@SuppressWarnings("serial")
@Entity(name = "userdetails")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements Serializable{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private int id;
	@NotBlank
	@Column(nullable = false,length = 20)
	@NotNull
	private String firstname;
	@Column(nullable = false,length = 20)
	private String lastname;
	@NotBlank
	@Email
	@Column(nullable = false,length = 50)
	private String email;
	@Nonnull
	@Column(length = 10)
	private long phone;
	@NotBlank
	@Column(nullable = false)
	private String password;
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "user_role", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "role_id", referencedColumnName = "id") })
	private List<Role> role;
}
