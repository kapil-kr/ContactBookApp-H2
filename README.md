# ContactBookApp-H2

This is a Kotlin MPP sample App.

### Libraries used:

 - [Ktor](https://github.com/ktorio/ktor) - Kotlin async web framework
 - [Netty](https://github.com/netty/netty) - Async web server
 - [Exposed](https://github.com/JetBrains/Exposed) - Kotlin SQL framework by Jetbrains
 - [H2](https://github.com/h2database/h2database) - Embeddable database
 - [HikariCP](https://github.com/brettwooldridge/HikariCP) - High performance JDBC connection pooling
 - [Jackson](https://github.com/FasterXML/jackson) - JSON serialization/deserialization

### Unit Tests:

 - [JUnit 5](https://junit.org/junit5/)
 - [AssertJ](http://joel-costigliola.github.io/assertj/) 
 - [Rest Assured](http://rest-assured.io/) for testing
 
 ##### Running The App:
        gradlew run
 ##### Running Tests:
        gradlew test       