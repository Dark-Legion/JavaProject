package me.web_server.controller.rest;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.service.AuthService;
import me.web_server.service.GenericService;

@RequestMapping("/api/auth")
@RestController
public class AuthRestController extends GenericRestController {
	@Autowired
	private AuthService authService;

	@GetMapping("/admin")
	public Callable<HashMap<String, Object>> authAdmin(
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(() -> authService.authenticateAdmin(username, Hasher.hash(password)));
	}

	@GetMapping("/seller")
	public Callable<HashMap<String, Object>> authSeller(
		@RequestParam("user") String username,
		@RequestParam("pass") String password
	) {
		return GenericService.handleAsyncRestRequest(() -> authService.authenticateSeller(username, Hasher.hash(password)));
	}
}
