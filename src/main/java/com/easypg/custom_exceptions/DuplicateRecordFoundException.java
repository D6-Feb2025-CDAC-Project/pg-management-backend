package com.easypg.custom_exceptions;

public class DuplicateRecordFoundException extends RuntimeException {
	public DuplicateRecordFoundException (String meg) {
		super(meg);
	}
}
