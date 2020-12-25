package me.web_server.controller.rest.sales;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.ServiceRequestException;
import me.web_server.Utils;
import me.web_server.controller.rest.GenericRestController;
import me.web_server.Hasher;
import me.web_server.model.SaleUnit;
import me.web_server.service.GenericService;
import me.web_server.service.SaleService;

@RestController
@RequestMapping("/api/sales")
public class SalesController extends GenericRestController {
	@Autowired
	private SaleService saleService;

	@GetMapping("/{page}")
	public Callable<HashMap<String, Object>> getSaleReport(
		@PathVariable("page") Integer page,
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
		@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end
	) {
		return GenericService.handleAsyncRestRequest(
			() -> {
				return saleService.getSalesReport(username, Hasher.hash(password), start, end, page);
			}
		);
	}

	@GetMapping
	public Callable<HashMap<String, Object>> getSaleReportPageCount(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
		@RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end
	) {
		return GenericService.handleAsyncRestRequest(
			() -> {
				return saleService.getSalesReportPageCount(username, Hasher.hash(password), start, end);
			}
		);
	}

	@PutMapping
	public Callable<HashMap<String, Object>> addSale(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("client") String client,
		@RequestParam MultiValueMap<String, String> variadicParameters
	) {
		return GenericService.handleAsyncRestRequest(
			() -> {
				SaleUnit[] saleUnits;

				{
					ArrayList<SaleUnit> saleUnitList;
					{
						List<String> productsStringList = variadicParameters.get("product");

						{
							List<String> quantitiesStringList = variadicParameters.get("quantity");
							List<String> pricePerUnitStringList = variadicParameters.get("price_per_unit");

							if (productsStringList.size() != quantitiesStringList.size() || productsStringList.size() != pricePerUnitStringList.size()) {
								throw new ServiceRequestException("Products, quantities and prices per unit differ in count!");
							}

							saleUnitList = new ArrayList<>(productsStringList.size());

							try {
								for (int z = 0; z < productsStringList.size(); ++z) {
									Integer quantity = Integer.parseUnsignedInt(quantitiesStringList.get(z));
									Double pricePerUnit = Double.parseDouble(pricePerUnitStringList.get(z));

									if (quantity == 0) {
										throw new ServiceRequestException("Quantity cannot be equal to zero!");
									}

									if (pricePerUnit < 0) {
										throw new ServiceRequestException("Price per unit cannot be less than zero!");
									}

									Utils.ifNotNullThenElseThrowing(
										SaleUnit.load(productsStringList.get(z), quantity, pricePerUnit),
										(SaleUnit lamdbaSaleUnit) -> saleUnitList.add(lamdbaSaleUnit),
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

					saleUnits = saleUnitList.toArray(SaleUnit[]::new);
				}

				saleService.addSale(username, Hasher.hash(password), client, saleUnits);
				
				return null;
			}
		);
	}
}
