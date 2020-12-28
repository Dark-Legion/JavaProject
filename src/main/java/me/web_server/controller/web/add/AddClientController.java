package me.web_server.controller.web.add;

import java.util.LinkedHashMap;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ErrorPage;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Tuple2;
import me.web_server.service.ClientService;
import me.web_server.service.GenericService;

@Controller
@RequestMapping("/add/client")
public class AddClientController {
	private final static LinkedHashMap<String, Tuple2<String, String>> FIELDS;
	private final static LinkedHashMap<String, String> NO_VALUES = new LinkedHashMap<>();

	static {
		FIELDS = new LinkedHashMap<>();

		FIELDS.put("name", new Tuple2<>("Client", "text"));
		FIELDS.put("is_company", new Tuple2<>("Is company", "checkbox"));
	}

	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private ClientService clientService;

	@GetMapping
	public Callable<Object> blankAddClientForm(
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
				(String username, byte[] passwordHash) -> {
					model.addAttribute("pageTitle", "Add client");

					model.addAttribute("fields", FIELDS);
					model.addAttribute("values", NO_VALUES);

					return ModelAndViews.ADD;
				},
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> addClient(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("name") String client,
		@RequestParam(name = "is_company", required = false) Boolean isCompany
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				(String username, byte[] passwordHash) -> {
					model.addAttribute("pageTitle", "Add client");

					if (client.isEmpty()) {
						model.addAttribute("error", "All fields must be filled in!");
					} else {
						if (clientService.clientExists(username, passwordHash, client)) {
							model.addAttribute("error", "Client with this name already exists!");
						} else {
							clientService.addClient(username, passwordHash, client, isCompany != null);

							return ModelAndViews.MAIN_REDIRECT;
						}
					}

					model.addAttribute("fields", FIELDS);

					{
						LinkedHashMap<String, String> values = new LinkedHashMap<>();

						if (!client.isEmpty()) {
							values.put("name", client);
						}

						if (isCompany != null) {
							values.put("is_company", "");
						}

						model.addAttribute("values", values);
					}

					return ModelAndViews.ADD;
				},
				true
			),
			model
		);
	}
}
