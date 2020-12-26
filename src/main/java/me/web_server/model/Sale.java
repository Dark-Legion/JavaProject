package me.web_server.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import me.web_server.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public final class Sale implements Serializable {
	private final static long serialVersionUID = 0;

	public final String seller;
	public final String client;
	public final String soldOn;
	public final SaleUnit[] products;

	private Sale(String seller, String client, Date soldOn, SaleUnit[] products) {
		super();

		this.seller = seller;
		this.client = client;
		this.soldOn = Utils.formatDateIso(soldOn);
		this.products = products;
	}

	@Override
	public boolean equals(Object obj) {
		Sale other = Sale.class.cast(obj);

		if (obj != null) {
			return seller.equals(other.seller) &&
				client.equals(other.client) &&
				soldOn.equals(other.soldOn) &&
				Arrays.equals(products, other.products);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(seller, client, soldOn, Arrays.deepHashCode(products));
	}

	public static Sale load(String seller, String client, Date soldOn, SaleUnit[] products) {
		if (Utils.nonNullParameters(seller, client, soldOn, products)) {
			return new Sale(seller, client, soldOn, products);
		} else {
			return null;
		}
	}

	public static Sale load(HashMap<String, Object> map) {
		String seller = Utils.safeCast(String.class, map.get("seller"));
		String client = Utils.safeCast(String.class, map.get("client"));
		Date soldOn = Utils.safeCast(Date.class, map.get("sold_on"));
		SaleUnit[] products = Utils.safeTransformArray(SaleUnit.class, Utils.safeCast(Object[].class, map.get("products")));

		return load(seller, client, soldOn, products);
	}

	public static Sale[] loadList(ArrayList<HashMap<String, Object>> list) {
		ArrayList<Sale> saleList = new ArrayList<>(list.size());

		for (HashMap<String, Object> map : list) {
			Utils.ifNotNullThen(load(map), (Sale lamdbaSale) -> saleList.add(lamdbaSale));
		}

		return saleList.toArray(Sale[]::new);
	}
}
