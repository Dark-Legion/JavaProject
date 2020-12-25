package me.web_server.controller.web;

import java.util.Optional;
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
import org.springframework.web.servlet.ModelAndView;

import me.web_server.Hasher;
import me.web_server.service.GenericService;

@Controller
@RequestMapping("/login")
public class LoginController {
	@Autowired
	private AuthAgent authAgent;

    @GetMapping
    public ModelAndView loginGet() {
        return ModelAndViews.LOGIN_SELECT;
	}

	@PostMapping
	public Callable<Object> loginPost(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		@RequestParam("loginType") Optional<String> loginTypeOptional,
		@RequestParam("user") Optional<String> usernameOptional,
		@RequestParam("pass") Optional<String> passwordOptional
	) {
		return GenericService.handleAsyncWebRequest(
			() -> {
				Boolean admin = authAgent.getAdmin(session);
		
				if (loginTypeOptional.isPresent()) {
					String loginType = loginTypeOptional.get();
		
					if (loginType.equals("admin") || loginType.equals("seller")) {
						authAgent.setAdmin(session, loginType.equals("admin"));
		
						return ModelAndViews.LOGIN;
					} else {
						return ModelAndViews.INVALID_LOGIN;
					}
				} else if (admin != null && usernameOptional.isPresent() && passwordOptional.isPresent()) {
					String username = usernameOptional.get();
					String password = passwordOptional.get();
		
					final byte[] passwordHash = Hasher.hash(password);
		
					if (authAgent.authenticate(admin, username, passwordHash)) {
						authAgent.saveCredentials(session, admin, username, passwordHash);
		
						return ModelAndViews.MAIN_REDIRECT;
					} else {
						return ModelAndViews.INVALID_LOGIN;
					}
				} else {
					return ModelAndViews.LOGIN_SELECT;
				}
			},
			model
		);
	}
}
