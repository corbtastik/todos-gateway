## Spring Boot Zuul Gateway

This gateway is used to associate a SPA app running on the ``web.endpoint`` to a backend REST API supporting the SPA app on ``api.endpoint``

You would run your front end app then run the backend app, then start this gateway and set the web.endpoint and api.endpoint URLS.

Then access the SPA app via the gateway on localhost:9999 (see example below)

### Run

```bash
java -jar target/todos-gateway-0.0.1-SNAPSHOT.jar --server.port=9999 --api.endpoint=http://localhost:8080 --web.endpoint=http://localhost:4040
```