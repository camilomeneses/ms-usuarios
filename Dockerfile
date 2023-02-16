## Argumentos globales
ARG MS_NAME=ms-usuarios

## Imagen linux amd64 con eclipse-temurin 17.0.5
FROM eclipse-temurin:17.0.5_8-jdk-alpine as builder

#Argumentos de construccion de imagen
ARG MS_NAME

## Carpeta dentro de la imagen
WORKDIR /app/$MS_NAME

## Copiamos archivos necesarios para empaquetado como librerias, pom, mvn y mvnw
COPY ./pom.xml /app
COPY ./$MS_NAME/.mvn ./.mvn
COPY ./$MS_NAME/mvnw .
COPY ./$MS_NAME/pom.xml .

## Ejecutar empaquetado proyecto pero sin codigo funte y test y borramos target
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/
# RUN ./mvnw dependency:go-offline

# Copiamos todo el codigo fuente del proyecto
COPY ./$MS_NAME/src ./src

## Ejecutar empaquetado saltandose el test
RUN ./mvnw clean package -DskipTests

## Hacemos una imagen nueva a partir de la anterior
##  -> Imagen de produccion que solo toma el jar quitando asi las dependencias y maven que no
## los necesitamos en produccion
FROM eclipse-temurin:17.0.5_8-jdk-alpine

WORKDIR /app

## Creamos la carpeta para guardar los logs de spring
RUN mkdir ./logs

## Argumentos de construccion
ARG MS_NAME
ARG TARGET_FOLDER=/app/$MS_NAME/target
ARG PORT_APP=8001

## Copiamos el jar de la construccion anterior
COPY --from=builder $TARGET_FOLDER/ms-usuarios-0.0.1-SNAPSHOT.jar .

## Variables de ambiente
ENV PORT $PORT_APP

## Puerto donde se va a ejecutar nuestra app
EXPOSE $PORT

## Punto de entrada para ejecucion esta capa es del contenedor docker RUN
## ENTRYPOINT es mas seguro dado que no permite entrar a la linea de comandos
# ENTRYPOINT ["java","-jar","ms-usuarios-0.0.1-SNAPSHOT.jar"]

## CMD permite entrar a la bash y navegar en la maquina virtual del contenedor
CMD ["java","-jar","ms-usuarios-0.0.1-SNAPSHOT.jar"]