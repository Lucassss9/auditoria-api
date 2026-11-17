package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.EstoqueCfDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CfObrasClientHttp {

    private final RestClient busca;
    private final CfAutenticacao cfAutenticacao;

    public CfObrasClientHttp(@Value("${CF_URL}") String url, CfAutenticacao cfAutenticacao) {
        this.busca = RestClient.create(url);
        this.cfAutenticacao = cfAutenticacao;
    }

    public EstoqueCfDto buscarEstoque(String filialId, int limite, int pulo) {
        return busca.get()
                .uri("/estoque/register-filter/?limit={l}&skip={p}&filial={f}", limite, pulo, filialId)
                .header("Authorization", cfAutenticacao.pegar())
                .retrieve()
                .body(EstoqueCfDto.class);
    }

}
