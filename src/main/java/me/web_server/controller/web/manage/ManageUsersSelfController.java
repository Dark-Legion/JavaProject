package me.web_server.controller.web.manage;

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
import me.web_server.controller.web.ModelAndViews;
import me.web_server.service.GenericService;
import me.web_server.service.UserService;

@Controller
@RequestMapping("/manage/users/self")
public class ManageUsersSelfController {
	@Autowired
	private AuthAgent authAgent;
	@Autowired
	private UserService userService;

	@GetMapping
	public Callable<Object> changePasswordGet(
		HttpSession session,
		HttpServletRequest request,
		Model model
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> ModelAndViews.MANAGE_USERS_SELF,
				(String username, byte[] passwordHash) -> ModelAndViews.MANAGE_USERS_SELF,
				true
			),
			model
		);
	}

	@PostMapping
	public Callable<Object> changePasswordGet(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("new_pass") String newPassword
	) {
		return GenericService.handleAsyncWebRequest(
			() -> authAgent.authenticateAndCallHandler(
				session,
				request,
				model,
				(String username, byte[] passwordHash) -> {
					return GenericService.handleWebRequest(
						() -> {
							byte[] newPasswordHash = Hasher.hash(newPassword);

							userService.changeUserPassword(username, passwordHash, newPasswordHash);

							authAgent.setPasswordHash(session, newPasswordHash);

							return ModelAndViews.MAIN_REDIRECT;
						},
						model
					);
				},
				(String username, byte[] passwordHash) -> {
					return GenericService.handleWebRequest(
						() -> {
							byte[] newPasswordHash = Hasher.hash(newPassword);

							userService.changeUserPassword(username, passwordHash, newPasswordHash);

							authAgent.setPasswordHash(session, newPasswordHash);

							return ModelAndViews.MAIN_REDIRECT;
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
