package com.profile.domingueti.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.profile.domingueti.profile.Profile;
import com.profile.domingueti.profile.ProfileController;

@Component
public class ProfileAssembler implements RepresentationModelAssembler<Profile, EntityModel<Profile>>{

	@Override
	public EntityModel<Profile> toModel(Profile profile) {
		return EntityModel.of(profile, linkTo(methodOn(ProfileController.class).getById(profile.getId())).withSelfRel(),
				linkTo(methodOn(ProfileController.class).getAll()).withRel("profiles"));
	}

}
