package br.com.olympicgames.service.exception;

import org.springframework.http.HttpStatus;

/**
 * @author tramuce
 * 
 *         Classe de exception criada para representar as exceptions esperadas
 *         pelo sistema no caso de validações.
 *
 */
public class ApiException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -711888906245200864L;

    private int code;
    private HttpStatus httpStatus;

    /**
     * @param httpStatus
     *            - Status Http que representa a exception.
     * @param code
     *            - Código definido para o tipo de mensagem.
     * @param message
     *            - Mensagem de erro.
     */
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
