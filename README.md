# ChatApp (Backend)

Java Spring Boot-додаток для простого чату в режимі реального часу.

Версії:
- Java 17
- Spring Boot 3.2.5
- REST API
- Lombok
- Maven

Структура проєкту:

com.example.chatapp
├── controller # REST-контролери
│ └── ChatController.java
├── model # DTO-класи (повідомлення)
│ └── Message.java
├── service # Бізнес-логіка (поки пуста)
│ └── ChatService.java
└── ChatAppApplication.java

Запуск:

```bash
./mvnw spring-boot:run

Maven залежності:

spring-boot-starter-web
spring-boot-devtools
lombok
logback-core (v.1.5.6 для закриття вразливості)

Статус:
Початкова структура проєкту, перший endpoint, готово до додавання WebSocket, кімнат і обміну повідомленнями
