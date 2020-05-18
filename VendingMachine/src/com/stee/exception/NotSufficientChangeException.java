package com.stee.exception;

public class NotSufficientChangeException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
   
    public NotSufficientChangeException(String message) {
        this.message = message;
    }
   
    @Override
    public String getMessage(){
        return message;
    }
   
}

