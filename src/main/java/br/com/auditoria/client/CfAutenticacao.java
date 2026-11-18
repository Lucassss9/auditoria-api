package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.LoginCfDto;
import br.com.auditoria.dto.cfobras.TokenCfDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
public class CfAutenticacao {

    private final RestClient cf;
    private final String email;
    private final String senha;
    private String token;

    public CfAutenticacao(
            @Value("${CF_URL}") String url,
            @Value("${CF_EMAIL}") String email,
            @Value("${CF_SENHA}") String senha) {

        SimpleClientHttpRequestFactory simpleClient = new SimpleClientHttpRequestFactory();
        simpleClient.setConnectTimeout(Duration.ofSeconds(5));
        simpleClient.setReadTimeout(Duration.ofSeconds(30));

        this.cf = RestClient.builder().baseUrl(url).requestFactory(simpleClient).build();
        this.email = email;
        this.senha = senha;
    }

    public String pegar() {
        if (token == null) {
            TokenCfDto tokenCfDto = cf.post()
                    .uri("/auth/login")
                    .body(new LoginCfDto(email, senha))
                    .retrieve()
                    .body(TokenCfDto.class);

            token = tokenCfDto.access_token();
        }
        return token;
    }

    public String renovar() {
        token = null;
        return pegar();
    }
}
