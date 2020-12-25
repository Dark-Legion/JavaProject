package me.web_server.controller.web;

import java.util.concurrent.Callable;

import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

public class ModelAndViews {
	public final static ModelAndView ROOT = new ModelAndView("root");
	public final static ModelAndView LOGIN_REDIRECT = new ModelAndView("redirect:/login");
	public final static Callable<ModelAndView> LOGIN_REDIRECT_CALLABLE = new Callable<ModelAndView>(){
		public ModelAndView call() {
			return LOGIN_REDIRECT;
		};
	};
	public final static ModelAndView LOGIN = new ModelAndView("login/login");
	public final static ModelAndView LOGIN_SELECT = new ModelAndView("login/select");
	public final static ModelAndView MAIN_REDIRECT = new ModelAndView("redirect:/main");
	public final static ModelAndView MAIN = new ModelAndView("main");
	public final static ModelAndView PAGE_SELECT = new ModelAndView("manage/pageSelect");
	public final static ModelAndView MANAGE_CLIENTS = new ModelAndView("manage/clients");
	public final static ModelAndView MANAGE_PRODUCTS = new ModelAndView("manage/products");
	public final static ModelAndView MANAGE_USERS = new ModelAndView("manage/users");
	public final static ModelAndView MANAGE_USERS_SELF = new ModelAndView("manage/users/self");
	public final static ModelAndView INVALID_LOGIN = new ModelAndView("login/invalid", HttpStatus.NOT_ACCEPTABLE);
	public final static ModelAndView INVALID_SESSION = new ModelAndView("invalidSession", HttpStatus.BAD_REQUEST);
	public final static ModelAndView ERROR = new ModelAndView("error", HttpStatus.SERVICE_UNAVAILABLE);
}
