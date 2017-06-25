# Nimbus
Micro service infrastructure guide.

# Project structure

## Master POM or BOM

This POM file will keep control of dependency versions, spring boot parent and makes sure some 
elements are readily available for all other components.

For this, include as a parent the latest spring-boot parent:

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>1.5.4.RELEASE</version>
    <!-- Setting this to empty eliminates a problem with the aggregator -->
    <relativePath/>
</parent>
```

**Note** that I have set an empty relative path for the parent. This help preventing a couple warnings.

### Dependency management

Since I will be using the Netflix OSS stack, I include the dependencies required in the management section.
This makes sure the netflix components are available for the other components where required. The same is true for the
spring cloud modules:

```xml
<properties>
    <spring-cloud.version>Dalston.SR1</spring-cloud.version>
    <spring-admin.version>1.5.1</spring-admin.version>
</properties>
```

```xml
<dependencyManagement>
    <dependencies>
        <!-- Netflix dependencies import -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-netflix</artifactId>
            <version>${netflix.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Base dependencies

1. **Lombok**: This saves a lot code for getter/setter and log instancing, not critical, but useful.
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```
2. **Spring boot test starter**: Testing is surely going to be part of things, so including this is a must.
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

This provides the following artifacts:

* **JUnit** — The de-facto standard for unit testing Java applications.
* **Spring Test & Spring Boot Test** — Utilities and integration test support for Spring Boot applications.
* **AssertJ** — A fluent assertion library.
* **Hamcrest** — A library of matcher objects (also known as constraints or predicates).
* **Mockito** — A Java mocking framework.
* **JSONassert** — An assertion library for JSON.
* **JsonPath** — XPath for JSON.

3. **Spring boot configuration processor**

Generates a file `/META-INF/spring-configuration-metadata.json`containing the declared properties 
for the module (mainly, those annotated with `@ConfigurationProperties`).
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
</dependency>
```

The generated content for a configuration class like this:
```java
@Data
@Configuration
@ConfigurationProperties(prefix="nimbus.demo")
public class NimbusDemoSettings
{
    private Integer randomId = 0;
}
```

Looks like:
```
{
  "hints": [],
  "groups": [
    {
      "sourceType": "com.github.apycazo.nimbus.demo.cfg.NimbusDemoSettings",
      "name": "nimbus.demo",
      "type": "com.github.apycazo.nimbus.demo.cfg.NimbusDemoSettings"
    }
  ],
  "properties": [
    {
      "sourceType": "com.github.apycazo.nimbus.demo.cfg.NimbusDemoSettings",
      "defaultValue": 0,
      "name": "nimbus.demo.random-id",
      "type": "java.lang.Integer"
    }
  ]
}
```

## Service modules

The modules that will be deployed (tomcat/jetty...). I create the following structure:

```
<projectGroup>.<projectName>
+--- <projectGroup>.<projectName>App: Main class
|
+--- cfg: Configuration classes
|
+--- api: Keep public api & controllers here
|
+--- svc: Shared services
|
+--- etc: Others
```

### Service dependencies

1. **Spring boot web starter**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

2. **Spring boot actuator starter**: Production-ready administration endpoints

**NOTE** Many other services, like eureka or cloud config make use of this one.
   
```xml
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Initial dev configuration:
```yml
management.contextPath: /
management.security.enabled: false
endpoints.enabled: true

info:
    application: ${spring.application.name}
    instanceId: ${random.uuid}
```

3. **Apache HTTP clients**: Replaces java default http services on rest templates.

```xml
<!-- HTTP client -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
</dependency>
<!-- HTTP async client -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpasyncclient</artifactId>
</dependency>
```

4. **Feign**: Declarative rest client

```xml
<!-- Feign client -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-feign</artifactId>
</dependency>
```

5. **Git commit ID**: If you are using git, this will create a git info file (will be used by `spring-actuator`)-
```xml
<plugin>
    <groupId>pl.project13.maven</groupId>
    <artifactId>git-commit-id-plugin</artifactId>
    <version>${git-commit.version}</version>
</plugin>
```

6. **Spring boot Maven builder**: To generate a custom artifact, use this dependency:
```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <!-- Add service script -->
    <configuration>
        <executable>true</executable>
    </configuration>
    <!-- Create a fat jar -->
    <executions>
        <execution>
            <goals>
                <goal>build-info</goal>
                <goal>repackage</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```
This will do three things:
* Create a fat-jar, with all dependencies included.
* Insert information about the build (to be used by the `spring-actuator` module)
* Create an embedded service script on the jar file, so it can be used as a service out of the box.

# Initial configuration

## Application properties

An example of a customized application properties file would look like this in `yaml`:

```yml
spring.application.name: service-name
logging.file: logs/${spring.application.name}.log
server.port: 8080
```

# Creating a supporting framework

The microservice support framework should consist of the following:

* Discovery server/client (Eureka)
* Configuration service (Cloud config)
* Logs & management (Sleuth & Zipkin)

Since I don't want to deploy three different services I am going to mix them a little inside a spring boot project.

## Service discovery with Eureka

### Server

You will need the following dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka-server</artifactId>
    </dependency>
</dependencies>
```

And enable the client in the configuration:

```java
@EnableDiscoveryClient
@SpringBootApplication
public class NimbusDemoApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusDemoApp.class)
                .properties("sun.misc.URLClassPath.disableJarChecking=true")
                .run(args);
    }
}
```

Configure the client (inside applications properties)
```yml
eureka:
    environment: deployment
    datacenter: local
    server:
        eviction-interval-timer-in-ms: 5000
        # for single node only
        enableSelfPreservation: false
    client:
        registerWithEureka: false
        serviceUrl.defaultZone: http://${eureka.instance.hostname:localhost}:${server.port}/eureka/
```

### Client

You will need the following dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
</dependencies>
```

And enable the service in the configuration:

```java
@EnableEurekaServer
@SpringBootApplication
public class NimbusCentralApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusCentralApp.class).run(args);
    }
}
```

Configure the server (inside applications properties)
```yml
eureka.instance:
    preferIpAddress: true
    statusPageUrlPath: ${management.contextPath:/}info
    healthCheckUrlPath: ${management.contextPath:/}health
    # To setup a custom id
    # instanceId: ${spring.application.name}-${random.uuid}
    metadataMap.servicePath: ${nimbus.mapping:/data}
    leaseRenewalIntervalInSeconds: 30
    leaseExpirationDurationInSeconds: 30
