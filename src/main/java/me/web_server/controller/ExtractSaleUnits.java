package me.web_server.controller;

import java.util.List;

import org.springframework.util.MultiValueMap;

import me.web_server.ServiceRequestException;
import me.web_server.Utils;
import me.web_server.model.SaleUnit;

public final class ExtractSaleUnits {
	private ExtractSaleUnits() {
		super();
	}

	public static SaleUnit[] extractSaleUnits(MultiValueMap<String, String> map) throws ServiceRequestException {
		SaleUnit[] saleUnits;

		{
			List<String> productsStringList = map.get("product");

			{
				List<String> pricePerUnitStringList = map.get("price_per_unit");
				List<String> quantitiesStringList = map.get("quantity");

				if (productsStringList == null || pricePerUnitStringList == null || quantitiesStringList == null) {
					throw new ServiceRequestException("At least one product must be added to the sale!");
				}

				if (productsStringList.size() != pricePerUnitStringList.size() || productsStringList.size() != quantitiesStringList.size()) {
					throw new ServiceRequestException("Products, prices per unit and quantities differ in count!");
				}

				saleUnits = new SaleUnit[productsStringList.size()];

				try {
					for (int z = 0; z < productsStringList.size(); ++z) {
						Double pricePerUnit = Double.parseDouble(pricePerUnitStringList.get(z));
						Integer quantity = Integer.parseUnsignedInt(quantitiesStringList.get(z));

						if (quantity == 0) {
							throw new ServiceRequestException("Quantity cannot be equal to zero!");
						}

						if (pricePerUnit < 0) {
							throw new ServiceRequestException("Price per unit cannot be less than zero!");
						}

						final int lambdaZ = z;

						Utils.ifNotNullThenElseThrowing(
							SaleUnit.load(productsStringList.get(z), pricePerUnit, quantity),
							(SaleUnit lamdbaSaleUnit) -> saleUnits[lambdaZ] = lamdbaSaleUnit,
							() -> {
								throw new ServiceRequestException("Internal error occured!");
							}
						);
					}
				} catch (NumberFormatException exception) {
					throw new ServiceRequestException("Invalid quantity value passed!");
				}
			}
		}

		return saleUnits;
	}
}
