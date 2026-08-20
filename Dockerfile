# Dockerfile - ambiente reproducible para P01 (Java 11 + Maven)
#
# Construir:   docker build -t tcsw-ventas:p01 .
# Ejecutar pruebas dentro del contenedor:
#   docker run --rm tcsw-ventas:p01
#
# El contenedor compila el proyecto y ejecuta "mvn clean test" como parte
# del build, de modo que una imagen construida con exito ya es evidencia
# de que el producto compila y las pruebas pasan.

FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -B clean test package

FROM eclipse-temurin:11-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/tcsw-ventas.jar ./tcsw-ventas.jar
CMD ["echo", "Build e imagen listos. El artefacto compilado esta en /app/tcsw-ventas.jar"]
