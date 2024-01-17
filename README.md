## Shareit

The main function of the application is to rent and rent personal items. The service provides with the opportunity to place things, rent them out, create requests for things, and comment on lease agreements.

Structurally, the application consists of 3 docker-containers. The first container contains the main service of the application with all the business logic. The second container contains the database to which the main service connects. The third container contains a service for filtering requests to the application. It accepts all external requests, filters them for correctness, and accesses the main service only if the request was correct.

Stack: Java 11, Spring Boot, Lombock, Mockito, JUnit 5, PostgreSQL.
