package me.web_server.controller.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import me.web_server.Utils;

abstract class AntiSessionHijack {
	private final static String REMOTE_ADDRESS_ATTRIBUTE = "___ASH__REMOTE_ADDRESS___";
	private final static String REMOTE_PORT_ATTRIBUTE = "___ASH__REMOTE_PORT___";
	private final static String MAY_CHANGE_PORT_ATTRIBUTE = "___ASH__MAY_CHANGE_PORT___";

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

	private static Boolean getMayChangePort(HttpSession session) {
		return Boolean.class.cast(session.getAttribute(MAY_CHANGE_PORT_ATTRIBUTE));
	}

	private static void setMayChangePort(HttpSession session, boolean mayChangePort) {
		session.setAttribute(MAY_CHANGE_PORT_ATTRIBUTE, mayChangePort);
	}

	static void enableLooseASH(HttpSession session) {
		session.setAttribute(MAY_CHANGE_PORT_ATTRIBUTE, true);
	}

	private static void initializeSession(HttpSession session, HttpServletRequest request) {
		setRemoteAddress(session, request.getRemoteAddr());
		setRemotePort(session, request.getRemotePort());
		setMayChangePort(session, false);
	}

	static boolean validateSession(HttpSession session, HttpServletRequest request) {
		String remoteAddress = getRemoteAddress(session);
		Integer remotePort = getRemotePort(session);
		if (Utils.nonNullParameters(remoteAddress, remotePort)) {
			boolean result = remoteAddress.equals(request.getRemoteAddr()) && remotePort.equals(request.getRemotePort());

			setMayChangePort(session, false);

			if (!result) {
				session.invalidate();
			}

			return result;
		} else {
			initializeSession(session, request);

			return true;
		}
	}

	static boolean validateSessionLoose(HttpSession session, HttpServletRequest request) {
		String remoteAddress = getRemoteAddress(session);
		Integer remotePort = getRemotePort(session);
		if (Utils.nonNullParameters(remoteAddress, remotePort)) {
			boolean result = remoteAddress.equals(request.getRemoteAddr());

			{
				if (Utils.ifNotNullThenIf(getMayChangePort(session), (x) -> x)) {
					setMayChangePort(session, false);

					setRemotePort(session, request.getRemotePort());

					return result;
				}
			}

			result &= remotePort.equals(request.getRemotePort());

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
