package org.user.app.exceptions;


public class ScheduleNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public ScheduleNotFoundException(String message) {
        super(message);
    }

}
