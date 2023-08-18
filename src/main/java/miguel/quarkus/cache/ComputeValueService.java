package miguel.quarkus.cache;

import static miguel.quarkus.cache.manager.CacheManager.CACHE_NAME;

import javax.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;
import miguel.quarkus.cache.interceptor.AsyncCacheKey;
import miguel.quarkus.cache.interceptor.CacheResultAsync;
import org.slf4j.helpers.MessageFormatter;

@Slf4j
@ApplicationScoped
public class ComputeValueService {

  @CacheResultAsync(cacheName = CACHE_NAME)
  public String computeValue(@AsyncCacheKey final String key, final int delayInMs) {
    log.info("Computing value from key %s".formatted(key));
    try {
      Thread.sleep(delayInMs);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    return MessageFormatter.format("Computed value from key '{}'", key).getMessage();
  }

}
