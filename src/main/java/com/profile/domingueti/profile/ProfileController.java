package com.profile.domingueti.profile;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.ObjectNotFoundException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.profile.domingueti.assembler.ProfileAssembler;
import com.profile.domingueti.assembler.RoleAssembler;
import com.profile.domingueti.roles.Role;
import com.profile.domingueti.roles.RoleRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(path = "/api")
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileServiceImp service;
	private final ProfileRepository repo;
	private final RoleRepository rrepo;
	private final ProfileAssembler assembler;
	private final RoleAssembler rassembler;
	
	@GetMapping(path = "profile/all")
	public CollectionModel<EntityModel<Profile>> getAll() {
		List<EntityModel<Profile>> profiles = repo.findAll().stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		return CollectionModel.of(profiles, 
				linkTo(methodOn(ProfileController.class).getAll()).withSelfRel());
	}
	
	@GetMapping(path = "profile/id/{id}")
	public EntityModel<Profile> getById(@PathVariable Long id) {
		Profile profile = repo.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, null));
		return assembler.toModel(profile);
	}
	
	@PostMapping(path = "profile/save")
	public ResponseEntity<EntityModel<Profile>> addProfile(@RequestBody Profile profile) {
		EntityModel<Profile> entityModel = assembler.toModel(repo.save(profile));
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
	
	@PostMapping(path = "role/save")
	public ResponseEntity<EntityModel<Role>> addRole(@RequestBody Role role) {
		EntityModel<Role> entityModel = rassembler.toModel(rrepo.save(role));
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
	
	@PostMapping(path = "role/addtouser")
	public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserForm form) {
		service.addRoleToUser(form.getUsername(), form.getRolename());
		return ResponseEntity.ok().build();
	}
	
	@PutMapping(path = "profile/update/{id}")
	public ResponseEntity<EntityModel<Profile>> updateProfile(@RequestBody Profile newProfile, @PathVariable Long id) {
		Profile updatedProfile = repo.findById(id).map(profile -> {
			profile.setName(newProfile.getName());
			return repo.save(profile);
		})
			.orElseGet(() -> {
				newProfile.setId(id);
				return repo.save(newProfile);
			});
		EntityModel<Profile> entityModel = assembler.toModel(updatedProfile);
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
	
	@GetMapping("token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws JsonMappingException, IOException {
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			try {
				String refresh_token = authorizationHeader.substring("Bearer ".length());
				Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
				JWTVerifier verifier = JWT.require(algorithm).build();
				DecodedJWT decodedJWT = verifier.verify(refresh_token);
				String username = decodedJWT.getSubject();
				Profile user = repo.findByUsername(username);

				String access_token = JWT.create().withSubject(user.getUsername())
						.withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
						.withIssuer(request.getRequestURL().toString()).withClaim("roles", user.getRoles()
								.stream().map(Role::getName).collect(Collectors.toList()))
						.sign(algorithm);
				Map<String, String> tokens = new HashMap<>();
				tokens.put("access_token", access_token);
				tokens.put("refresh_token", refresh_token);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), tokens);
			} catch (Exception e) {
				log.error("Error loggin in: {}", e.getMessage());
				response.setHeader("error", e.getMessage());
				response.setStatus(HttpStatus.FORBIDDEN.value());
//				response.sendError(HttpStatus.FORBIDDEN.value());

				Map<String, String> error = new HashMap<>();
				error.put("error_message", e.getMessage());
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		} else {
			throw new RuntimeException("Refresh token is missing");
		}
	}
	
}

@Data
class RoleToUserForm {
	private String username;
	private String rolename;
}
