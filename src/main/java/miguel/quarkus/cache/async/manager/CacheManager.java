package miguel.quarkus.cache.async.manager;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@CacheManagerQualifier
public class CacheManager implements io.quarkus.cache.CacheManager {

  public static final String CACHE_NAME = "async-cache";

  private static final Set<String> CACHE_NAMES;

  static {
    CACHE_NAMES = Set.of(CACHE_NAME);
  }

  private final Map<String, Cache> caches;

  public CacheManager(@CacheName(CACHE_NAME) final Cache cache) {
    this.caches = Map.of(CACHE_NAME, cache);
  }

  @Override
  public Set<String> getCacheNames() {
    return CACHE_NAMES;
  }

  @Override
  public Optional<Cache> getCache(String name) {
    if (name == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(caches.get(name));
  }
}
