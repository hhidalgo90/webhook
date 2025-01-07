# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Define el directorio de trabajo en el contenedor
WORKDIR /app

# Copia el archivo JAR generado en el contenedor
COPY target/shopifywebhook-0.0.1-SNAPSHOT.jar shopifywebhook.jar

# Exponer el puerto que utiliza tu aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "shopifywebhook.jar"]
