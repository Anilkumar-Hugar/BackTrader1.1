package com.backtrader.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserModel {
	@NotNull(message = "Value should not be null")
	@NotBlank(message = "Should not be blank")
	private String firstname;
	@NotNull(message = "Value should not be null")
	@NotBlank(message = "Should not be blank")
	private String lastname;
	@NotNull(message = "Value should not be null")
	@NotBlank(message = "Should not be blank")
	private String email;
	@NotNull(message = "should not be null")
	private long phone;
	@NotNull(message = "Value should not be null")
	@NotBlank(message = "Should not be blank")
	private String password;
}
