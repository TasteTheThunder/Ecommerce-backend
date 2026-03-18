# ===============================
# BUILD STAGE
# ===============================
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first and download deps
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# ===============================
# RUNTIME STAGE
# ===============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy your exact jar from build stage
COPY --from=build /app/target/Sb-Ecom-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9090

ENTRYPOINT ["java","-jar","/app/app.jar"]
