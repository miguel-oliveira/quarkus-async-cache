# quarkus-async-cache

Implementation of an asynchronous cache
using [quarkus cache extension](https://quarkus.io/guides/cache).

The goal of the asynchronous cache is to avoid unnecessary requests to resources that are currently
being fetched and cached by another thread.

When multiple threads request the same resource, only the first request will be executed and cached.

The remaining threads will not perform the request, but instead wait for the cached value resulting
from the first thread's request.

## Usage

* Build and run the application using maven:

````shell
mvn quarkus:dev
````

* Access the API through http://localhost:8080/swagger
* Perform a request for a given key, passed through query parameter
* If the key does not have any cached value yet, it will be computed and cached
* A delay, in milliseconds, may be passed as query parameter when performing the request in order to
  simulate an expensive computation

In order to check if a given value is computed only once for multiple concurrent requests for the
same key:

* Open two browser instances of http://localhost:8080/swagger
* Perform a request in the first instance for a given key, and add a delay (e.g. 10000)
* Perform a request for the same key in the second instance
* The second request will then wait for the first request to complete and return the cached result
* The [service](https://github.com/miguel-oliveira/quarkus-async-cache/blob/master/src/main/java/miguel/quarkus/cache/async/ComputeValueService.java)
responsible for the computation will log a message every time it executes
* After executing the above steps, only one log message should appear
