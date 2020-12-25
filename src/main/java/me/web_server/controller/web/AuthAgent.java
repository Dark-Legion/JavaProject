package me.web_server.controller.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import me.web_server.ServiceRequestException;
import me.web_server.Utils;
import me.web_server.service.AuthService;

@Component
public final class AuthAgent {
	@Autowired
	private AuthService authService;

	private final static String ADMIN_ATTRIBUTE = "admin";
	private final static String USERNAME_ATTRIBUTE = "user";
	private final static String PASSWORD_HASH_ATTRIBUTE = "pass";

	public interface AuthAgentCallable {
		Object call(String username, byte[] passwordHash);
	}

	public void enableLooseASH(HttpSession session) {
		AntiSessionHijack.enableLooseASH(session);
	}

	public Object authenticateAndCallHandler(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		AuthAgentCallable adminPage,
		AuthAgentCallable sellerPage,
		boolean loginRedirect
	) throws ServiceRequestException {
		if (!AntiSessionHijack.validateSession(session, request)) {
			return ErrorPage.error(model, "Invalid or invalidated session! Please, login again.");
		}

		Boolean admin = getAdmin(session);
		String username = getUsername(session);
		byte[] passwordHash = getPasswordHash(session);

		if (Utils.nonNullParameters(admin, username, passwordHash)) {
			if (authenticate(admin, username, passwordHash)) {
				return (admin ? adminPage.call(username, passwordHash) : sellerPage.call(username, passwordHash));
			}

			return ModelAndViews.INVALID_LOGIN;
		}

		return (loginRedirect ? ModelAndViews.LOGIN_REDIRECT : ModelAndViews.INVALID_LOGIN);
	}

	public Object authenticateAndCallHandlerLooseASH(
		HttpSession session,
		HttpServletRequest request,
		Model model,
		AuthAgentCallable adminPage,
		AuthAgentCallable sellerPage,
		boolean loginRedirect
	) throws ServiceRequestException {
		if (!AntiSessionHijack.validateSessionLoose(session, request)) {
			return ErrorPage.error(model, "Invalid or invalidated session! Please, login again.");
		}

		Boolean admin = getAdmin(session);
		String username = getUsername(session);
		byte[] passwordHash = getPasswordHash(session);

		if (Utils.nonNullParameters(admin, username, passwordHash)) {
			if (authenticate(admin, username, passwordHash)) {
				return (admin ? adminPage.call(username, passwordHash) : sellerPage.call(username, passwordHash));
			}

			return ModelAndViews.INVALID_LOGIN;
		}

		return (loginRedirect ? ModelAndViews.LOGIN_REDIRECT : ModelAndViews.INVALID_LOGIN);
	}

	public boolean authenticate(boolean admin, String username, byte[] passwordHash) throws ServiceRequestException {
		if (admin) {
			return authService.authenticateAdmin(username, passwordHash);
		} else {
			return authService.authenticateSeller(username, passwordHash);
		}
	}

	public boolean authenticate(HttpSession session) throws ServiceRequestException {
		return Utils.ifNotNullThenIfThrowing(
			getAdmin(session),
			(Boolean lambdaAdmin) -> authenticate(lambdaAdmin, getUsername(session), getPasswordHash(session))
		);
	}

	public Boolean getAdmin(HttpSession session) {
		return Boolean.class.cast(session.getAttribute(ADMIN_ATTRIBUTE));
	}

	public void setAdmin(HttpSession session, Boolean admin) {
		session.setAttribute(ADMIN_ATTRIBUTE, admin);
	}

	public String getUsername(HttpSession session) {
		return String.class.cast(session.getAttribute(USERNAME_ATTRIBUTE));
	}

	public void setUsername(HttpSession session, String username) {
		session.setAttribute(USERNAME_ATTRIBUTE, username);
	}

	public byte[] getPasswordHash(HttpSession session) {
		return byte[].class.cast(session.getAttribute(PASSWORD_HASH_ATTRIBUTE));
	}

	public void setPasswordHash(HttpSession session, byte[] passwordHash) {
		session.setAttribute(PASSWORD_HASH_ATTRIBUTE, passwordHash);
	}

	public void saveCredentials(HttpSession session, Boolean admin, String username, byte[] passwordHash) {
		setAdmin(session, admin);
		setUsername(session, username);
		setPasswordHash(session, passwordHash);
	}
}
