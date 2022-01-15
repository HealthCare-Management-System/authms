package com.citiustech.authms.controller;

import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.citiustech.authms.service.AuthService;
import com.model.LoginUser;
import com.model.UserDto;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	AuthService authService;
	
	@ApiOperation(value = "Sign Up Users", notes = "default method for adding Users")
    @PostMapping("/signup")
    public ResponseEntity<UserDto> addUser(@RequestBody UserDto dto) {
        
		UserDto newUser = authService.addUser(dto);
		return new ResponseEntity<>(newUser, HttpStatus.OK);

    }
	
	@ApiOperation(value = "Log In User", notes = "default method for Logging In User")
    @PostMapping("/login")
    public AccessTokenResponse logInUser(@RequestBody LoginUser user) {
		
        return authService.logInUser(user);
    }
}
