## Todo(s) Gateway

Howdy and welcome.  This repository contains a Microservice Gateway implemented in [Spring Boot](https://spring.io/projects/spring-boot) and [Spring Cloud](https://spring.io/projects/spring-cloud).  This Gateway uses Spring Cloud Netflix Zuul to handle routing to the correct Microservice.

For example ``/api`` is mapped to a [backing API](https://github.com/corbtastik/todos-api) and ``/`` is mapped to a [frontend UI](https://github.com/corbtastik/todos-ui).  Zuul is a dynamic router, proxy and server-side load-balancer implemented by Netflix.  Spring Cloud makes using Zuul a cinch which the [Ghostbusters](http://ghostbusters.wikia.com/wiki/Zuul) would surely appreciate.

**Major dependencies**
* [Spring Cloud Netflix Eureka](https://cloud.spring.io/spring-cloud-netflix/)
* [Spring Cloud Netflix Zuul](https://cloud.spring.io/spring-cloud-netflix/)
* [Spring Cloud Config Client](https://cloud.spring.io/spring-cloud-config/)

This gateway is used for the [Todo collection](https://github.com/corbtastik/todos-ecosystem) of Microservices which are part of a larger demo set used in Cloud Native Developer Workshops.

Todo(s) Gateway has next to no code, it's a Spring Boot Microservice that boots an embedded tomcat server to host the Gateway.  It has a main class with the ``@SpringBootApplication`` annotation but also adds ``@EnableZuulProxy`` to enable auto-configuration of Zuul when the server starts.  Beyond that, the only thing we actually need to setup is manual configuration of 2 routes.  When Todo(s) gateway boots into a Spring Cloud environment it will sync with Service Discovery and load Microservice routes dynamically as they come online and remove them when they go offline.  More on that later :)

**Route Configuration**

```yml
todos:
    api:
        endpoint: http://localhost:8080/todos    
    ui:
        endpoint: http://localhost:4040
# Router configuration
zuul:
    routes:
zuul:
    routes:
        todos-gateway-ops:
            path: /ops/**
            url: forward:/ops    
        todos-api:
            path: /api/**
            url: ${todos.api.endpoint}            
        todos-ui:
            path: /**
            url: ${todos.ui.endpoint}
```

By default only 3 routes are defined, one for a backing API and one for a frontend UI and a forward to itself so [actuator endpoints](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-endpoints.html) are exposed.  Specifically the backing API and UI are part of the Todo-EcoSystem of Microservices.  The frontend UI is the Vue.js implementation of [TodoMVC](http://todomvc.com/examples/vue/) and the backing API implements endpoints necessary to make the UI function.  In order to use this Gateway you need to clone, build and run each of these apps.  See the respective repos for information on the [UI](https://github.com/corbtastik/todos-ui) and [API](https://github.com/corbtastik/todos-api).

### Build

```bash
git clone https://github.com/corbtastik/todos-gateway.git
cd todos-gateway
./mvnw clean package
```

### Run 

```bash
java -jar target/todos-gateway-1.0.0.SNAP.jar
```

### Run Overrides

```bash
java -jar target/todos-gateway-1.0.0.SNAP.jar \
  --todos.api.endpoint=howdy \
  --todos.ui.endpoint=spring-boot
```

This will override the API and UI endpoints.

```bash
> http :9999/ops/routes
HTTP/1.1 200 
Content-Type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
Date: Sat, 23 Jun 2018 23:30:15 GMT
Transfer-Encoding: chunked

{
    "/**": "spring-boot",
    "/api/**": "howdy",
    "/ops/**": "forward:/ops"
}
```

### Run with Remote Debug 
```bash
java -Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=9111,suspend=n \
  -jar target/todos-gateway-1.0.0.SNAP.jar
```

By default the Gateway binds to port ``9999`` but this can be overridden on the command line by passing a new port like so, ``--server.port=9191``.  Knowing the port is really only necessary for local deployment of the Todo EcoSystem of apps.  When we push to the Cloud we'll let the Cloud Platform (PAS Pivotal Application Service) manage what port is used.

### Verify

Once the Gateway is running, use an HTTP Client such as [cURL](https://curl.haxx.se/) or [HTTPie](https://httpie.org/) and call ``/ops/routes`` and get a listing of proxy-paths.

```bash
> http :9999/ops/routes
HTTP/1.1 200 
Content-Type: application/vnd.spring-boot.actuator.v2+json;charset=UTF-8
Date: Sat, 23 Jun 2018 22:43:46 GMT
Transfer-Encoding: chunked

{
    "/**": "http://localhost:4040",
    "/api/**": "http://localhost:8080/todos",
    "/ops/**": "forward:/ops"
}
```

If you have the [Todo(s) backing API](https://github.com/corbtastik/todos-api) running locally on port ``8080`` and the [Todo(s) frontend UI](https://github.com/corbtastik/todos-ui) running on ``4040`` then you can access those apps through the Gateway endpoint as shown below.

```bash
> http :9999/api/ 
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Sat, 23 Jun 2018 23:18:24 GMT
Transfer-Encoding: chunked

[]

> http :9999/api/ title="make bacon pancakes"
HTTP/1.1 200 
Content-Type: application/json;charset=UTF-8
Date: Sat, 23 Jun 2018 23:18:37 GMT
Transfer-Encoding: chunked

{
    "completed": false,
    "id": 0,
    "title": "make bacon pancakes"
}
```

The Gateway returns the Todo(s) UI app when client calls on the root path.  For example this call returns the HTML, JavaScript and CSS necessary to render the UI client-side (i.e. a Web Browser).

```bash
> http :9999/
HTTP/1.1 200 
Content-Type: text/html;charset=UTF-8
Date: Sat, 23 Jun 2018 23:18:19 GMT
Transfer-Encoding: chunked
cache-control: max-age=3600
etag: W/"1984861-2524-"2018-06-23T20:01:15.169Z""
last-modified: Sat, 23 Jun 2018 20:01:15 GMT

<!doctype html>
<html data-framework="vue">
	<head>
		<meta charset="utf-8">
		<title>Vue.js • TodoMVC</title>
		<link rel="stylesheet" href="node_modules/todomvc-common/base.css">
		<link rel="stylesheet" href="node_modules/todomvc-app-css/index.css">
		<style> [v-cloak] { display: none; } </style>
	</head>
	<body>
		<section class="todoapp" v-cloak>
```

Which means we can load the Todo(s) UI with the Browser by accessing the Gateway on ``localhost:9999``.
<p align="center">
  <img src="https://github.com/corbtastik/todos-images/raw/master/todos-gateway-images/todos-gateway-ui.png">
</p>

### Spring Cloud Ready

Like every Microservice in [Todo-EcoSystem](https://github.com/corbtastik/todos-ecosystem) the Gateway plugs into the Spring Cloud stack several ways.

#### 1) Spring Cloud Config Client : Pull config from Config Server

From a Spring Cloud perspective we need ``bootstrap.yml`` added so we can configure several important properties that will connect this Microservice to Spring Cloud Config Server so that all external config can be pulled and applied.  We also define ``spring.application.name`` which is the default ``serviceId|VIP`` used by Spring Cloud to refer to this Microservice at runtime.  When the App boots Spring Boot will load ``bootstrap.yml`` before ``application.yml|.properties`` to hook Config Server.  Which means we need to provide where our Config Server resides.  By default Spring Cloud Config Clients (*such as this Gateway*) will look for Config Server on ``localhost:8888`` but if we push to the cloud we'll need to override the value for ``spring.cloud.config.uri``.

```yml
spring:
  application:
    name: todos-gateway
  cloud:
    config:
      uri: ${SPRING_CLOUD_CONFIG_URI:http://localhost:8888}
```

#### 2) Spring Cloud Eureka Client : Participate in service discovery

To have the Gateway participate in Service Discovery we added the eureka-client dependency in our pom.xml.

```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
```

This library will be on the classpath and when Spring Boot starts it will automatically register with Eureka.  When running locally with Eureka we don't need to provide config to find the Eureka Server.  However when we push to the cloud we'll need to locate Eureka and that's done with the following config in ``application.yml|properties`` 

```yml
eureka:
    client:
        service-url:
            defaultZone: http://localhost:8761/eureka 
```

The ``defaultZone`` is the fallback/default zone used by this Eureka Client, we could register with another zone should one be created in Eureka.

To **disable** Service Registration we can set ``eureka.client.enabled=false``.

#### 3) Spring Cloud Sleuth : Support for request tracing

Tracing request/response(s) in Microservices is no small task.  Thankfully Spring Cloud Sleuth provides easy entry into distributed tracing.  We added this dependency in ``pom.xml`` to auto-configure request tracing.

```xml
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-sleuth</artifactId>
    </dependency>
```

Once added our Gateway will add tracing information to each logged event.  For example when we called ``/api`` the Gateway logged that fact and added tracing info to the event.

```shell
INFO [todos-gateway,3805eb97e16e2cdf,3805eb97e16e2cdf,false] 26899 --- [nio-9999-exec-1] o.s.c.n.zuul.web.ZuulHandlerMapping ...
```

The event format is: ``[app, traceId, spanId, isExportable]``, where

* **app**: is the ``spring.application.name`` that sourced the log event
* **traceId**: The ID of the trace graph that contains the span
* **spanId**: The ID of a specific operation that took place
* **isExportable**: Whether the log should be exported to Zipkin

Reference the [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/) docs for more information.


### Run on (PAS) Pivotal Application Service

[Pivotal Application Service](https://pivotal.io/platform/pivotal-application-service) is a modern runtime for Java, .NET, Node.js apps and many more.  It's a Cloud Native Runtime that provides a connected 5-star development to delivery experience.  PAS provides a cloud agnostic surface for apps and ops alike.

#### manifest.yml & vars.yml

The only PAS specific artifacts in this code repo are ``manifest.yml`` and ``vars.yml``.  Modify ``vars.yml`` to add properties specific to your PAS environment. See [Variable Substitution](https://docs.cloudfoundry.org/devguide/deploy-apps/manifest.html#multi-manifests) for more information.  The gist is we only need to set values for our PAS deployment in ``vars.yml`` and pass that file to ``cf push``.

The Todo(s) Gateway requires 3 environment variables:

1. ``EUREKA_CLIENT_SERVICE-URL_DEFAULTZONE`` - Service Discovery URL
2. ``TODOS_UI_ENDPOINT`` - [Todo(s) UI in Vue.js](https://github.com/corbtastik/todos-ui) URL
3. ``TODOS_API_ENDPOINT`` - [Todo(s) API](https://github.com/corbtastik/todos-api) in Spring Boot

#### manifest.yml

```yml
---
applications:
- name: ((app.name))
  memory: ((app.memory))
  routes:
  - route: ((app.route))
  path: ((app.artifact))
  buildpack: java_buildpack
  env:
    ((env-key-1)): ((env-val-1))
    ((env-key-2)): ((env-val-2))
    ((env-key-3)): ((env-val-3))
```

#### vars.yml

```yml
app:
  name: todos-gateway
  artifact: target/todos-gateway-1.0.0.SNAP.jar
  memory: # your memory value, ex: 1G
  route:  # your route value, ex: todos-gateway.cfapps.io
env-key-1: EUREKA_CLIENT_SERVICE-URL_DEFAULTZONE
env-val-1: # your service discovery url, ex: http://cloud-index.cfapps.io/eureka/
env-key-2: TODOS_UI_ENDPOINT
env-val-2: # your todo(s) UI url, ex: http://todos-ui.cfapps.io
env-key-3: TODOS_API_ENDPOINT
env-val-3: # your todo(s) API url, ex: http://todos-api.cfapps.io/todos
```

#### cf push...awe yeah

Yes you can go from zero to hero with one command :)

Make sure your in the Todo(s) Gateway project root (folder with ``manifest.yml``) and cf push...awe yeah!

```
> cf push --vars-file ./vars.yml
```

```
> cf app todos-gateway
Showing health and status for app todos-gateway in org bubbles / space dev as ... 

name:              todos-gateway
requested state:   started
instances:         1/1
usage:             1G x 1 instances
routes:            todos-gateway.cfapps.io
last uploaded:     Sat 23 Jun 18:48:53 CDT 2018
stack:             cflinuxfs2
buildpack:         java_buildpack

     state     since                  cpu      memory         disk           details
#0   running   2018-06-23T23:50:33Z   113.9%   389.2M of 1G   165.4M of 1G  
```





















