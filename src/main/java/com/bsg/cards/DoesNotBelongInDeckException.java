package com.bsg.cards;

public class DoesNotBelongInDeckException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1861384954418929182L;

	public DoesNotBelongInDeckException() {
		super();
	}

	public DoesNotBelongInDeckException(String msg) {
		super(msg);
	}

	public DoesNotBelongInDeckException(Throwable cause) {
		super(cause);
	}

	public DoesNotBelongInDeckException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
