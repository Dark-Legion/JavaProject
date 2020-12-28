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

import me.web_server.Hasher;
import me.web_server.controller.web.AuthAgent;
import me.web_server.controller.web.ErrorPage;
import me.web_server.controller.web.ModelAndViews;
import me.web_server.model.Tuple2;
import me.web_server.service.UserService;
import me.web_server.service.GenericService;

@Controller
@RequestMapping("/add/user")
public class AddUserController {
	private final static LinkedHashMap<String, Tuple2<String, String>> FIELDS;
	private final static LinkedHashMap<String, String> NO_VALUES = new LinkedHashMap<>();

	static {
		FIELDS = new LinkedHashMap<>();

		FIELDS.put("name", new Tuple2<>("User", "text"));
		FIELDS.put("pass", new Tuple2<>("Password", "password"));
		FIELDS.put("is_admin", new Tuple2<>("Is admin", "checkbox"));
	}

	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private UserService userService;

	@GetMapping
	public Callable<Object> blankAddUserForm(
		HttpSession session,
		HttpServletRequest request,
		Model model
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("pageTitle", "Add user");

					model.addAttribute("fields", FIELDS);
					model.addAttribute("values", NO_VALUES);

					return ModelAndViews.ADD;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> addUser(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("name") String user,
		@RequestParam("pass") String password,
		@RequestParam(name = "is_admin", required = false) Boolean isAdmin
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					model.addAttribute("pageTitle", "Add user");

					if (user.isEmpty() || password.isEmpty()) {
						model.addAttribute("error", "All fields must be filled in!");
					} else {
						if (userService.userExists(username, passwordHash, user)) {
							model.addAttribute("error", "User with this name already exists!");
						} else {
							userService.addUser(username, passwordHash, user, Hasher.hash(password), isAdmin != null);

							return ModelAndViews.MAIN_REDIRECT;
						}
					}

					model.addAttribute("fields", FIELDS);

					{
						LinkedHashMap<String, String> values = new LinkedHashMap<>();

						if (!user.isEmpty()) {
							values.put("name", user);
						}

						if (isAdmin != null) {
							values.put("is_admin", "");
						}

						model.addAttribute("values", values);
					}

					return ModelAndViews.ADD;
				},
				(String username, byte[] passwordHash) -> ErrorPage.error(model, "Insufficient privileges!"),
				true
			),
			model
		);
	}
}
