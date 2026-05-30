# Utiliser une image OpenJDK pour lancer l'application
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copier les fichiers Maven
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

# Copier le code source et compiler
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Étape finale : créer une image légère
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Exposer le port par défaut de Spring Boot
EXPOSE 8090

# Lancer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]