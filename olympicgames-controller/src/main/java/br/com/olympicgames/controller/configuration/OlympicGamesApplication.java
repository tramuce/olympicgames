package br.com.olympicgames.controller.configuration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author tramuce
 *
 *         Classe de inicialização da aplicação utilizando Spring Boot.
 */
@SpringBootApplication
@ComponentScan({ "br.com.olympicgames" })
public class OlympicGamesApplication {
    public static void main(String[] args) {
	SpringApplication.run(OlympicGamesApplication.class, args);
    }
}
