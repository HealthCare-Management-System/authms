package com.citiustech.authms.service.impl;

import java.util.Arrays;
import java.util.Collections;

import javax.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.citiustech.authms.service.AuthService;
import com.model.LoginUser;
import com.model.UserDto;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${keyclock.serverUrl}")
    private   String serverUrl;
	@Value("${keyclock.realm}")
    private   String realm;
	@Value("${keyclock.clientId}")
    private   String clientId;
	@Value("${keyclock.clientSecret}")
    private   String clientSecret;
	@Value("${keyclock.userMsUrl}")
    private   String userMsUrl;
	@Value("${keyclock.userName}")
    private   String userName;
	@Value("${keyclock.password}")
    private   String password;
	
	
	

	@Override
	public UserDto addUser(UserDto dto) {

		// call create method on User Controller
		UserDto addedUser=addUserInDb(dto);
		UserResource resource=null;

		// call create User on Keyclock
		if(null != addedUser) {
			 resource=	addUserinKeyClock(dto);
		}

		// delete method in db
		if(null == resource) {
			//delete operation
		}

		return addedUser;
	}

	public UserResource addUserinKeyClock(UserDto dto) {

		Keycloak keycloak = KeycloakBuilder.builder() //
				.serverUrl(serverUrl) //
				.realm(realm) //
				.grantType(OAuth2Constants.PASSWORD) //
				.clientId(clientId) //
				.clientSecret(clientSecret) //
				.username(userName) //
				.password(password) //
				.build();
		// Define user
		UserRepresentation user = new UserRepresentation();
		user.setEnabled(true);
		user.setUsername(dto.getEmail());
		user.setFirstName(dto.getName());
		user.setLastName(dto.getLname());
		user.setEmail(dto.getEmail());
		user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));

		// Get realm
		RealmResource realmResource = keycloak.realm(realm);
		UsersResource usersRessource = realmResource.users();

		// Create user (requires manage-users role)
		Response response = usersRessource.create(user);
		// System.out.printf("Repsonse: %s %s%n", response.getStatus(),
		// response.getStatusInfo());
		// System.out.println(response.getLocation());
		String userId = CreatedResponseUtil.getCreatedId(response);

		System.out.printf("User created with userId: %s%n", userId);

		// Define password credential
		CredentialRepresentation passwordCred = new CredentialRepresentation();
		passwordCred.setTemporary(false);
		passwordCred.setType(CredentialRepresentation.PASSWORD);
		passwordCred.setValue(dto.getPassword());

		UserResource userResource = usersRessource.get(userId);

		// Set password credential
		userResource.resetPassword(passwordCred);

//        // Get realm role "tester" (requires view-realm role)
		RoleRepresentation testerRealmRole = realmResource.roles()//
				.get(dto.getRole().toUpperCase()).toRepresentation();
//
//        // Assign realm role tester to user
		userResource.roles().realmLevel() //
				.add(Arrays.asList(testerRealmRole));
		
		return userResource;
//
//        // Get client
//        ClientRepresentation app1Client = realmResource.clients() //
		// .findByClientId("clientId").get(0);
//
//        // Get client level role (requires view-clients role)
		// RoleRepresentation userClientRole =
		// realmResource.clients().get(app1Client.getId()) //
		// .roles().get("user").toRepresentation();
//
//        // Assign client level role to user
		// userResource.roles() //
		// .clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));

		// Send password reset E-Mail
		// VERIFY_EMAIL, UPDATE_PROFILE, CONFIGURE_TOTP, UPDATE_PASSWORD,
		// TERMS_AND_CONDITIONS
//        usersRessource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));

		// Delete User
//        userResource.remove();

	}

	@Override
	public AccessTokenResponse logInUser(LoginUser user) {

		Keycloak keycloak = KeycloakBuilder.builder() //
				.serverUrl(serverUrl) //
				.realm(realm) //
				.grantType(OAuth2Constants.PASSWORD) //
				.clientId(clientId) //
				.clientSecret(clientSecret) //
				.username(user.getUsername()) //
				.password(user.getPassword()) //
				.build();
		return keycloak.tokenManager().getAccessToken();
	}

	public UserDto addUserInDb(UserDto dto) {
		ResponseEntity<UserDto> response = restTemplate.postForEntity(userMsUrl+"/signup", dto,
				UserDto.class);
		return response.getBody();
	}

	@Override
	public String pingTest() {
		ResponseEntity<String> response = restTemplate.getForEntity(userMsUrl+"/ping", String.class);
		return response.getBody();
	}
}
