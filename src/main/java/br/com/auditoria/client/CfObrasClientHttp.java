package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.EstoqueCfDto;
import br.com.auditoria.exception.CfIndisponivelException;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

@Component
public class CfObrasClientHttp implements CfObrasClient {

    private final RestClient busca;
    private final CfAutenticacao cfAutenticacao;

    public CfObrasClientHttp(@Value("${CF_URL}") String url, CfAutenticacao cfAutenticacao) {
        SimpleClientHttpRequestFactory simpleClient = new SimpleClientHttpRequestFactory();
        simpleClient.setConnectTimeout(Duration.ofSeconds(5));
        simpleClient.setReadTimeout(Duration.ofSeconds(30));

        this.busca = RestClient.builder().baseUrl(url).requestFactory(simpleClient).build();
        this.cfAutenticacao = cfAutenticacao;
    }

    public EstoqueCfDto buscarEstoque(String filialId, int limite, int pagina) {
        try {
            try {
                return getEstoqueCfDto(filialId, limite, pagina);
            } catch (HttpClientErrorException.Unauthorized tokenVencido) {
                cfAutenticacao.renovar();
                return getEstoqueCfDto(filialId, limite, pagina);
            }
        } catch (RestClientException falha) {
            throw new CfIndisponivelException(
                    "O CF Obras nao respondeu ao pedido de estoque.", falha);
        }
    }

    private @Nullable EstoqueCfDto getEstoqueCfDto(String filialId, int limite, int pagina) {
        return busca.get()
                .uri("/estoque/register-filter/?limit={l}&skip={p}&filial={f}", limite, pagina, filialId)
                .header("Authorization", cfAutenticacao.pegar())
                .retrieve()
                .body(EstoqueCfDto.class);
    }
}
