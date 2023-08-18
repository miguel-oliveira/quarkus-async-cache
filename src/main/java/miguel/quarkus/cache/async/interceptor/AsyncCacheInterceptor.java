package miguel.quarkus.cache.async.interceptor;

import java.lang.reflect.Parameter;
import java.util.concurrent.ExecutionException;
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
    final String cacheName = extractCacheName(context);
    final Object cacheKey = extractCacheKey(context);
    return proceed(context, cacheName, cacheKey);
  }

  private String extractCacheName(final InvocationContext context) {
    final CacheResultAsync annotation = context.getMethod().getAnnotation(CacheResultAsync.class);
    return annotation.cacheName();
  }

  private Object extractCacheKey(final InvocationContext context) {
    final Parameter[] parameters = context.getMethod().getParameters();
    for (int i = 0; i < parameters.length; i++) {
      if (parameters[i].isAnnotationPresent(AsyncCacheKey.class)) {
        return context.getParameters()[i];
      }
    }
    throw missingCacheKey(context);
  }

  private RuntimeException missingCacheKey(final InvocationContext context) {
    final String message = "A parameter must be marked with @AsyncCacheKey in order to cache the result. Method = %s";
    return new RuntimeException(message.formatted(context.getMethod()));
  }

  private Object proceed(
      final InvocationContext context,
      final String cacheName,
      final Object cacheKey
  ) throws ExecutionException, InterruptedException {
    return asyncCache.get(cacheName, cacheKey, () -> proceed(context));
  }

  private Object proceed(final InvocationContext context) {
    try {
      return context.proceed();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
