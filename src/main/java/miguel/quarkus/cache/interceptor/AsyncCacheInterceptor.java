package miguel.quarkus.cache.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import lombok.RequiredArgsConstructor;

@Interceptor
@CacheResultAsync(cacheName = "")
@RequiredArgsConstructor
public class AsyncCacheInterceptor {

  private final AsyncCache asyncCache;

  @AroundInvoke
  public Object cacheResult(final InvocationContext context) throws Exception {

    final CacheResultAsync annotation = context.getMethod().getAnnotation(CacheResultAsync.class);

    final String cacheName = annotation.cacheName();
    final int cacheKeyIndex = annotation.cacheKeyIndex();
    final Object key = context.getParameters()[cacheKeyIndex];

    return asyncCache.get(cacheName, key, () -> proceed(context));
  }

  private Object proceed(final InvocationContext context) {
    try {
      return context.proceed();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
