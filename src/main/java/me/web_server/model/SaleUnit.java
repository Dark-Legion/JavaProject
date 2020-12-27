package me.web_server.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import me.web_server.Utils;

public final class SaleUnit implements Serializable {
	private final static long serialVersionUID = 0;

	public final String product;
	public final double pricePerUnit;
	public final int quantity;

	private SaleUnit(String product, double pricePerUnit, int quantity) {
		super();

		this.product = product;
		this.pricePerUnit = pricePerUnit;
		this.quantity = quantity;
	}

	@Override
	public boolean equals(Object obj) {
		SaleUnit other = SaleUnit.class.cast(obj);

		if (obj != null) {
			return product.equals(other.product) &&
				pricePerUnit == other.pricePerUnit &&
				quantity == other.quantity;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(product, pricePerUnit, quantity);
	}

	@Override
	public String toString() {
		return "(\"" + product + "\", " + pricePerUnit + ", " + quantity + ")";
	}

	public static SaleUnit load(String product, Double pricePerUnit, Integer quantity) {
		if (Utils.nonNullParameters(product, pricePerUnit, quantity)) {
			return new SaleUnit(product, pricePerUnit, quantity);
		} else {
			return null;
		}
	}

	public static SaleUnit load(HashMap<String, Object> map) {
		String product = Utils.safeCast(String.class, map.get("product"));
		Double pricePerUnit = Utils.safeCast(Double.class, map.get("price_per_unit"));
		Integer quantity = Utils.safeCast(Integer.class, map.get("quantity"));

		return load(product, pricePerUnit, quantity);
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
