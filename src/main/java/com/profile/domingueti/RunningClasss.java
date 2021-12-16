package com.profile.domingueti;

import java.util.ArrayList;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.profile.domingueti.profile.Profile;
import com.profile.domingueti.profile.ProfileServiceImp;
import com.profile.domingueti.roles.Role;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class RunningClasss implements CommandLineRunner{

	private final ProfileServiceImp serviceI;
	
	@Override
	public void run(String... args) throws Exception {

		serviceI.saveRole(new Role(null, "ROLE_USER"));
		serviceI.saveRole(new Role(null, "ROLE_ADMIN"));
		serviceI.saveRole(new Role(null, "ROLE_MANAGER"));
		serviceI.saveRole(new Role(null, "ROLE_SUPER_ADM"));
		
		serviceI.saveProfile(new Profile(null, "Dianho", "dianho123", "123", new ArrayList<>()));
		serviceI.saveProfile(new Profile(null, "Will smith", "will", "123", new ArrayList<>()));
		serviceI.saveProfile(new Profile(null, "Arnold schwarzenegger", "arnoldzinho", "123", new ArrayList<>()));
		serviceI.saveProfile(new Profile(null, "Jim carry", "jim", "123", new ArrayList<>()));
		

		serviceI.addRoleToUser("dianho123", "ROLE_USER");
		serviceI.addRoleToUser("dianho123", "ROLE_MANAGER");
		serviceI.addRoleToUser("dianho123", "ROLE_ADMIN");
		serviceI.addRoleToUser("dianho123", "ROLE_SUPER_ADM");
		serviceI.addRoleToUser("will", "ROLE_ADMIN");
		serviceI.addRoleToUser("arnoldzinho", "ROLE_MANAGER");
		serviceI.addRoleToUser("jim", "ROLE_SUPER_ADM");
	}

}
