# Transaction Management System

A simple transaction management system for recording and managing financial transactions.

[English](README.md) | [中文](README(chinese).md)

## Technology Stack

- Java 21
- Spring Boot 3.2.3
- Maven

## External Dependencies

The project uses the following major external libraries:

### 1. Spring Boot Libraries

#### spring-boot-starter-web (3.2.3)
- Purpose: Provides web application development support
- Main features:
  - Embedded Tomcat server
  - Spring MVC framework
  - RESTful API support
  - JSON serialization/deserialization
  - Basic web application configuration

#### spring-boot-starter-validation (3.2.3)
- Purpose: Provides parameter validation support
- Main features:
  - JSR-380 (Bean Validation 2.0) implementation
  - Annotation-based parameter validation
  - Custom validator support
  - Validation message internationalization

### 2. Development Tools

#### Lombok
- Purpose: Simplifies Java code through annotations
- Main features:
  - `@Data`: Automatically generates getter/setter/toString methods
  - `@Slf4j`: Automatically creates logger objects
  - `@Builder`: Implements builder pattern
  - `@AllArgsConstructor`/`@NoArgsConstructor`: Automatically generates constructors

### 3. Testing Libraries

#### spring-boot-starter-test
- Purpose: Provides testing support
- Main features:
  - JUnit 5 testing framework
  - Mockito mocking framework
  - AssertJ assertion library
  - Spring Test utilities

## Requirements

- Java 21 or higher
- Maven 3.6 or higher
- Docker 20.10 or higher (if using Docker deployment)

## Build and Run

### Option 1: Direct Execution

1. Build the project:
```bash
mvn clean package
```

2. Run the application:
```bash
java -jar target/trans-0.0.1-SNAPSHOT.jar
```

### Option 2: Using Docker

1. Build Docker image:
```bash
docker build -t trans-app .
```

2. Run Docker container:
```bash
docker run -d -p 8080:8080 --name trans-app trans-app
```

3. View container logs:
```bash
docker logs -f trans-app
```

4. Stop and remove container:
```bash
docker stop trans-app
docker rm trans-app
```
