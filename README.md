# Kopidlno saver
Trixi interview task

## Description
This Spring Boot application:
- downloads zipped data about a town from given url
- parses data from xml
- saves them to database

## Notes

### Source URL
The source URL can be set in `application.properties`. The application should work with any valid URL that points to a zipped XML file with the same structure as the provided example.

### Database
The application uses simple SQLite database but can be easily configured to use any other relational database by changing the `spring.datasource` properties in `application.properties` and adding the appropriate JDBC driver dependency in `pom.xml`. Database schema is created/updated automatically by Hibernate.

### Tests
The application includes unit tests for classes responsible for parsing data, importing XML files from URLs, and also for facade class that orchestrates the saving process. There is also basic integration test to ensure the entire workflow functions correctly. To run the tests, use the command `mvn test`.

## Instructions
To run the application, follow these steps:
1. Clone the repository
2. Set the source URL in `application.properties`
3. Run the application using `mvn spring-boot:run` in the project root directory
