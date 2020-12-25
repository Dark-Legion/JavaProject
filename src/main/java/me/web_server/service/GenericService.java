package me.web_server.service;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.ui.Model;

import me.web_server.ServiceRequestException;
import me.web_server.controller.web.ModelAndViews;

public abstract class GenericService {
	public interface ServiceCallable {
		public Object call() throws ServiceRequestException;
	}

	private static final HashMap<String, Object> getResultMap(boolean success, Object data) {
		HashMap<String, Object> result = new HashMap<>();

		result.put("success", success);
		result.put("result", (success ? data : null));
		result.put("error", (success ? null : data));

		return result;
	}

	public static final HashMap<String, Object> getSuccessResultMap(Object data) {
		return getResultMap(true, data);
	}

	public static final HashMap<String, Object> getErrorResultMap(String error) {
		return getResultMap(false, error);
	}

	public static final <T> Object handleWebRequest(ServiceCallable callable, Model model) {
		try {
			return callable.call();
		} catch (ServiceRequestException exception) {
			model.addAttribute("error", exception.getMessage());

			return ModelAndViews.ERROR;
		}
	}

	public static final <T> Callable<Object> handleAsyncWebRequest(ServiceCallable callable, Model model) {
		return new Callable<Object>(){
			@Override
			public Object call() {
				return handleWebRequest(callable, model);
			}
		};
	}

	public static final <T> HashMap<String, Object> handleRestRequest(ServiceCallable callable) {
		try {
			return getSuccessResultMap(callable.call());
		} catch (ServiceRequestException exception) {
			return getErrorResultMap(exception.getMessage());
		}
	}

	public static final <T> Callable<HashMap<String, Object>> handleAsyncRestRequest(ServiceCallable callable) {
		return new Callable<HashMap<String, Object>>() {
			@Override
			public HashMap<String, Object> call() {
				return handleRestRequest(callable);
			}
		};
	}
}
