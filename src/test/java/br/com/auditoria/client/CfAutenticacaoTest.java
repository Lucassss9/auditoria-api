package br.com.auditoria.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CfAutenticacaoTest {

    @Test
    void logaNoCfEDevolveToken() {
        CfAutenticacao cfAutenticacao = new CfAutenticacao(System.getenv("CF_URL"),
                System.getenv("CF_EMAIL"), System.getenv("CF_SENHA"));

        String token = cfAutenticacao.pegar();

        assertNotNull(token, "o CF nao devolveu token - confira CF_EMAIL e CF_SENHA na configuracao de teste");
    }
}
