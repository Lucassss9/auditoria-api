package br.com.auditoria.client;

import br.com.auditoria.dto.cfobras.EstoqueCfDto;

public interface CfObrasClient {

    EstoqueCfDto buscarEstoque(String filialId, int limite, int pagina);
}
