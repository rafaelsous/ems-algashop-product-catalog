package com.rafaelsousa.algashop.product.catalog.infrastructure.cache;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class ResilienceCacheErrorHandler implements CacheErrorHandler {

	@Override
	public void handleCacheGetError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
        String method = "GET";
		logWarn(exception, cache, key, method);
	}

	@Override
	public void handleCachePutError(@NonNull RuntimeException exception, @NonNull Cache cache,
	                                @NonNull Object key, @Nullable Object value) {
		String method = "PUT";
		logWarn(exception, cache, key, method);
	}

	@Override
	public void handleCacheEvictError(@NonNull RuntimeException exception, @NonNull Cache cache, @NonNull Object key) {
		String method = "EVICT";
		logWarn(exception, cache, key, method);
	}

	@Override
	public void handleCacheClearError(@NonNull RuntimeException exception, @NonNull Cache cache) {
		String method = "CLEAR";
		logWarn(exception, cache, "", method);
	}

	private void logWarn(RuntimeException exception, Cache cache, Object key, String method) {
		log.warn("Cache {} error | cache='{}' | key='{}' | cause='{}'",
				method, cache.getName(), key, exception.getClass().getSimpleName());
	}
}