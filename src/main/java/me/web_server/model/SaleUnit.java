package me.web_server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.web_server.Utils;

public final class SaleUnit implements Serializable {
	private final static long serialVersionUID = 0;

	public final String product;
	public final int quantity;
	public final double pricePerUnit;

	private SaleUnit(String product, int quantity, double pricePerUnit) {
		super();

		this.product = product;
		this.quantity = quantity;
		this.pricePerUnit = pricePerUnit;
	}

	@Override
	public boolean equals(Object obj) {
		SaleUnit other = SaleUnit.class.cast(obj);

		if (obj != null) {
			return product.equals(other.product) &&
				quantity == other.quantity;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(product, quantity, pricePerUnit);
	}

	@Override
	public String toString() {
		return "(\"" + product + "\", " + quantity + ", " + pricePerUnit + ")";
	}

	public static SaleUnit load(String product, Integer quantity, Double pricePerUnit) {
		if (Utils.nonNullParameters(product, quantity, pricePerUnit)) {
			return new SaleUnit(product, quantity, pricePerUnit);
		} else {
			return null;
		}
	}

	public static SaleUnit load(HashMap<String, Object> map) {
		String product = Utils.safeCast(String.class, map.get("product"));
		Integer quantity = Utils.safeCast(Integer.class, map.get("quantity"));
		Double pricePerUnit = Utils.safeCast(Double.class, map.get("price_per_unit"));

		return load(product, quantity, pricePerUnit);
	}

	public static SaleUnit[] loadList(ArrayList<HashMap<String, Object>> list) {
		ArrayList<SaleUnit> saleList = new ArrayList<>(list.size());

		for (HashMap<String, Object> map : list) {
			SaleUnit sale = load(map);

			if (sale != null) {
				saleList.add(sale);
			}
		}

		return saleList.toArray(SaleUnit[]::new);
	}
}
