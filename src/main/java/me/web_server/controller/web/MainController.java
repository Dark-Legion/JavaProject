package me.web_server.controller.web;

import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import me.web_server.ServiceRequestException;
import me.web_server.service.GenericService;

@Controller
@RequestMapping("/main")
public class MainController {
	@Autowired
	private AuthAgent authAgent;

	@GetMapping
	public Callable<Object> mainGet(HttpSession session, HttpServletRequest request, Model model) throws ServiceRequestException {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandlerLooseASH(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("user", username);
					model.addAttribute("admin", true);

					return ModelAndViews.MAIN;
				},
				(String username, byte[] passwordHash) -> {
					model.addAttribute("user", username);
					model.addAttribute("admin", false);

					return ModelAndViews.MAIN;
				},
				true
			),
			model
		);
	}
}
