package com.citiustech.authms.service;

import org.keycloak.representations.AccessTokenResponse;

import com.model.LoginUser;
import com.model.UserDto;

public interface AuthService {

	public UserDto addUser(UserDto dto);
	
	public String pingTest();
	
	public AccessTokenResponse logInUser(LoginUser user);
}
