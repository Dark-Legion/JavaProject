package me.web_server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import me.web_server.Utils;

public final class Product implements Serializable {
	private final static long serialVersionUID = 0;

	public final String name;
	public final double price;
	public final String addedBy;
	public final String addedOn;

	private Product(String name, double price, String addedBy, Date addedOn) {
		super();

		this.name = name;
		this.price = price;
		this.addedBy = addedBy;
		this.addedOn = Utils.formatDateIso(addedOn);
	}

	@Override
	public boolean equals(Object obj) {
		Product other = Product.class.cast(obj);

		if (obj != null) {
			return name.equals(other.name) &&
				price == other.price &&
				addedBy.equals(other.addedBy) &&
				addedOn.equals(other.addedOn);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, price, addedBy, addedOn);
	}

	public static Product load(String name, double price, String addedBy, Date addedOn) {
		if (Utils.nonNullParameters(name, price, addedBy, addedOn)) {
			return new Product(name, price, addedBy, addedOn);
		} else {
			return null;
		}
	}

	public static Product load(HashMap<String, Object> map) {
		String name = Utils.safeCast(String.class, map.get("name"));
		Double price = Utils.safeCast(Double.class, map.get("price"));
		String addedBy = Utils.safeCast(String.class, map.get("added_by"));
		Date addedOn = Utils.safeCast(Date.class, map.get("added_on"));

		return load(name, price, addedBy, addedOn);
	}

	public static Product[] loadList(ArrayList<HashMap<String, Object>> list) {
		ArrayList<Product> productList = new ArrayList<>(list.size());

		for (HashMap<String, Object> map : list) {
			Product product = load(map);

			if (product != null) {
				productList.add(product);
			}
		}

		return productList.toArray(Product[]::new);
	}
}
