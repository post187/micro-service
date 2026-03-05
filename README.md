## Microservices E-Commerce System
# 1. Overview

Đây là dự án Microservices Architecture được xây dựng bằng Spring Boot nhằm mô phỏng hệ thống thương mại điện tử.
Hệ thống được chia thành nhiều service độc lập, mỗi service quản lý một domain riêng và giao tiếp với nhau thông qua REST API và event messaging.

Kiến trúc microservices giúp hệ thống:

Dễ mở rộng (scalability)

Dễ bảo trì (maintainability)

Triển khai độc lập từng service

Tách biệt domain logic
# 2. System Architecture
                Client
                  |
                  v
            API Gateway
                  |
    ---------------------------------
    |          |         |          |
 Product   Customer     Order     Search
 Service   Service     Service    Service

# 3. Technology Stack
Backend

Java 17+

Spring Boot

Spring Web

Spring Data JPA

Spring Security

OAuth2 Resource Server

Microservice Infrastructure

Spring Cloud Gateway

Resilience4j (Circuit Breaker, Retry)

Database

MySQL

Search Engine

Elasticsearch

Messaging

Apache Kafka

Build Tool

Maven


## Author

Project được xây dựng nhằm mục đích học tập và nghiên cứu kiến trúc Microservices với Spring Boot.
    

