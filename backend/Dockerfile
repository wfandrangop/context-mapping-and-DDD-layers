FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
COPY checkstyle.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

RUN addgroup --system spring && adduser --system spring --ingroup spring
RUN mkdir -p /app/uploads && chown -R spring:spring /app
USER spring

COPY --from=builder /build/target/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://postgres:5432/veritrabajo"
ENV SPRING_DATASOURCE_USERNAME="veritrabajo"
ENV SPRING_DATASOURCE_PASSWORD="veritrabajo"
ENV SPRING_JPA_HIBERNATE_DDL_AUTO="update"
ENV SPRING_JPA_SHOW_SQL="false"
ENV APP_STORAGE_UPLOAD_DIR="/app/uploads"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
