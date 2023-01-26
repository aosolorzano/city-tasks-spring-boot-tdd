## Using TDD with Integration Testing and Testcontainers.

* **Author**: [Andres Solorzano](https://www.linkedin.com/in/aosolorzano/).
* **Level**: Intermediate.
* **Technologies**: Java, Spring Boot, Testcontainers, Quartz, Postgres, DynamoDB and Docker Compose.

---

## Description
This project uses the Spring Boot Framework to perform CRUD operations over Tasks records that store Quartz Jobs on AWS Postgres.
The idea is to use TDD from the beginning of the project and use Testcontainers to run the application and the database in a Docker container.

### Running the application using Docker Compose
Execute the following command from the root of the project:
```bash
docker compose up --build
```

### Adding a Device item into DynamoDB
Execute the following command from the root of the project:
```bash
aws dynamodb put-item                     \
    --table-name Devices                  \
    --endpoint-url http://localhost:8000  \
    --item file://utils/dynamodb/items/device-item.json
```

### Getting a Device item from DynamoDB
Execute the following command:
```bash
aws dynamodb scan         \
  --table-name Devices    \
  --endpoint-url http://localhost:8000
```

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.0.1/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#web)
* [Quartz Scheduler](https://docs.spring.io/spring-boot/docs/3.0.1/reference/htmlsingle/#io.quartz)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

