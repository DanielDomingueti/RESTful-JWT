package com.profile.domingueti.profile;

import java.util.List;

import com.profile.domingueti.roles.Role;

public interface ProfileService {

	Profile saveProfile(Profile profile);
	Role saveRole(Role role);
	void addRoleToUser(String username, String role);
	Profile getProfile(String username);
	List<Profile> getProfiles();
	
}
