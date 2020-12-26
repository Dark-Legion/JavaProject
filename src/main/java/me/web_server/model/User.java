package me.web_server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import me.web_server.Utils;

public final class User implements Serializable {
	private final static long serialVersionUID = 0;

	public final String name;
	public final boolean isAdmin;
	public final String addedBy;
	public final String addedOn;

	private User(String name, boolean isAdmin, String addedBy, Date addedOn) {
		super();

		this.name = name;
		this.isAdmin = isAdmin;
		this.addedBy = addedBy;
		this.addedOn = Utils.formatDateIso(addedOn);
	}

	@Override
	public boolean equals(Object obj) {
		User other = User.class.cast(obj);

		if (obj != null) {
			return name.equals(other.name) &&
				isAdmin == other.isAdmin &&
				addedBy.equals(other.addedBy) &&
				addedOn.equals(other.addedOn);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, isAdmin, addedBy, addedOn);
	}

	public static User load(String name, Boolean isAdmin, String addedBy, Date addedOn) {
		if (Utils.nonNullParameters(name, isAdmin, addedBy, addedOn)) {
			return new User(name, isAdmin, addedBy, addedOn);
		} else {
			return null;
		}
	}

	public static User load(HashMap<String, Object> map) {
		String name = String.class.cast(map.get("name"));
		Boolean isAdmin = Boolean.class.cast(map.get("is_admin"));
		String addedBy = String.class.cast(map.get("added_by"));
		Date addedOn = Date.class.cast(map.get("added_on"));

		return load(name, isAdmin, addedBy, addedOn);
	}

	public static User[] loadList(ArrayList<HashMap<String, Object>> list) {
		ArrayList<User> userList = new ArrayList<>(list.size());

		for (HashMap<String, Object> map : list) {
			User user = load(map);

			if (user != null) {
				userList.add(user);
			}
		}

		return userList.toArray(User[]::new);
	}
}
