package me.web_server.controller;

import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import me.web_server.controller.web.ErrorPage;
import me.web_server.service.GenericService;

@Controller
public class ErrorPageController implements ErrorController {
	@Override
	public String getErrorPath() {
		return "/error";
	}

	private ModelAndView handlePageError(HttpServletRequest request, Model model, Exception exception) {
		String message = exception.getMessage();

		return ErrorPage.error(model, message == null ? "Service unavailable!" : message);
	}

	private ResponseEntity<HashMap<String, Object>> handleRestError(HttpServletRequest request, Exception exception) {
		String message = exception.getMessage();

		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
			GenericService.getErrorResultMap(message == null ? "Service unavailable!" : message)
		);
	}

	@RequestMapping("/error")
	@ResponseBody
	public Object handleError(HttpServletRequest request, Model model, Exception exception) {
		boolean restError = false;

		{
			String url = request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI).toString();

			restError = url.equals("/api");

			if (!restError) {
				restError |= url.startsWith("/api/");
			}
		}

		return (
			restError ?
			handleRestError(request, exception) :
			handlePageError(request, model, exception)
		);
	}
}
