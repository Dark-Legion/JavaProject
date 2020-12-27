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

import me.web_server.ServiceRequestException;
import me.web_server.Utils;
import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Sale;
import me.web_server.service.GenericService;
import me.web_server.service.SalesService;

@Controller
@RequestMapping("/sales")
public class SalesController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private SalesService salesService;

	interface IServiceRequestCallable<T> {
		T call() throws ServiceRequestException;
	}

	static Object getSalesReport(
		Model model,
		String username,
		byte[] passwordHash,
		Date start,
		Date end,
		Integer page,
		IServiceRequestCallable<Sale[]> reportCallable,
		IServiceRequestCallable<Integer> pageCountCallable
	) throws ServiceRequestException {
		if (Utils.nonNullParameters(start, end)) {
			model.addAttribute("start", Utils.formatDateIso(start));
			model.addAttribute("end", Utils.formatDateIso(end));

			int pageCount = pageCountCallable.call();

			if (page != null) {
				if (0 < page && page <= pageCount) {
					model.addAttribute("sales", reportCallable.call());

					if (page > 1) {
						model.addAttribute("prev", page - 1);
					}

					if (page != pageCount) {
						model.addAttribute("next", page + 1);
					}

					return ModelAndViews.SALES_REPORT;
				}
			}

			model.addAttribute("pages", pageCount);
		}

		return ModelAndViews.SALES_REPORT_SELECT;
	}

	@GetMapping
	public Callable<Object> getUsers(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam(name = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date start,
		@RequestParam(name = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date end,
		@RequestParam(name = "page", required = false) Integer page
	) {
		model.addAttribute("specificSeller", false);

		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("hideSeller", false);

					return getSalesReport(
						model,
						username,
						passwordHash,
						start,
						end,
						page,
						() -> Sale.loadList(salesService.getSalesReport(username, passwordHash, start, end, page)),
						() -> salesService.getSalesReportPageCount(username, passwordHash, start, end)
					);
				},
				(String username, byte[] passwordHash) -> {
					model.addAttribute("hideSeller", true);

					return getSalesReport(
						model,
						username,
						passwordHash,
						start,
						end,
						page,
						() -> Sale.loadList(salesService.getSalesReportForSeller(username, passwordHash, username, start, end, page)),
						() -> salesService.getSalesReportForSellerPageCount(username, passwordHash, username, start, end)
					);
				},
				true
			),
			model
		);
	}
}
