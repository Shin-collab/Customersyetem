# ใช้ Maven Build โค้ด Java ของเรา
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# ใช้ Java รันไฟล์ที่ Build เสร็จแล้ว
FROM eclipse-temurin:21-jdk
COPY --from=build /target/customersystem-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]