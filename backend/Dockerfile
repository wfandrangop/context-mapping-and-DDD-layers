# --- Etapa 1: Builder (Construcción) ---
# Usamos una imagen que ya tiene Maven y el JDK instalado
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /build

# Copiar solo el pom.xml para aprovechar el cache de capas de Docker
COPY pom.xml .
# Descargar dependencias (esto se cachea si el pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente y construir el JAR
COPY src ./src
RUN mvn clean package -DskipTests

# --- Etapa 2: Runtime (Ejecución) ---
# Usamos JRE para una imagen más ligera y segura
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Crear un usuario de sistema para no ejecutar como root
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring

# Copiar el JAR desde la etapa de construcción
# Usamos un nombre específico para evitar copiar JARs de fuentes o documentación
COPY --from=builder /build/target/*.jar app.jar

# Configuración de variables de entorno óptimas para Java en contenedores
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8080

# Ejecutar con las opciones de optimización de memoria
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]