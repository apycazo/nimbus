# Nimbus
Microservice infrastructure guide.

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

Since I will be using the Netflix OSS stack, I include the depedencies required in the management section.
This makes sure the netflix components are available for the other components where required:

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


