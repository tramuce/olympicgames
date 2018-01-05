package br.com.olympicgames.controller.configuration;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

/**
 * @author tramuce
 * 
 *         Classe que irá customizar o tomcat que sobe a aplicação. Estão sendo
 *         customizados o contextPath e a porta a serem acessadas para o recurso
 *
 */
@Component
public class OlympicGamesContainerCustomizer
	implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public void customize(ConfigurableServletWebServerFactory server) {
	server.setPort(9000);
	server.setContextPath("/olympicgames");
    }
}
