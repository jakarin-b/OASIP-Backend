FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:11
COPY --from=build /app/target/sj1-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]


