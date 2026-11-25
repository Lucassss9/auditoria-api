package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.EstoqueCfDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CfObrasClientHttpTest {

    @Test
    @EnabledIfEnvironmentVariable(named = "CF_SENHA", matches = ".+")
    void buscarEstoque() {
        CfAutenticacao cfAutenticacao = new CfAutenticacao(System.getenv("CF_URL"),
                System.getenv("CF_EMAIL"), System.getenv("CF_SENHA"));

        CfObrasClientHttp clientHttp = new CfObrasClientHttp(System.getenv("CF_URL"), cfAutenticacao);

        EstoqueCfDto busca = clientHttp.buscarEstoque("64091f67b46d603d53838e0e",  15, 0);

        assertNotNull(busca, "Nenhum estoque encontrado");
    }
}
