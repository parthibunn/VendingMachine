package com.stee.exception;

public class SoldOutException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
   
    public SoldOutException(String message) {
        this.message = message;
    }
   
    @Override
    public String getMessage(){
        return message;
    }
}

