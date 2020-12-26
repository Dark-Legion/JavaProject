package me.web_server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import me.web_server.Utils;

public final class Client implements Serializable {
	private final static long serialVersionUID = 0;

	public final String name;
	public final boolean isCompany;
	public final String addedBy;
	public final String addedOn;

	private Client(String name, boolean isCompany, String addedBy, Date addedOn) {
		super();

		this.name = name;
		this.isCompany = isCompany;
		this.addedBy = addedBy;
		this.addedOn = Utils.formatDateIso(addedOn);
	}

	@Override
	public boolean equals(Object obj) {
		Client other = Client.class.cast(obj);

		if (obj != null) {
			return name.equals(other.name) &&
				isCompany == other.isCompany &&
				addedBy.equals(other.addedBy) &&
				addedOn.equals(other.addedOn);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, isCompany, addedBy, addedOn);
	}

	public static Client load(String name, Boolean isCompany, String addedBy, Date addedOn) {
		if (Utils.nonNullParameters(name, isCompany, addedBy, addedOn)) {
			return new Client(name, isCompany, addedBy, addedOn);
		} else {
			return null;
		}
	}

	public static Client load(HashMap<String, Object> map) {
		String name = Utils.safeCast(String.class, map.get("name"));
		Boolean isCompany = Utils.safeCast(Boolean.class, Boolean.class.cast(map.get("is_company")));
		String addedBy = Utils.safeCast(String.class, map.get("added_by"));
		Date addedOn = Utils.safeCast(Date.class, map.get("added_on"));

		return load(name, isCompany, addedBy, addedOn);
	}

	public static Client[] loadList(ArrayList<HashMap<String, Object>> list) {
		ArrayList<Client> clientList = new ArrayList<>(list.size());

		for (HashMap<String, Object> map : list) {
			Client client = load(map);

			if (client != null) {
				clientList.add(client);
			}
		}

		return clientList.toArray(Client[]::new);
	}
}
