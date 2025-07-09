package org.aero.ingestion.constants;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.aero.ldapservice.model.Person;
import org.aero.loggedinuser.person.LoggedInUser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserRoleEnum {

	APP_APPDEV_AES_ADMIN("APP_AppDev_AES_Admin");

	@Getter
	private final String ldapGroup;

	private static Map<String, UserRoleEnum> ldapGroupToRole = stream(values())
			.collect(toMap(UserRoleEnum::getLdapGroup, Function.identity()));

	public static List<UserRoleEnum> getRoles(Person person) {
		return person.getGroups().stream().map(ldapGroupToRole::get).filter(Objects::nonNull).toList();
	}

	public static List<UserRoleEnum> getRoles(LoggedInUser loggedInUser) {
		return getRoles(loggedInUser.getPerson());
	}
}
