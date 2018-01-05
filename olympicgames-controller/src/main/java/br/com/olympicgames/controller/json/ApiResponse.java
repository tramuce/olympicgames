package br.com.olympicgames.controller.json;

public class ApiResponse {

    private int code;
    private String message;

    public ApiResponse(int code, String message) {
	this.code = code;
	this.message = message;
    }

    public int getCode() {
	return code;
    }

    public String getMessage() {
	return message;
    }
}
