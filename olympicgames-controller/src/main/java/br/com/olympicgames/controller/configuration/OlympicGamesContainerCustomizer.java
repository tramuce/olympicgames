package br.com.olympicgames.controller.configuration;

import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.stereotype.Component;

@Component
public class OlympicGamesContainerCustomizer
	implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public void customize(ConfigurableServletWebServerFactory server) {
	server.setPort(9000);
	server.setContextPath("/olympicgames");
    }
}
