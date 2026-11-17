package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.LoginCfDto;
import br.com.auditoria.dto.cfobras.TokenCfDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

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

        this.cf = RestClient.create(url);
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
