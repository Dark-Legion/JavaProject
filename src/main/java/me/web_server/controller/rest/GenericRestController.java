package me.web_server.controller.rest;

import java.util.HashMap;
import java.util.concurrent.Callable;

import org.springframework.web.bind.annotation.RestController;

import me.web_server.ServiceRequestException;

@RestController
public class GenericRestController {
	public interface RestRequestCallable {
		public Object call() throws ServiceRequestException;
	}

	public static HashMap<String, Object> handleRestRequest(RestRequestCallable callable) {
		Object data = null;
		String error = null;

		try {
			data = callable.call();
		} catch (ServiceRequestException exception) {
			error = exception.getMessage();
		}

		HashMap<String, Object> result = new HashMap<>();

		result.put("success", error == null);
		result.put("result", data);
		result.put("error", error);

		return result;
	}

	public static Callable<HashMap<String, Object>> handleAsyncRestRequest(RestRequestCallable callable) {
		return new Callable<HashMap<String,Object>>(){
			@Override
			public HashMap<String, Object> call() throws Exception {
				return handleRestRequest(callable);
			}
		};
	}
}
