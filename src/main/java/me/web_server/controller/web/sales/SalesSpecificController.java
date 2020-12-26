package me.web_server.controller.web.sales;

import java.util.Date;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Sale;
import me.web_server.service.GenericService;
import me.web_server.service.SalesService;

@Controller
@RequestMapping("/sales/specific")
public class SalesSpecificController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private SalesService salesService;

	@GetMapping
	public Callable<Object> getUsers(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("seller") String seller,
		@RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
		@RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end,
		@RequestParam(name = "page", required = false) Integer page
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("seller", seller);

					model.addAttribute("hideSeller", true);

					return SalesController.getSalesReport(
						model,
						username,
						passwordHash,
						start,
						end,
						page,
						() -> Sale.loadList(salesService.getSalesReportForSeller(username, passwordHash, seller, start, end, page)),
						() -> salesService.getSalesReportForSellerPageCount(username, passwordHash, seller, start, end)
					);
				},
				(String username, byte[] passwordHash) -> {
					model.addAttribute("error", "Insufficient privileges!");

					return ModelAndViews.ERROR;
				},
				true
			),
			model
		);
	}
}
