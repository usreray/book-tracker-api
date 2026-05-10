# Book Tracker API

Spring Boot backend for the [Book Tracker](https://github.com/usreray/book-tracker) Flutter app. It handles authentication, cloud sync of each reader's library, reviews, reading challenges, badges, the social feed, and real-time chat.

## Tech stack

- Java 17, Spring Boot 4
- Spring Web MVC, Spring Data JPA, Spring Security, Bean Validation
- PostgreSQL
- JWT authentication (`jjwt`), passwords hashed with bcrypt
- WebSocket + STOMP for direct messages
- springdoc-openapi (Swagger UI)
- Lombok

## Features

- Register and log in with email and password; stateless JWT sessions
- Personal library with *Want to Read*, *Currently Reading*, and *Read* shelves plus page progress
- Ratings and reviews, public per-book review lists
- Yearly reading challenge
- Automatic badges: First Book (1 book), Bookworm (10), Bibliophile (50), Scholar (100), First Review, Critic (10 reviews)
- Follow and unfollow readers, activity feed of followed users
- Profile editing and profile photo upload
- Real-time one-to-one chat with conversation history

## Getting started

### Prerequisites
- JDK 17+
- PostgreSQL with a database named `booktracker`

### Configuration

Secrets are never stored in `application.yml`. Provide them in one of two ways:

**Environment variables**

| Variable | Required | Default |
|---|---|---|
| `DB_PASSWORD` | yes | – |
| `JWT_SECRET` | yes | – (Base64-encoded key, at least 256 bits) |
| `DB_URL` | no | `jdbc:postgresql://localhost:5432/booktracker` |
| `DB_USERNAME` | no | `postgres` |

**Local file (for development)**

```bash
cp src/main/resources/application-local.example.yml src/main/resources/application-local.yml
```

Then fill in your values. `application-local.yml` is git-ignored.

Generate a JWT secret with:

```bash
openssl rand -base64 32
```

### Run

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`. The Swagger UI is available at `http://localhost:8080/swagger-ui.html`.

When the Flutter app runs on the Android emulator it reaches this server at `http://10.0.2.2:8080`.

## API overview

All endpoints except those marked *public* require an `Authorization: Bearer <token>` header.

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/register` | Create an account *(public)* |
| POST | `/api/auth/login` | Log in and receive a JWT *(public)* |
| GET / PUT | `/api/users/me` | Get or update the current user |
| POST | `/api/users/me/photo` | Upload a profile photo |
| GET | `/api/users/search` | Search readers by name or email |
| GET | `/api/users/{id}/profile` | Public profile *(public)* |
| POST / DELETE | `/api/users/{id}/follow` | Follow or unfollow a reader |
| GET / POST | `/api/library` | List library entries or add a book |
| PUT | `/api/library/{isbn}/status` | Move a book to another shelf |
| PUT | `/api/library/{isbn}/progress` | Update the current page |
| DELETE | `/api/library/{isbn}` | Remove a book |
| GET / POST | `/api/reviews` | List own reviews or create one |
| PUT | `/api/reviews/{id}` | Edit a review |
| GET | `/api/reviews/book/{isbn}` | Reviews for a book *(public)* |
| GET | `/api/challenges/current` | Current year's challenge |
| POST | `/api/challenges` | Create or update the challenge |
| GET | `/api/feed` | Activity feed of followed readers |
| GET | `/api/chat/conversations` | Recent conversations |
| GET | `/api/chat/history/{userId}` | Message history with a reader |

### Real-time chat

- Connect to `/ws` (raw WebSocket or SockJS) and send the JWT in the STOMP `CONNECT` headers.
- Send messages to `/app/chat.send`.
- Subscribe to `/topic/chat.user.{yourUserId}` to receive them.

## Project structure

```
src/main/java/com/example/book_tracker_api/
├── config/       # Web MVC (uploads) and WebSocket configuration
├── controller/   # REST and STOMP controllers
├── dto/          # request and response models
├── entity/       # JPA entities and enums
├── exception/    # custom exceptions and global handler
├── repository/   # Spring Data repositories
├── security/     # JWT utility, filter, security config, WebSocket auth
└── service/      # business logic (library, reviews, badges, feed, chat, ...)
```
