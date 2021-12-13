package com.profile.domingueti.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.profile.domingueti.roles.Role;
import com.profile.domingueti.roles.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProfileServiceImp implements ProfileService, UserDetailsService {
	
	private final ProfileRepository repo;
	private final RoleRepository roleRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		Profile profile = repo.findByUsername(username);
		if (profile == null) {
			log.error("Profile not found in DB");
			throw new UsernameNotFoundException(username);
		
		} else {
			log.info("User found in DB : {}", username);
		}
		
		Collection<SimpleGrantedAuthority> auth = new ArrayList<>();
		profile.getRoles().forEach(role -> {
			auth.add(new SimpleGrantedAuthority(role.getName()));
		});
		
		return new org.springframework.security.core.userdetails.User(profile.getUsername(), profile.getPassword(), auth);
	
	}

	@Override
	public Profile saveProfile(Profile profile) {
		log.info("Saving new profile {} to DB", profile.getName());
//		profile.setPassword(passwordEncoder.encode(profile.getPassword()));
		return repo.save(profile);
	}

	@Override
	public Role saveRole(Role role) {
		log.info("Saving new role {} to DB", role.getName());
		return roleRepo.save(role);
	}

	@Override
	public void addRoleToUser(String username, String rolee) {
		log.info("Saving new role {} to user {}", rolee, username);
		Profile user = repo.findByUsername(username);
		Role role = roleRepo.findByName(rolee);
		user.getRoles().add(role);
	}

	@Override
	public Profile getProfile(String username) {
		log.info("Fetching profile {} by username", username);
		return repo.findByUsername(username);
	}

	@Override
	public List<Profile> getProfiles() {
		log.info("Fetching all profiles");
		return repo.findAll();
	}

	
	
}
