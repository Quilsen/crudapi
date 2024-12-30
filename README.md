# CRUD API Project

This project is a Spring Boot application with a secure authentication mechanism using JWT and role-based access. It includes REST endpoints for user management and authentication.

## Prerequisites

1. Docker and Docker Compose installed.
2. Java 17 or higher installed.
3. Postman or cURL for API testing.
4. Maven installed for dependency management.

---

## Setup and Run the Application

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-repo-name/project.git
cd project
```

### Step 2: Configure Environment Variables

Create an .env file in the root directory with the following content:

```bash
DB_URL=jdbc:postgresql://localhost:5432/yourdbname
DB_USERNAME=yourdbuser
DB_PASSWORD=yourdbpassword
JWT_SECRET=your_jwt_secret
JWT_EXPIRATION=360000
```
### Step 3: Build and Run the Application

1. Build the project:

```bash
mvn clean install
```
2. Start the application:

```bash
mvn spring-boot:run
```
## Running PostgreSQL in Docker

Use Docker Compose to spin up a PostgreSQL container.

### Step 1: Start PostgreSQL

Run the following command to start PostgreSQL:

```bash
docker-compose up -d
```