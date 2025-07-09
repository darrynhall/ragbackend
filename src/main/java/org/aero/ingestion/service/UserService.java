package org.aero.ingestion.service;

import java.util.Arrays;
import java.util.List;

import org.aero.ingestion.dto.UserViewDto;
import org.aero.ldapservice.LdapService;
import org.aero.ldapservice.model.Person;
import org.aero.loggedinuser.person.LoggedInUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final LoggedInUser loggedInUser;
	private final LdapService ldapService;

	public UserViewDto getLoggedInUser() {
		return convertPersonToUserViewDto(loggedInUser.getPerson());
	}

	private UserViewDto convertPersonToUserViewDto(Person person) {
		return new UserViewDto(person.getBadgeNumber(), person.getDisplayName());
	}

	public UserViewDto getUserByBadge(String badge) {
		return convertPersonToUserViewDto(ldapService.getEmployeeByBadge(badge));

	}

	public List<UserViewDto> search(String query) {
		boolean emptyQuery = !StringUtils.hasText(query);

		return ldapService.getEmployees().stream().filter(p -> emptyQuery || match(p, query))
				.map(this::convertPersonToUserViewDto).toList();
	}

	private boolean match(Person person, String query) {
		// Split on whitespace and see all words are contained, like "John", "Smith"
		List<String> words = Arrays.asList(query.toLowerCase().split("\\s+"));
		return words.stream().allMatch(word -> matchFields(person, word));
	}

	private boolean matchFields(Person person, String word) {
		String displayName = person.getDisplayName() != null ? person.getDisplayName().toLowerCase() : "";
		String badgeNumber = person.getBadgeNumber() != null ? person.getBadgeNumber().toLowerCase() : "";

		return displayName.contains(word) || badgeNumber.contains(word);
	}

}
