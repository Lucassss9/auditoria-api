package br.com.auditoria.config;

import br.com.auditoria.client.ObrasDoCf;
import br.com.auditoria.service.EstoqueDoCf;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableCaching
@EnableScheduling
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(EstoqueDoCf.CACHE, ObrasDoCf.CACHE);
    }
}
