package com.profile.domingueti.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.profile.domingueti.profile.ProfileController;
import com.profile.domingueti.roles.Role;
@Component
public class RoleAssembler implements RepresentationModelAssembler<Role, EntityModel<Role>> {

	@Override
	public EntityModel<Role> toModel(Role role) {
		return EntityModel.of(role, linkTo(methodOn(ProfileController.class).getById(role.getId())).withSelfRel(),
				linkTo(methodOn(ProfileController.class).getAll()).withRel("roles"));
	}

}
