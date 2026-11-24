package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.FilialCfDto;
import br.com.auditoria.dto.cfobras.ObraCompletaCfDto;
import br.com.auditoria.dto.cfobras.ObrasCfDto;
import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.enums.Regiao;
import br.com.auditoria.enums.Segmento;
import br.com.auditoria.enums.Situacao;
import br.com.auditoria.exception.CfIndisponivelException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ObrasDoCf {

    public static final String CACHE = "obrasDoCf";

    private final RestClient cf;
    private final CfAutenticacao cfAutenticacao;

    public ObrasDoCf(@Value("${CF_URL}") String url, CfAutenticacao cfAutenticacao) {
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(5));
        fabrica.setReadTimeout(Duration.ofSeconds(30));

        this.cf = RestClient.builder().baseUrl(url).requestFactory(fabrica).build();
        this.cfAutenticacao = cfAutenticacao;
    }

    @Cacheable(CACHE)
    public List<ObraDto> todas() {
        ObrasCfDto resposta = buscarTodas();

        if (resposta == null || resposta.data() == null) {
            return List.of();
        }

        List<ObraDto> obras = new ArrayList<>();

        for (ObraCompletaCfDto obra : resposta.data()) {
            obras.add(paraObraDto(obra));
        }

        return List.copyOf(comRegiaoDaFilial(obras));
    }

    private static List<ObraDto> comRegiaoDaFilial(List<ObraDto> obras) {
        Map<String, Regiao> escolhida = regiaoPorFilial(obras);
        List<ObraDto> ajustadas = new ArrayList<>();

        for (ObraDto obra : obras) {
            Regiao regiao = obra.filial() == null ? null : escolhida.get(obra.filial().id());

            if (regiao == null || regiao == obra.filial().regional()) {
                ajustadas.add(obra);
                continue;
            }

            FilialDto filial = new FilialDto(obra.filial().id(), obra.filial().codigo(),
                    obra.filial().nome(), regiao);

            ajustadas.add(new ObraDto(obra.id(), obra.codigo(), obra.nome(), obra.segmento(),
                    obra.situacao(), obra.filialId(), filial, obra.totalAuditorias()));
        }

        return ajustadas;
    }

    private static Map<String, Regiao> regiaoPorFilial(List<ObraDto> obras) {
        Map<String, Map<Regiao, Integer>> votos = new HashMap<>();

        for (ObraDto obra : obras) {
            if (obra.filial() == null || obra.filial().regional() == null) {
                continue;
            }

            votos.computeIfAbsent(obra.filial().id(), id -> new EnumMap<>(Regiao.class))
                    .merge(obra.filial().regional(), 1, Integer::sum);
        }

        Map<String, Regiao> escolhida = new HashMap<>();

        for (Map.Entry<String, Map<Regiao, Integer>> filial : votos.entrySet()) {
            Regiao vencedora = null;
            int maior = 0;

            for (Map.Entry<Regiao, Integer> voto : filial.getValue().entrySet()) {
                if (voto.getValue() > maior) {
                    maior = voto.getValue();
                    vencedora = voto.getKey();
                }
            }

            escolhida.put(filial.getKey(), vencedora);
        }

        return escolhida;
    }

    private ObrasCfDto buscarTodas() {
        try {
            try {
                return pedirObras();
            } catch (HttpClientErrorException.Unauthorized tokenVencido) {
                cfAutenticacao.renovar();
                return pedirObras();
            }
        } catch (RestClientException falha) {
            throw new CfIndisponivelException("O CF Obras nao respondeu ao pedido de obras.",
                    falha);
        }
    }

    private ObrasCfDto pedirObras() {
        return cf.get()
                .uri("/obras/all")
                .header("Authorization", cfAutenticacao.pegar())
                .retrieve()
                .body(ObrasCfDto.class);
    }

    private ObraDto paraObraDto(ObraCompletaCfDto obra) {
        FilialDto filial = paraFilialDto(obra.filial(), obra.estado());

        return new ObraDto(
                obra._id(),
                obra.codigo() == null ? null : String.valueOf(obra.codigo()),
                nomeSemSegmento(obra.nome()),
                segmentoDe(obra.nome()),
                Situacao.ATIVA,
                filial == null ? null : filial.id(),
                filial,
                0);
    }

    private FilialDto paraFilialDto(FilialCfDto filial, String estado) {
        if (filial == null) {
            return null;
        }

        String nomeCompleto = filial.nome() == null ? "" : filial.nome();
        int separador = nomeCompleto.indexOf(" - ");

        String codigo = separador < 0 ? nomeCompleto : nomeCompleto.substring(0, separador);
        String nome = separador < 0 ? nomeCompleto : nomeCompleto.substring(separador + 3);

        return new FilialDto(filial._id(), codigo, nome, regiaoDe(estado));
    }

    private static Segmento segmentoDe(String nome) {
        return nome != null && nome.toUpperCase().contains("INSTALL")
                ? Segmento.INSTALL
                : Segmento.CIVIL;
    }

    private static String nomeSemSegmento(String nome) {
        if (nome == null) {
            return null;
        }

        return nome.replaceAll("(?i)install", "")
                .replaceAll("[\\s-]+$", "")
                .trim();
    }

    private static Regiao regiaoDe(String estado) {
        return "RJ".equalsIgnoreCase(estado) ? Regiao.RJ : Regiao.SP;
    }
}
