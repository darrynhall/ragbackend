package org.aero.ingestion.entity;

import java.io.Serializable;
import java.util.List;

public record FileUploadEvent(String fileName, String badgeNumber, List<String> allowedUserBadges,
		List<String> allowedUserGroups, List<String> allowedSharePointPermissions) implements Serializable {

}
