package org.aero.ingestion.controller;

import java.util.List;

import org.aero.ingestion.dto.UserViewDto;
import org.aero.ingestion.service.UserService;
import org.aero.ldapservice.LdapService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
	private final UserService userService;
	private final LdapService ldapService;

	@GetMapping("/logged-in-user")
	public UserViewDto getLoggedInUser() {
		return userService.getLoggedInUser();
	}

	@PutMapping("/refresh-ldap")
	public void refreshLdapCache() {
		ldapService.refreshCache();
	}

	@GetMapping(value = "/{badge}")
	public UserViewDto getUserByBadge(@PathVariable String badge) {
		return userService.getUserByBadge(badge);
	}

	@GetMapping
	public List<UserViewDto> search(@RequestParam(required = false) String query) {
		return userService.search(query);
	}
}
