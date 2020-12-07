package me.web_server.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

abstract class AntiSessionHijack {
    private final static String REMOTE_ADDRESS_ATTRIBUTE = "___ASH__REMOTE_ADDRESS___";
    private final static String REMOTE_PORT_ATTRIBUTE = "___ASH__REMOTE_PORT___";

    static void initializeSession(HttpSession session, HttpServletRequest request) {
        session.setAttribute(REMOTE_ADDRESS_ATTRIBUTE, request.getRemoteAddr());
        session.setAttribute(REMOTE_PORT_ATTRIBUTE, Integer.valueOf(request.getRemotePort()));
    }

    static boolean validateSession(HttpSession session, HttpServletRequest request) {
        String remoteAddress = String.class.cast(session.getAttribute(REMOTE_ADDRESS_ATTRIBUTE));
        Integer remotePort = Integer.class.cast(session.getAttribute(REMOTE_PORT_ATTRIBUTE));

        if (remoteAddress == null || remotePort == null) {
            initializeSession(session, request);

            return true;
        } else {
            boolean result = remoteAddress.equals(request.getRemoteAddr()) && remotePort.equals(Integer.valueOf(request.getRemotePort()));

            if (!result) {
                session.invalidate();
            }

            return result;
        }
    }
}
