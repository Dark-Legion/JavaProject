package me.web_server.controller.rest.sales;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.controller.rest.GenericRestController;
import me.web_server.Hasher;
import me.web_server.service.GenericService;
import me.web_server.service.SaleService;

@RestController
@RequestMapping("/api/sales/self")
public class SalesSpecificController extends GenericRestController {
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
				return saleService.getSalesReportForSeller(username, Hasher.hash(password), username, start, end, page);
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
				return saleService.getSalesReportForSellerPageCount(username, Hasher.hash(password), username, start, end);
			}
		);
	}
}
