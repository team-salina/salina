package org.salina.android.exceptions;

public class NotInitializedException extends RuntimeException {
	private static final long serialVersionUID = 7920929723541778823L;

	public NotInitializedException(String message){
		super(message);
	}
}
