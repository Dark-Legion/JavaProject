package me.web_server.controller.rest.manage;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.service.GenericService;
import me.web_server.service.UserService;

@RequestMapping("/api/manage/users/self")
@RestController
public class ManageUsersSelfRestController {
	@Autowired
	private UserService userService;

	@PostMapping
	public Callable<HashMap<String, Object>> changeUserPassword(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("new_pass") String newPassword
	) {
		return GenericService.handleAsyncRestRequest(
			() -> userService.changeUserPassword(username, Hasher.hash(password), Hasher.hash(newPassword))
		);
	}
}
