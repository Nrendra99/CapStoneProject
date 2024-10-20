package org.user.app.exceptions;

public class NoAvailableBusesException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public NoAvailableBusesException(String message) {
        super(message);
    }
}