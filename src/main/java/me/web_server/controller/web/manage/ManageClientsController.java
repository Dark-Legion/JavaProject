package me.web_server.controller.web.manage;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Client;
import me.web_server.service.ClientService;
import me.web_server.service.GenericService;

@Controller
@RequestMapping("/manage/clients")
public class ManageClientsController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private ClientService clientService;

	@GetMapping
	public Callable<Object> getUsers(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam(name = "page", required = false) Integer page
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("error", "Insufficient privileges!");

					return ModelAndViews.ERROR;
				},
				(String username, byte[] passwordHash) -> {
					return GenericService.handleWebRequest(
						() -> {
							int pageCount = clientService.getClientListPageCount(username, passwordHash);

							if (page != null) {
								if (0 < page && page <= pageCount) {
									model.addAttribute("clients", Client.loadList(clientService.getClientList(username, passwordHash, page)));

									if (page > 1) {
										model.addAttribute("prev", page - 1);
									}

									if (page != pageCount) {
										model.addAttribute("next", page + 1);
									}

									return ModelAndViews.MANAGE_CLIENTS;
								}
							}

							model.addAttribute("pages", pageCount);

							return ModelAndViews.PAGE_SELECT;
						},
						model
					);
				},
				true
			),
			model
		);
	}
}
