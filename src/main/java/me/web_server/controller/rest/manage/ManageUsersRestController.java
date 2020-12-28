package me.web_server.controller.rest.manage;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.service.GenericService;
import me.web_server.service.UserService;

@RequestMapping("/api/manage/users")
@RestController
public class ManageUsersRestController {
	@Autowired
	private UserService userService;

	@GetMapping
	public Callable<HashMap<String, Object>> getUserListPageCount(
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(
			() -> userService.getUserListPageCount(username, Hasher.hash(password))
		);
	}

	@GetMapping("/{page}")
	public Callable<HashMap<String, Object>> getUserList(
		@PathVariable("page") Integer page,
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(
			() -> userService.getUserList(username, Hasher.hash(password), page)
		);
	}

	@PutMapping
	public Callable<HashMap<String, Object>> addUser(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("name") String newUsername,
		@RequestParam("user_pass") String newPassword,
		@RequestParam("is_admin") Boolean isAdmin
	) {
		return GenericService.handleAsyncRestRequest(
			() -> {
				if (newPassword.isEmpty()) {
					return GenericService.getErrorResultMap("New user's password must not be empty!");
				}

				return userService.addUser(
					username,
					Hasher.hash(password),
					newUsername,
					Hasher.hash(newPassword),
					isAdmin
				);
			}
		);
	}

	@PostMapping
	public Callable<HashMap<String, Object>> changeUser(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("change") String user,
		@RequestParam(name = "name", required = false) String newName,
		@RequestParam(name = "new_pass", required = false) String newPassword
	) {
		return GenericService.handleAsyncRestRequest(
			() -> {
				if (newPassword != null) {
					if (newPassword.isEmpty()) {
						return GenericService.getErrorResultMap("User's new password must not be empty!");
					}
				}

				return userService.changeUser(
					username,
					Hasher.hash(password),
					user,
					newName,
					(newPassword == null ? null : Hasher.hash(newPassword))
				);
			}
		);
	}

	@DeleteMapping
	public Callable<HashMap<String, Object>> deleteUser(
		@RequestParam("user") String username,
		@RequestParam("pass") String password,
		@RequestParam("name") String user,
		@RequestParam("reason") String reason
	) {
		return GenericService.handleAsyncRestRequest(
			() -> userService.deleteUser(
				username,
				Hasher.hash(password),
				user,
				reason
			)
		);
	}
}
