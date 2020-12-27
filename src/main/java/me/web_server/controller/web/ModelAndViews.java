package me.web_server.controller.web;

import java.util.concurrent.Callable;

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
	public final static ModelAndView MANAGE_CLIENTS = new ModelAndView("manage/clients/list");
	public final static ModelAndView CHANGE_DELETE_CLIENT = new ModelAndView("manage/clients/changeDelete");
	public final static ModelAndView MANAGE_PRODUCTS = new ModelAndView("manage/products/list");
	public final static ModelAndView CHANGE_DELETE_PRODUCT = new ModelAndView("manage/products/changeDelete");
	public final static ModelAndView MANAGE_USERS = new ModelAndView("manage/users/list");
	public final static ModelAndView CHANGE_DELETE_USER = new ModelAndView("manage/users/changeDelete");
	public final static ModelAndView MANAGE_USERS_SELF = new ModelAndView("manage/users/self");
	public final static ModelAndView SELL = new ModelAndView("sell");
	public final static ModelAndView SALES_REPORT_SELECT = new ModelAndView("sales/select");
	public final static ModelAndView SALES_REPORT = new ModelAndView("sales/report");
	public final static ModelAndView INVALID_LOGIN = new ModelAndView("login/invalid");
	public final static ModelAndView INVALID_SESSION = new ModelAndView("invalidSession");
	public final static ModelAndView ERROR = new ModelAndView("error");
}
