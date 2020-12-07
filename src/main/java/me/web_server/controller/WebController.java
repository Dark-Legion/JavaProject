package me.web_server.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import me.web_server.service.IUserService;

@Controller
public class WebController {
	@Autowired
	private IUserService userService;

	private final static String ADMIN_ATTRIBUTE = "admin";
	private final static String USERNAME_ATTRIBUTE = "user";
	private final static String PASSWORD_HASH_ATTRIBUTE = "pass";

	private interface ModelAndViewCallable {
		abstract ModelAndView get(String username, byte[] passwordHash);
	}

	private boolean nonNullParameters(Object... objects) {
		if (objects == null) {
			return false;
		}
		
		for (Object object : objects) {
			if (object == null) {
				return false;
			}
		}

		return true;
	}

	public ModelAndView authenticateAndCallHandler(HttpSession session, HttpServletRequest request, ModelAndViewCallable adminPage, ModelAndViewCallable sellerPage, boolean loginRedirect) {
		if (!AntiSessionHijack.validateSession(session, request)) {
			return ModelAndViews.INVALID_LOGIN;
		}

		Boolean admin = getAdmin(session);
		String username = getUsername(session);
		byte[] passwordHash = getPasswordHash(session);

		if (nonNullParameters(admin, username, passwordHash)) {
			if (admin) {
				if (userService.authenticateAdmin(username, passwordHash)) {
					return adminPage.get(username, passwordHash);
				} else {
					return ModelAndViews.INVALID_LOGIN;
				}
			} else {
				if (userService.authenticateSeller(username, passwordHash)) {
					return sellerPage.get(username, passwordHash);
				} else {
					return ModelAndViews.INVALID_LOGIN;
				}
			}
		}

		return (loginRedirect ? ModelAndViews.LOGIN_REDIRECT : ModelAndViews.INVALID_LOGIN);
	}

	@GetMapping("/")
	public ModelAndView rootGet() {
		return ModelAndViews.ROOT;
	}

	private static Boolean getAdmin(HttpSession session) {
		return Boolean.class.cast(session.getAttribute(ADMIN_ATTRIBUTE));
	}

	private static void setAdmin(HttpSession session, boolean admin) {
		session.setAttribute(ADMIN_ATTRIBUTE, Boolean.valueOf(admin));
	}

	private static void setAdmin(HttpSession session, Boolean admin) {
		session.setAttribute(ADMIN_ATTRIBUTE, admin);
	}

	private static String getUsername(HttpSession session) {
		return String.class.cast(session.getAttribute(USERNAME_ATTRIBUTE));
	}

	private static void setUsername(HttpSession session, String username) {
		session.setAttribute(USERNAME_ATTRIBUTE, username);
	}

	private static byte[] getPasswordHash(HttpSession session) {
		return byte[].class.cast(session.getAttribute(PASSWORD_HASH_ATTRIBUTE));
	}

	private static void setPasswordHash(HttpSession session, byte[] passwordHash) {
		session.setAttribute(PASSWORD_HASH_ATTRIBUTE, passwordHash);
	}

	private static void saveCredentials(HttpSession session, Boolean admin, String username, byte[] passwordHash) {
		setAdmin(session, admin);
		setUsername(session, username);
		setPasswordHash(session, passwordHash);
	}

    @GetMapping("/login")
    public ModelAndView loginGet() {
        return ModelAndViews.LOGIN_SELECT;
    }

	@PostMapping("/login")
	public ModelAndView loginPost(
		HttpSession session,
		HttpServletRequest request,
		@RequestParam("loginType") Optional<String> loginTypeOptional,
		@RequestParam("user") Optional<String> usernameOptional,
		@RequestParam("pass") Optional<String> passwordOptional
	) {
		Boolean admin = getAdmin(session);

		if (loginTypeOptional.isPresent()) {
			String loginType = loginTypeOptional.get();

			if (loginType.equals("admin") || loginType.equals("seller")) {
				setAdmin(session, loginType.equals("admin"));

				return ModelAndViews.LOGIN;
			} else {
				return ModelAndViews.INVALID_LOGIN;
			}
		} else if (nonNullParameters(admin) && usernameOptional.isPresent() && passwordOptional.isPresent()) {
			String username = usernameOptional.get();
			String password = passwordOptional.get();

			try {
				byte[] passwordHash;

				{
					final MessageDigest digest = MessageDigest.getInstance("SHA3-512");
					passwordHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
				}

				boolean authenticationSuccessful = false;

				if (admin) {
					authenticationSuccessful = userService.authenticateAdmin(username, passwordHash);
				} else {
					authenticationSuccessful = userService.authenticateSeller(username, passwordHash);
				}

				if (authenticationSuccessful) {
					saveCredentials(session, admin, username, passwordHash);

					return ModelAndViews.MAIN_REDIRECT;
				} else {
					return ModelAndViews.INVALID_LOGIN;
				}
			} catch (NoSuchAlgorithmException exception) {
				return ModelAndViews.INTERNAL_ERROR;
			}
		} else {
			return ModelAndViews.LOGIN_SELECT;
		}
	}

	@GetMapping("/main")
	public ModelAndView mainGet(HttpSession session, HttpServletRequest request) {
		return authenticateAndCallHandler(
			session,
			request,
			(user, pass) -> { return ModelAndViews.ADMIN_MAIN; },
			(user, pass) -> { return ModelAndViews.SELLER_MAIN; },
			true
		);
	}
}
