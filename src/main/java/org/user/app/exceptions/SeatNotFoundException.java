package org.user.app.exceptions;

public class SeatNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public SeatNotFoundException(String message) {
        super(message);
    }

}