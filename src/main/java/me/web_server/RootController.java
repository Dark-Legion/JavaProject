package me.web_server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
	@Autowired
	private IUserService userService;

	@PostMapping("/auth_admin")
	public boolean authAdmin(@RequestParam("user") String username, @RequestParam("pass") byte[] passwordHash) {
		return userService.authenticateAdmin(username, passwordHash);
	}

	@PostMapping("/auth_seller")
	public boolean authSeller(@RequestParam("user") String username, @RequestParam("pass") byte[] passwordHash) {
		return userService.authenticateSeller(username, passwordHash);
	}
}
