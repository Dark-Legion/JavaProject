package me.web_server.controller.rest;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.web_server.Hasher;
import me.web_server.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	private UserService userService;

	@GetMapping("/admin")
	public Callable<HashMap<String, Boolean>> authAdmin(@RequestParam("user") String username, @RequestParam("pass") String password) {
		return new Callable<HashMap<String, Boolean>>(){
			@Override
			public HashMap<String, Boolean> call() throws Exception {
				HashMap<String, Boolean> result = new HashMap<>(1);
				result.put("success", userService.authenticateAdmin(username, Hasher.hash(password)));
				return result;
			}
		};
	}

	@GetMapping("/seller")
	public Callable<HashMap<String, Boolean>> authSeller(@RequestParam("user") String username, @RequestParam("pass") String password) {
		return new Callable<HashMap<String, Boolean>>(){
			@Override
			public HashMap<String, Boolean> call() throws Exception {
				HashMap<String, Boolean> result = new HashMap<>(1);
				result.put("success", userService.authenticateSeller(username, Hasher.hash(password)));
				return result;
			}
		};
	}
}
