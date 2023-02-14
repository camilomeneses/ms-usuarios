## Imagen linux amd64 con eclipse-temurin 17.0.5
FROM eclipse-temurin:17.0.5_8-jdk-alpine

## Carpeta dentro de la imagen
WORKDIR /app

## Carpeta dentro de nuestra maquina
COPY ./target/ms-usuarios-0.0.1-SNAPSHOT.jar .

## Puerto donde se va a ejecutar nuestra app
EXPOSE 8001

## Punto de entrada para ejecucion
ENTRYPOINT ["java","-jar","ms-usuarios-0.0.1-SNAPSHOT.jar"]