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

    private static final long CINCO_MINUTOS = 5 * 60 * 1000L;

    private final CacheManager cacheManager;

    @Scheduled(fixedDelay = CINCO_MINUTOS, initialDelay = CINCO_MINUTOS)
    public void esquecerOQueVeioDoCf() {
        for (String nome : List.of(EstoqueDoCf.CACHE, ObrasDoCf.CACHE)) {
            Cache cache = cacheManager.getCache(nome);

            if (cache != null) {
                cache.clear();
            }
        }
    }
}
