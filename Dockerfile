## Imagen linux amd64 con eclipse-temurin 17.0.5
FROM eclipse-temurin:17.0.5_8-jdk-alpine as builder

## Carpeta dentro de la imagen
WORKDIR /app/ms-usuarios

## Copiamos archivos necesarios para empaquetado como librerias, pom, mvn y mvnw
COPY ./pom.xml /app
COPY ./ms-usuarios/.mvn ./.mvn
COPY ./ms-usuarios/mvnw .
COPY ./ms-usuarios/pom.xml .

## Ejecutar empaquetado proyecto pero sin codigo funte y test y borramos target
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# RUN ./mvnw dependency:go-offline

# Copiamos todo el codigo fuente del proyecto
COPY ./ms-usuarios/src ./src

## Ejecutar empaquetado saltandose el test
RUN ./mvnw clean package -DskipTests

## Hacemos una imagen nueva a partir de la anterior
##  -> Imagen de produccion que solo toma el jar quitando asi las dependencias y maven que no
## los necesitamos en produccion
FROM eclipse-temurin:17.0.5_8-jdk-alpine

WORKDIR /app

## Creamos la carpeta para guardar los logs de spring
RUN mkdir ./logs

## Copiamos el jar de la construccion anterior
COPY --from=builder /app/ms-usuarios/target/ms-usuarios-0.0.1-SNAPSHOT.jar .

## Puerto donde se va a ejecutar nuestra app
EXPOSE 8001

## Punto de entrada para ejecucion esta capa es del contenedor
## ENTRYPOINT es mas seguro dado que no permite entrar a la linea de comandos
# ENTRYPOINT ["java","-jar","ms-usuarios-0.0.1-SNAPSHOT.jar"]

## CMD permite entrar a la bash y navegar en la maquina virtual del contenedor
CMD ["java","-jar","ms-usuarios-0.0.1-SNAPSHOT.jar"]