```

### Extra: Admin UI

Right now you can get the Eureka dashboard at `http://localhost:8761`, but if actuator is present on the client, you
can get a more detailed dashboard. Here I am going to run it along with the Eureka server, instead of a stand alone
service.

1. Add server dependencies:
```xml
<properties>
    <spring-admin.version>1.5.1</spring-admin.version>
</properties>

<dependencies>
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-server</artifactId>
        <version>${spring-admin.version}</version>
    </dependency>
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-server-ui</artifactId>
        <version>${spring-admin.version}</version>
    </dependency>
</dependencies>
```
2. Enable service 

Note that this requires an eureka discovery client to be active too.

```java
@EnableAdminServer
@EnableEurekaServer
@EnableDiscoveryClient
@SpringBootApplication
public class NimbusCentralApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusCentralApp.class).run(args);
    }
}
```

3. Configure the dashboard not to clash with eureka:

```yml
spring.boot.admin.contextPath: /admin
```

## Cloud configuration

Now I am going to embed another service, this time spring-cloud-config, so the services can get their configuration 
from a remote, centralized service.

### Server

1. Add dependency management for cloud

```xml
<properties>
    <spring-cloud.version>Dalston.SR1</spring-cloud.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

2. Include dependencies for the server
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>
```

3. Enable the server

```java
@EnableConfigServer
@SpringBootApplication
public class NimbusCentralApp
{
    public static void main(String[] args)
    {
        new SpringApplicationBuilder(NimbusCentralApp.class).run(args);
    }
}
```

4. And configure it (note that we change to path from `/` to `/cloud`, to avoid errors with the eureka dashboard)

```yml
# Cloud config
# Configures properties directory, by default it uses git, but can be classpath:.., file://...
spring.cloud.config.server.git.uri: classpath:/config
# Avoids clashing with Eureka
spring.cloud.config.server.prefix: cloud
spring.profiles.active: native
```

**NOTE** By default this service is expected on port 8888.

### Client

1. Include dependencies (requires access to the dependency management too)

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

2. Configure cloud service

Since this service will be loaded BEFORE the service itself, it uses a different configuration file: `bootstrap.yml` 
(as usual, regular property files can be used too). The security config is in place in case spring security is in the
classpath.

```yml
spring:
    application.name: nimbus-demo
    profiles.active: defaults
    cloud.config:
        uri: http://localhost:8761/cloud
        username: root
        password: s3cr3t
        server.prefix: cloud
```
