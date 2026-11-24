package br.com.auditoria.config;

import jakarta.servlet.RequestDispatcher;
import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Configuration
public class RecursosEstaticosConfig {

    @Bean
    public ErrorViewResolver rotasDoAngular() {
        return (requisicao, situacao, modelo) -> {
            if (situacao != HttpStatus.NOT_FOUND) {
                return null;
            }

            Object original = requisicao.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            String caminho = original == null ? requisicao.getRequestURI() : original.toString();

            if (caminho.startsWith("/api") || caminho.contains(".")) {
                return null;
            }

            return new ModelAndView("forward:/index.html", Map.of(), HttpStatus.OK);
        };
    }
}
