# DIGG

A test project for **Myndigheten för digital förvaltning** (Swedish Authority for Digital Government)

## Overview

DIGG is a full-stack web application demonstrating modern CRUD operations for user management. Built as a testing ground for evaluating contemporary web technologies in a government context.

## Tech Stack

### Backend
- **Quarkus 3.25.4** - Supersonic Subatomic Java framework
- **H2 Database** - In-memory database for fast testing
- **Hibernate ORM with Panache** - Simplified data persistence
- **REST Assured** - API testing framework
- **SmallRye Health** - Health check endpoints
- **SmallRye OpenAPI** - API documentation generation
- **Bean Validation** - Request validation

### Frontend  
- **Vue 3** with TypeScript - Progressive JavaScript framework
- **TailwindCSS** - Utility-first CSS framework
- **Vite** - Fast build tool and dev server
- **Vue Router** - Client-side routing
- **Axios** - HTTP client
- **Headless UI + Heroicons** - Accessible UI components

### Architecture
- **Monorepo** - Single repository containing both frontend and backend

## Features

- **Complete User CRUD Operations** - Create, Read, Update, Delete users
- **User Search** - Search users by name
- **Email Lookup** - Find users by email address
- **User Statistics** - Get total user count
- **Email Uniqueness** - Prevents duplicate email addresses
- **Input Validation** - Server-side validation with proper error responses
- **Health Monitoring** - Application health endpoint
- **API Documentation** - Auto-generated Swagger/OpenAPI documentation

## User Data Model

Each user contains:
- **Name** - User's full name
- **Email** - Unique email address
- **Address** - Physical address
- **Telephone** - Phone number

## Prerequisites

- **Java 21** (required for Quarkus 3.25.4)
- **Node.js 18+** (for Vue 3 and Vite)
- **Maven 3.8+** (for Java dependencies)
- **npm or yarn** (for frontend dependencies)

## Quick Start

### 1. Clone the repository
```bash
git clone DIGG
cd digg
```

### 2. Start the backend (Quarkus)
```bash
mvn quarkus:dev
```
Backend will start on `http://localhost:8080`

### 3. Start the frontend (Vue 3)
```bash
# Navigate to frontend directory
cd webapp
npm install
npm run dev
```
Frontend will start on `http://localhost:3000`

## API Endpoints

### Users
- `GET /users` - List all users
- `GET /users/{id}` - Get user by ID
- `GET /users/search?name={name}` - Search users by name
- `GET /users/email/{email}` - Get user by email address
- `GET /users/count` - Get total user count
- `POST /users` - Create new user (validates email uniqueness)
- `PUT /users/{id}` - Update user (validates email uniqueness)
- `DELETE /users/{id}` - Delete user

### System
- `GET /q/health` - Application health check
- `GET /q/swagger-ui` - Interactive API documentation
- `GET /q/openapi` - OpenAPI specification (JSON/YAML)

## Example API Usage

### Create User
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john.doe@example.com", 
    "address": "123 Main St, Stockholm",
    "telephone": "070-123-4567"
  }'
```

### Search Users
```bash
curl "http://localhost:8080/users/search?name=John"
```

## Testing

Run backend tests with REST Assured:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

Run with native compilation (requires GraalVM):
```bash
mvn package -Pnative
```

## Project Structure

```
digg/
├── src/main/java/
│   └── com/digg/
│       ├── entity/User.java        # Panache entity
│       └── resource/UserResource.java  # REST endpoints
├── src/main/resources/
│   ├── application.properties      # Quarkus configuration
├── src/test/java/                  # REST Assured tests
├── frontend/                       # Vue 3 application
│   ├── src/
│   ├── package.json
│   ├── vite.config.ts
│   └── tailwind.config.js
├── pom.xml                         # Maven configuration
└── README.md
```

## Development

### Backend Development
- **Quarkus dev mode** enables hot reload: `mvn quarkus:dev`
- **Dev UI** available at `http://localhost:8080/q/dev-ui`
- **Database console** at `http://localhost:8080/q/dev-ui/io.quarkus.quarkus-datasource/datasources` (H2 console)

### Frontend Development  
- **Vite dev server** with hot reload: `npm run dev`
- **TypeScript support** with Vue 3 Composition API
- **TailwindCSS** with forms plugin for rapid styling

### Database

Uses **H2 in-memory database** configured via Quarkus. Data is reset on application restart, making it perfect for testing and development.

**Key features:**
- Automatic table creation from Panache entities
- SQL import scripts supported (`import.sql`)
- Web console available in dev mode

## API Documentation

Once the backend is running, explore the API at:
- **Swagger UI:** `http://localhost:8080/q/swagger-ui`
- **Health JSON:** `http://localhost:8080/q/health`

## Dependencies

### Backend (Maven)
- **Quarkus Platform:** 3.25.4
- **Java:** 21
- **H2 Database:** In-memory
- **REST Assured:** Testing

### Frontend (npm)
- **Vue:** 3.4+
- **TypeScript:** 5.3+
- **TailwindCSS:** 3.4+
- **Vite:** 5.0+
- **Headless UI & Heroicons:** Accessible components

## Error Handling

The API includes proper error responses:
- `404` - User not found
- `409` - Email already exists (conflict)
- `400` - Validation errors
- `204` - Successful deletion (no content)

## Contributing

This is a test project for evaluating technologies and approaches for digital government services.

## License

[Add appropriate license information]

---

**Myndigheten för digital förvaltning**  
Testing modern web technologies for government digital services