# Autoconfiguration of the [RDBMS](https://github.com/MoodMinds/rdbms) in [Spring Boot](https://spring.io/projects/spring-boot)

The ability to autoconfigure RDBMS implementation - [RDBMS Traverse](https://github.com/MoodMinds/rdbms-traverse) (JDBC) or
[RDBMS Reactive](https://github.com/MoodMinds/rdbms-reactive) ([R2DBC](https://r2dbc.io)), depending on the environment
in Spring Boot application.

## The Concept

As well as for the [Routes Spring Boot](https://github.com/MoodMinds/routes-spring-boot) the purpose of this project is to
bring the possibility to have a single RDBMS manipulation logic codebase written with the [RDBMS](https://github.com/MoodMinds/rdbms) API
for both synchronous Servlet and Reactive application contexts.

## Usage

Include the dependencies in your assembly and use the injected `Routes` interface implementation to return `Emittable`
instances in your `@Component`:

```java
import org.moodminds.lang.Emittable;
import org.moodminds.rdbms.clause.Script;
import org.moodminds.rdbms.route.Routes;
import org.moodminds.rdbms.route.Stream1;
import org.moodminds.rdbms.statement.Query1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@Service
public class PersonService {

    private static final Script<Query1<String>> FIRST_NAMES_QUERY = $$ -> $$
            .query1("SELECT firstname FROM PERSON WHERE age > :age")
                .type(Integer.class);

    private static final Stream1<Integer, String, RdbmsException> FIRST_NAMES = ($, age) -> $
            .search(FIRST_NAMES_QUERY)
                .input("age", age)
                .handle(person -> $
                    .expand(person, firstName -> $
                        .result(firstName)));

    @Autowired
    private Routes routes;

    @Transactional
    public Emittable<String, RdbmsException> getNames(int age) {
        return routes.stream(FIRST_NAMES, age);
    }
}

@RestController("/persons")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping(path = "/names", produces = TEXT_EVENT_STREAM_VALUE)
    public Emittable<String, RdbmsException> getNames(@RequestParam int age) {
        return personService.getNames(age);
    }
}
```

In particular, for the synchronous [RDBMS Traverse](https://github.com/MoodMinds/rdbms-traverse), which uses JDBC under
the hood, it is required to have the `ConnectionSource` bean present in the Application Context. The default implementation
of this interface is available out of the box and relies on the `DataSource` bean present in the Application Context and
configured [Spring JDBC](https://docs.spring.io/spring-framework/docs/current/reference/html/data-access.html#jdbc) module.
In case of non-`DataSource` (for example, `XADataSource`)/multiple `DataSource` access it should be configured explicitly:

```java
import org.moodminds.rdbms.traverse.ConnectionSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SampleConfig {

    @Bean
    public ConnectionSource connectionSource() {
        return null; // instantiate it somehow
    }
}
```

For the asynchronous [RDBMS Reactive](https://github.com/MoodMinds/rdbms-reactive), which uses [R2DBC](https://r2dbc.io)
under the hood, it is required to have the `ConnectionSource` bean present in the Application Context and configured
[Spring R2DBC](https://docs.spring.io/spring-data/r2dbc/docs/current/reference/html/#get-started:first-steps:what) module.
The default implementation of this interface is available out of the box and relies on the `ConnectionFactory` bean present
in the Application Context. In case of multiple `ConnectionFactory` access or other specific cases it should be configured
explicitly:

```java
import org.moodminds.rdbms.reactive.ConnectionSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class SampleConfig {

    @Bean
    public ConnectionSource connectionSource() {
        return null; // instantiate it somehow
    }
}
```

## Maven configuration

Artifacts can be found on [Maven Central](https://search.maven.org/) after publication.

Add this required dependency to you classpath:

```xml
<dependency>
    <groupId>org.moodminds.route.spring.boot</groupId>
    <artifactId>rdbms-spring-boot</artifactId>
    <version>${version}</version>
</dependency>
```

Depending on the execution environment, include one of these to your classpath:

```xml
<dependency>
    <groupId>org.moodminds.rdbms</groupId>
    <artifactId>rdbms-traverse</artifactId>
    <version>${version}</version>
</dependency>
```

or:

```xml
<dependency>
    <groupId>org.moodminds.rdbms</groupId>
    <artifactId>rdbms-reactive</artifactId>
    <version>${version}</version>
</dependency>
```

## Building from Source

You may need to build from source to use **RDBMS Spring Boot** (until it is in Maven Central) with Maven and JDK 9 at least.

## License
This project is going to be released under version 2.0 of the [Apache License][l].

[l]: https://www.apache.org/licenses/LICENSE-2.0