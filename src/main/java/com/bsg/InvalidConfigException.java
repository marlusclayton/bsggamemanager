package com.bsg;

public class InvalidConfigException extends Exception {

	private static final long serialVersionUID = 4387249066079104852L;

	public InvalidConfigException() {
		super();
	}

	public InvalidConfigException(String msg) {
		super(msg);
	}

	public InvalidConfigException(Throwable cause) {
		super(cause);
	}

	public InvalidConfigException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
