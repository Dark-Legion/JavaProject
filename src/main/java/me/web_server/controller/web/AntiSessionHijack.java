package me.web_server.controller.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.web_server.Utils;

abstract class AntiSessionHijack {
	private final static String REMOTE_ADDRESS_ATTRIBUTE = "___ASH__REMOTE_ADDRESS___";
	private final static String REMOTE_PORT_ATTRIBUTE = "___ASH__REMOTE_PORT___";

	private static String getRemoteAddress(HttpSession session) {
		return String.class.cast(session.getAttribute(REMOTE_ADDRESS_ATTRIBUTE));
	}

	private static void setRemoteAddress(HttpSession session, String remoteAddress) {
		session.setAttribute(REMOTE_ADDRESS_ATTRIBUTE, remoteAddress);
	}

	private static Integer getRemotePort(HttpSession session) {
		return Integer.class.cast(session.getAttribute(REMOTE_PORT_ATTRIBUTE));
	}

	private static void setRemotePort(HttpSession session, int remotePort) {
		session.setAttribute(REMOTE_PORT_ATTRIBUTE, remotePort);
	}

	private static void initializeSession(HttpSession session, HttpServletRequest request) {
		setRemoteAddress(session, request.getRemoteAddr());
		setRemotePort(session, request.getRemotePort());
	}

	static boolean validateSession(HttpSession session, HttpServletRequest request) {
		String remoteAddress = getRemoteAddress(session);
		Integer remotePort = getRemotePort(session);
		if (Utils.nonNullParameters(remoteAddress, remotePort)) {
			boolean result = remoteAddress.equals(request.getRemoteAddr()) && remotePort.equals(request.getRemotePort());

			if (!result) {
				session.invalidate();
			}

			return result;
		} else {
			initializeSession(session, request);

			return true;
		}
	}
}
