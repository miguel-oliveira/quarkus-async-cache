package miguel.quarkus.cache.async;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;


@Path("/async-cache")
@RequiredArgsConstructor
public class AsyncCacheResource {

  private final ComputeValueService computeValueService;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String get(
      @QueryParam("key") final String key,
      @QueryParam("delayInMs") final int delayInMs
  ) {
    return computeValueService.computeValue(key, delayInMs);
  }


}
