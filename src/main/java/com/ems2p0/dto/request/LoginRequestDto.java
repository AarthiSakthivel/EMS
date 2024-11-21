package com.ems2p0.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequestDto {

	@NotBlank(message = "Usesrname shouldn't be empty")
	@Size(min = 5, max = 50, message = "Username should be min 5 char & max 50 char")
	String userName;

	@NotBlank(message = "Password shouldn't be empty")
	@Size(min = 5, max = 50, message = "Password should be min 5 char & max 50 char")
	String password;

}
