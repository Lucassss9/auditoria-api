package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.EstoqueCfDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CfObrasClientHttpTest {

    @Test
    void buscarEstoque() {
        CfAutenticacao cfAutenticacao = new CfAutenticacao(System.getenv("CF_URL"),
                System.getenv("CF_EMAIL"), System.getenv("CF_SENHA"));

        CfObrasClientHttp clientHttp = new CfObrasClientHttp(System.getenv("CF_URL"), cfAutenticacao);

        EstoqueCfDto busca = clientHttp.buscarEstoque("64091f67b46d603d53838e0e", 5, 0);
        System.out.println(busca.data().results());

        assertNotNull(busca, "Nenhum estoque encontrado");
    }
}
