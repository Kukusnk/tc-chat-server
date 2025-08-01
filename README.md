# ChatApp (Backend)

**Description**: a Spring Boot server that manages real-time chat among users on various topics. Users can join chat
rooms by selecting a
username only (no authorization needed) and participate in discussions. The server supports multiple concurrent users (
even with the same nickname) without using any external chat libraries.

## **Features:**

- Real-time messaging via WebSocket + STOMP (messages appear instantly).
- Each user's own messages are highlighted on their second open tab.
- Chat view always scrolls to the latest message.
- Typing indicator: users see when someone is typing.
- Timestamps: each message shows date and time.
- Username persistence: a chosen username is remembered across browser sessions.

## **Tech Stack:**

- Java 17, Spring Boot 3.5.2.
- WebSocket + STOMP for messaging.
- PostgreSQL database.
- Docker (and Docker Compose).
- Swagger (Springdoc) for REST API docs.
- Springwolf for AsyncAPI docs of the chat.

## **Setup & Installation:**

1. Clone the repository (e.g. git clone <repo-url>).
2. Make sure Docker and Docker Compose are installed.
3. From the project directory, run <pre>``` docker-compose up -d --build ``` </pre>
   This starts the backend server and the PostgreSQL database.
4. The backend will run on http://localhost:8080.
5. Start the frontend (Angular) separately (e.g. npm install && ng serve) on http://localhost:4200.

## **API Documentation:**

- **Swagger UI** (REST API):
    - An interactive API docs at http://localhost:8080/q/swagger-ui.html
    - JSON is available at http://localhost:8080/q/api-docs
    - YAML at http://localhost:8080/q/api-docs.yaml
- **AsyncAPI** (WebSocket): Springwolf provides the messaging documentation.
    - An interactive AsyncAPI UI at http://localhost:8080/springwolf/asyncapi-ui.html
    - JSON is available at http://localhost:8080/springwolf/docs
    - YAML at http://localhost:8080/springwolf/docs.yaml

**Usage:** Run both backend and frontend as above. Open the front-end URL (e.g. http://localhost:4200), enter a
username, and join or create chat rooms to start chatting.
**Deployment:** The application is containerized and can be deployed on cloud platforms (for example, it’s set up on
Render with a managed PostgreSQL database).
