package me.web_server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

class ModelAndViews {
	final static ModelAndView ROOT = new ModelAndView("redirect:/index.html");
	// final static ModelAndView ROOT_REDIRECT = new ModelAndView("redirect:/");
	final static ModelAndView LOGIN_REDIRECT = new ModelAndView("redirect:/login");
	final static ModelAndView LOGIN = new ModelAndView("login");
	final static ModelAndView LOGIN_SELECT = new ModelAndView("redirect:/loginSelect.html");
	final static ModelAndView MAIN_REDIRECT = new ModelAndView("redirect:/main");
	final static ModelAndView ADMIN_MAIN = new ModelAndView("adminMain");
	final static ModelAndView SELLER_MAIN = new ModelAndView("sellerMain");
	final static ModelAndView INVALID_LOGIN = new ModelAndView("redirect:/invalidLogin.html", HttpStatus.NOT_ACCEPTABLE);
	final static ModelAndView INTERNAL_ERROR = new ModelAndView("redirect:/internalError.html", HttpStatus.INTERNAL_SERVER_ERROR);
}
