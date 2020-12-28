package me.web_server.controller.web;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.web_server.ServiceRequestException;
import me.web_server.controller.ExtractSaleUnits;
import me.web_server.model.SaleUnit;
import me.web_server.service.ClientService;
import me.web_server.service.GenericService;
import me.web_server.service.SalesService;

@Controller
@RequestMapping("/sell")
public class SellController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private ClientService clientService;
	@Autowired
	private SalesService salesService;

	@GetMapping
	public Callable<Object> newSell(
		HttpSession session,
		HttpServletRequest request,
		Model model
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				(String username, byte[] passwordHash) -> ModelAndViews.SELL,
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> commitSale(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("name") String client,
		@RequestParam MultiValueMap<String, String> variadicParameters
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				(String username, byte[] passwordHash) -> {
					model.addAttribute("name", client);

					SaleUnit[] saleUnits = null;

					try {
						if (clientService.clientExists(username, passwordHash, client)) {
							saleUnits = ExtractSaleUnits.extractSaleUnits(variadicParameters);

							salesService.addSale(username, passwordHash, client, saleUnits);

							return ModelAndViews.MAIN_REDIRECT;
						} else {
							model.addAttribute("error", "No such client exists!");
						}
					} catch (ServiceRequestException exception) {
						model.addAttribute("error", exception.getMessage());
					}

					model.addAttribute("products", saleUnits);

					return ModelAndViews.SELL;
				},
				true
			),
			model
		);
	}
}
