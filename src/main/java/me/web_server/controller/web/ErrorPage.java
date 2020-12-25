package me.web_server.controller.web;

import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

public final class ErrorPage {
	private ErrorPage() {
		super();
	}

	public static ModelAndView error(Model model, String error) {
		model.addAttribute("error", error);

		return ModelAndViews.ERROR;
	}
}
