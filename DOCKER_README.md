# PING Project - Docker Setup

This project consists of a React frontend, Quarkus backend, and PostgreSQL database, all orchestrated with Docker Compose.

## Prerequisites

- Docker
- Docker Compose

## Services

- **Frontend**: React + Vite application running on port 5173
- **Backend**: Quarkus application running on port 8080
- **Database**: PostgreSQL running on port 5432

## Getting Started

1. **Start all services:**
   ```bash
   docker-compose up -d
   ```

2. **View logs:**
   ```bash
   # All services
   docker-compose logs -f
   
   # Specific service
   docker-compose logs -f backend
   docker-compose logs -f frontend
   docker-compose logs -f postgres
   ```

3. **Stop all services:**
   ```bash
   docker-compose down
   ```

4. **Stop and remove volumes (this will delete the database data):**
   ```bash
   docker-compose down -v
   ```

## Access Points

- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080
- **Database**: localhost:5432 (username: postgres, password: postgres, database: ping)

## Development

The Docker setup includes volume mounts for live reloading:
- Backend: Any changes to the Java code will trigger a reload
- Frontend: Any changes to the React code will be hot-reloaded

## Database

The PostgreSQL database will be automatically created with the name `ping`. The backend is configured to automatically update the database schema on startup.

## Troubleshooting

1. **Backend fails to connect to database**: Wait for the database to be fully ready. The backend service waits for the database health check to pass.

2. **Port conflicts**: If you have services running on ports 5173, 8080, or 5432, stop them or modify the ports in `docker-compose.yml`.

3. **Rebuild after changes**: If you modify Dockerfiles or package.json/pom.xml:
   ```bash
   docker-compose down
   docker-compose up --build
   ```
