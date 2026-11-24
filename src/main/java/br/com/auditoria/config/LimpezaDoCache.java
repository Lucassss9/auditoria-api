package br.com.auditoria.config;

import br.com.auditoria.client.ObrasDoCf;
import br.com.auditoria.service.EstoqueDoCf;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LimpezaDoCache {

    private static final long MEIA_HORA = 30 * 60 * 1000L;

    private final CacheManager cacheManager;

    @Scheduled(fixedDelay = MEIA_HORA, initialDelay = MEIA_HORA)
    public void esquecerOQueVeioDoCf() {
        for (String nome : List.of(EstoqueDoCf.CACHE, ObrasDoCf.CACHE)) {
            Cache cache = cacheManager.getCache(nome);

            if (cache != null) {
                cache.clear();
            }
        }
    }
}
