# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY mvnw .
COPY .mvn ./.mvn
COPY pom.xml .
RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/dentify-0.0.1.jar app.jar

ENV JAVA_OPTS="-Xmx450m -Xms256m"

EXPOSE ${PORT:-8008}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --server.port=${PORT:-8008}"]