FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY .mvn .mvn
COPY mvnw pom.xml ./
# As dependências ficam em uma camada própria, para não baixar tudo a cada mudança no código.
RUN sh mvnw -B -q dependency:go-offline
COPY src src
RUN sh mvnw -B -q package -DskipTests && mv target/*.jar app.jar

FROM eclipse-temurin:21-jre
RUN useradd --system --no-create-home lab
WORKDIR /app
COPY --from=build /build/app.jar app.jar
USER lab
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
