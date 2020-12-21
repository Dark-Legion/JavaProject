package me.web_server;

public final class SqlQueryException extends Exception {
    private final static long serialVersionUID = 0;

    private final String message;

    public SqlQueryException(String message) {
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
