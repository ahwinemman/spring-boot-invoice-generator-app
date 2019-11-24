# invoice-generator
This project is built with the Spring Java Framework.

## Getting Started
git clone "https://github.com/ahwinemman/spring-boot-invoice-generator-app"

### Prerequisites
You would need:
* JDK 1.8
* Maven

In the terminal window, cd into the root terminal of the project.
Build the project with the following command:
```shell script
mvn clean package
```
Then, to run the project's build jar file, run the following command:
```shell script
java -jar target/invoice-generator.jar
```

The server will be listening on "http://localhost:9090"

The project also has a jacoco plugin in its pom.xml allowing one to check things like code coverage with sonarqube.
* This is a very helpful tutorial as regards sonarqube: https://springbootdev.com/category/sonarqube/
