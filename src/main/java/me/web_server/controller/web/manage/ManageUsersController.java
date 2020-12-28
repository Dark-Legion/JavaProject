package me.web_server.controller.web.manage;

import java.util.HashMap;
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

import me.web_server.Hasher;
import me.web_server.ServiceRequestException;
import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ErrorPage;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.User;
import me.web_server.service.GenericService;
import me.web_server.service.UserService;

@Controller
@RequestMapping("/manage/users")
public class ManageUsersController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private UserService userService;

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
					int pageCount = userService.getUserListPageCount(username, passwordHash);

					if (page != null) {
						if (0 < page && page <= pageCount) {
							model.addAttribute("users", User.loadList(userService.getUserList(username, passwordHash, page)));

							if (page > 1) {
								model.addAttribute("prev", page - 1);
							}

							if (page != pageCount) {
								model.addAttribute("next", page + 1);
							}

							return ModelAndViews.MANAGE_USERS;
						}
					}

					model.addAttribute("pages", pageCount);

					return ModelAndViews.PAGE_SELECT;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> changeDelete(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("action") String action,
		@RequestParam("name") String user,
		@RequestParam HashMap<String, String> variableParameters
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					boolean edit;

					if (action.equals("edit")) {
						edit = true;
					} else if (action.equals("delete")) {
						edit = false;
					} else {
						throw new ServiceRequestException("Invalid action!");
					}

					model.addAttribute("edit", edit);

					if (userService.userExists(username, passwordHash, user)) {
						model.addAttribute("name", user);
					} else {
						throw new ServiceRequestException("No such client exists!");
					}

					variableParameters.computeIfPresent(
						"new_name",
						(String key, String oldValue) -> oldValue.isBlank() ? null : oldValue
					);

					variableParameters.computeIfPresent(
						"new_pass",
						(String key, String oldValue) -> oldValue.isEmpty() ? null : oldValue
					);

					variableParameters.computeIfPresent(
						"reason",
						(String key, String oldValue) -> oldValue.isBlank() ? null : oldValue
					);

					if (edit) {
						if (variableParameters.containsKey("commitEdit")) {
							userService.changeUser(
								username,
								passwordHash,
								user,
								variableParameters.get("new_name"),
								Hasher.hash(variableParameters.get("new_pass"))
							);

							return ModelAndViews.MAIN_REDIRECT;
						}
					} else if (variableParameters.containsKey("confirmDelete")) {
						String reason = variableParameters.get("reason");

						if (reason == null) {
							model.addAttribute("error", "No delete reason provided!");
						} else {
							try {
								userService.deleteUser(username, passwordHash, user, reason);

								return ModelAndViews.MAIN_REDIRECT;
							} catch (ServiceRequestException exception) {
								model.addAttribute("error", exception.getMessage());
							}
						}
					}

					return ModelAndViews.CHANGE_DELETE_USER;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}
}
