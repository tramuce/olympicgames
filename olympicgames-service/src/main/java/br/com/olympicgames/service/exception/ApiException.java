package br.com.olympicgames.service.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -711888906245200864L;

    private int code;
    private HttpStatus httpStatus;

    public ApiException(HttpStatus httpStatus, int code, String message) {
	super(message);
	this.code = code;
	this.httpStatus = httpStatus;
    }

    public int getCode() {
	return this.code;
    }

    public HttpStatus getHttpStatus() {
	return this.httpStatus;
    }
}
