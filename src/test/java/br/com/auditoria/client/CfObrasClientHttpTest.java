package br.com.auditoria.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CfObrasClientHttpTest {

    @Test
    void buscarEstoque() {
        CfAutenticacao cfAutenticacao = new CfAutenticacao(System.getenv("CF_URL"),
                System.getenv("CF_EMAIL"), System.getenv("CF_SENHA"));

        CfObrasClientHttp clientHttp = new CfObrasClientHttp(System.getenv("CF_URL"), cfAutenticacao);

        String busca = clientHttp.buscarEstoque("64091f67b46d603d53838e0e", 5, 0);

        assertNotNull(busca, "Nenhum estoque encontrado");
    }
}
