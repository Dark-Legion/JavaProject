package me.web_server.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import me.web_server.service.IUserService;

@Controller
@SessionAttributes("credentials")
public class WebController {
	@Autowired
	private IUserService userService;

	private boolean nonNullParameters(Object object) {
		return object != null;
	}

	private boolean nonNullParameters(Object object, Object... objects) {
		return nonNullParameters(object) && nonNullParameters(objects);
	}

	@ModelAttribute("credentials")
	public Credentials newUserCredentials() {
		return new Credentials();
	}

	@GetMapping("/")
	public ModelAndView rootGet(HttpServletRequest request) {
		Boolean admin = Boolean.class.cast(request.getAttribute("admin"));
		String username = String.class.cast(request.getAttribute("user"));
		byte[] passwordHash = byte[].class.cast(request.getAttribute("pass"));

		if (nonNullParameters(admin, username, passwordHash)) {
			if (admin) {
				if (userService.authenticateAdmin(username, passwordHash)) {
					return ModelAndViews.MAIN_REDIRECT;
				} else {
					return ModelAndViews.INVALID_LOGIN;
				}
			} else {
				if (userService.authenticateSeller(username, passwordHash)) {
					return ModelAndViews.MAIN_REDIRECT;
				} else {
					return ModelAndViews.INVALID_LOGIN;
				}
			}
		}

		return ModelAndViews.LOGIN_REDIRECT;
	}

	private static void saveCredentials(@ModelAttribute("credentials") Credentials credentials, boolean admin, String username, byte[] passwordHash) {
		credentials.setAdmin(Boolean.valueOf(admin));
		credentials.setUsername(username);
		credentials.setPasswordHash(passwordHash);
	}

    @GetMapping("/login")
    public ModelAndView loginGet() {
        return ModelAndViews.LOGIN_TYPE_SELECT;
    }

	@PostMapping("/login")
	public ModelAndView loginPost(
		@ModelAttribute("credentials") Credentials credentials,
		@RequestParam("loginType") Optional<String> loginTypeOption,
		@RequestParam("user") Optional<String> username,
		@RequestParam("pass") Optional<String> password
	) {
		Boolean admin = credentials.getAdmin();

		if (loginTypeOption.isPresent()) {
			String loginType = loginTypeOption.get();

			if (loginType.equals("admin") || loginType.equals("seller")) {
				credentials.setAdmin(Boolean.valueOf(loginType.equals("admin")));

				return ModelAndViews.LOGIN;
			} else {
				return ModelAndViews.INVALID_LOGIN;
			}
		} else if (nonNullParameters(admin) && username.isPresent() && password.isPresent()) {
			try {
				byte[] passwordHash;

				{
					final MessageDigest digest = MessageDigest.getInstance("SHA3-256");
					passwordHash = digest.digest(password.get().getBytes(StandardCharsets.UTF_8));
				}

				if (admin) {
					if (userService.authenticateAdmin(username.get(), passwordHash)) {
						saveCredentials(credentials, true, username.get(), passwordHash);

						return ModelAndViews.MAIN_REDIRECT;
					}
				} else {
					if (userService.authenticateSeller(username.get(), passwordHash)) {
						saveCredentials(credentials, false, username.get(), passwordHash);

						return ModelAndViews.MAIN_REDIRECT;
					}
				}

				return ModelAndViews.INVALID_LOGIN;
			} catch (NoSuchAlgorithmException exception) {
				return ModelAndViews.INTERNAL_ERROR;
			}
		} else {
			return ModelAndViews.LOGIN_TYPE_SELECT;
		}
	}

	@GetMapping("/main")
	public ModelAndView mainGet(HttpServletRequest request) {
		return ModelAndViews.ADMIN_MAIN;
	}
}
