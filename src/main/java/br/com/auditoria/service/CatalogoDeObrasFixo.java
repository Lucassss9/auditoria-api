package br.com.auditoria.service;

import br.com.auditoria.dto.obra.FilialDto;
import br.com.auditoria.dto.obra.ObraDto;
import br.com.auditoria.enums.Regiao;
import br.com.auditoria.enums.Segmento;
import br.com.auditoria.enums.Situacao;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CatalogoDeObrasFixo implements CatalogoDeObras {

    private static final List<FilialDto> FILIAIS = List.of(
            new FilialDto("CCISA101", "CCISA101", "CIDADE ALVORADA", Regiao.RJ),
            new FilialDto("CCISA118", "CCISA118", "PARQUE AURORA", Regiao.SP),
            new FilialDto("CCISA133", "CCISA133", "MIRANTE DO PORTO", Regiao.RJ)
    );

    private static final Map<String, FilialDto> FILIAIS_POR_ID = FILIAIS.stream()
            .collect(Collectors.toMap(FilialDto::id, Function.identity()));

    private static final List<ObraDto> OBRAS = List.of(
            obra("OBR-1011", "Alvorada I", Segmento.INSTALL, Situacao.ATIVA, "CCISA101"),
            obra("OBR-1012", "Alvorada II", Segmento.INSTALL, Situacao.ATIVA, "CCISA101"),
            obra("OBR-1013", "Alvorada III", Segmento.CIVIL, Situacao.ATIVA, "CCISA101"),
            obra("OBR-1181", "Jardim Aurora", Segmento.CIVIL, Situacao.ATIVA, "CCISA118"),
            obra("OBR-1331", "Mirante do Porto", Segmento.CIVIL, Situacao.ENCERRADA, "CCISA133")
    );

    @Override
    public List<ObraDto> listar() {
        return OBRAS;
    }

    @Override
    public ObraDto buscarPorId(String id) {
        return OBRAS.stream()
                .filter(obra -> obra.id().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<FilialDto> listarFiliais() {
        return FILIAIS;
    }

    private static ObraDto obra(String codigo, String nome, Segmento segmento,
                                Situacao situacao, String filialId) {
        return new ObraDto(codigo, codigo, nome, segmento, situacao, filialId,
                FILIAIS_POR_ID.get(filialId), 0);
    }
}
