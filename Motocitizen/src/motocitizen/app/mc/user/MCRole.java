package motocitizen.app.mc.user;

import java.util.Arrays;

import motocitizen.app.mc.MCAccidents;

public class MCRole {
	private static final String[] ReadOnly = new String[] { "readonly", "banned", "standart", "moderator", "admin" };
	private static final String[] Standart = new String[] { "standart", "moderator", "admin" };
	private static final String[] Moderator = new String[] { "moderator", "admin" };
	private static final String[] Admin = new String[] { "admin" };

	private static String getRole() {
		String role = MCAccidents.auth.role;
		if (role == null) {
			return "";
		}
		return role;
	}

	public static boolean isRO() {
		return Arrays.asList(ReadOnly).contains(getRole());
	}

	public static boolean isStandart() {
		return Arrays.asList(Standart).contains(getRole());
	}

	public static boolean isModerator() {
		return Arrays.asList(Moderator).contains(getRole());
	}

	public static boolean isAdmin() {
		return Arrays.asList(Admin).contains(getRole());
	}
}
