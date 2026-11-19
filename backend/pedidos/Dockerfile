# ---------- Etapa 1: build (JDK + Maven) ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
# Primero solo el pom: las dependencias quedan en caché mientras no cambie
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
# Los tests de contexto necesitan BD y RabbitMQ; se corren aparte
RUN mvn -B -q package -DskipTests

# ---------- Etapa 2: runtime (solo JRE) ----------
FROM eclipse-temurin:21-jre
# curl solo para el health check del compose
RUN apt-get update \
 && apt-get install -y --no-install-recommends curl \
 && rm -rf /var/lib/apt/lists/* \
 && useradd -r -u 1001 spring
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
USER spring
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
