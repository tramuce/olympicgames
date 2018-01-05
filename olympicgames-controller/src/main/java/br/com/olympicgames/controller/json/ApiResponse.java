package br.com.olympicgames.controller.json;

/**
 * @author tramuce
 * 
 *         Classe utilizada para montar um objeto de response padrão para
 *         retorno de mensagens de exceptions lançadas pelo sistema, sejam de
 *         validação ou erros inexperados.
 *
 */
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
