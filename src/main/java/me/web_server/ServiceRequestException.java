package me.web_server;

public final class ServiceRequestException extends Exception {
	private final static long serialVersionUID = 0;

	private final String message;

	public ServiceRequestException(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
