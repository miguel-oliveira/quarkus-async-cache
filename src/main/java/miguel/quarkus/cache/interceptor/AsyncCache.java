package miguel.quarkus.cache.interceptor;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheManager;
import io.quarkus.cache.CaffeineCache;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import javax.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import miguel.quarkus.cache.manager.CacheManagerQualifier;
import org.eclipse.microprofile.context.ManagedExecutor;

@Slf4j
@ApplicationScoped
public class AsyncCache {

  private final CacheManager cacheManager;
  private final ManagedExecutor executor;

  public AsyncCache(
      @CacheManagerQualifier final CacheManager cacheManager,
      final ManagedExecutor executor
  ) {
    this.cacheManager = cacheManager;
    this.executor = executor;
  }

  public <T> T get(final String cacheName, final Object key, final Supplier<T> supplier)
      throws ExecutionException, InterruptedException {

    final Optional<Cache> optionalCache = cacheManager.getCache(cacheName);

    if (optionalCache.isEmpty()) {
      log.warn("Cache with name '{}' does not exist.", cacheName);
      return supplier.get();
    }

    final Cache cache = optionalCache.get();

    final CaffeineCache caffeineCache = cache.as(CaffeineCache.class);

    return get(caffeineCache, key, supplier);
  }

  private <T> T get(
      final CaffeineCache caffeineCache,
      final Object key,
      final Supplier<T> supplier
  ) throws InterruptedException, ExecutionException {

    final CompletableFuture<T> value = caffeineCache.getIfPresent(key);

    if (value != null) {
      return value.get();
    }

    final CompletableFuture<T> asyncValue = CompletableFuture.supplyAsync(supplier, executor);
    caffeineCache.put(key, asyncValue);

    return asyncValue.get();
  }

}
