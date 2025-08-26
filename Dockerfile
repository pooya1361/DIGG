# ---------------------------
# STAGE 1: Build Frontend
# ---------------------------
FROM node:20-alpine AS frontend-build

WORKDIR /app

# Copy frontend package files and install dependencies
COPY webapp/package*.json ./
RUN npm install

# Copy frontend source code and build
COPY webapp .
RUN npm run build

# ---------------------------
# STAGE 2: Build Backend
# ---------------------------
FROM maven:3.9.9-eclipse-temurin-21 AS backend-build

WORKDIR /app

# Copy backend pom.xml and fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy backend source code
COPY src ./src

# Copy built frontend into Quarkus resources
COPY --from=frontend-build /app/dist ./src/main/resources/META-INF/resources/

# Build Quarkus app (includes frontend)
RUN mvn clean package -DskipTests

# ---------------------------
# STAGE 3: Runtime Image
# ---------------------------
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy Quarkus app from build stage
COPY --from=backend-build /app/target/quarkus-app /app

# Expose backend port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
