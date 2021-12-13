package com.profile.domingueti.assembler;

import java.lang.ModuleLayer.Controller;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.profile.domingueti.profile.Profile;


@Component
public class ProfileAssembler implements RepresentationModelAssembler<Profile, EntityModel<Profile>>{

	@Override
	public EntityModel<Profile> toModel(Profile profile) {
		return EntityModel.of(profile, linkTo(methodOn(Controller.class).getById(profile.getId())).withSelfRel(),
				linkTo(methodOn(Controller.class).getAll()).withRel("profiles"));
	}

}
