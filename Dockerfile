# 1단계: Gradle로 JAR 빌드
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
# gradlew 실행 권한 부여 (Windows에서 커밋 시 누락될 수 있음)
RUN chmod +x gradlew
# 메모리 제한 설정 후 빌드 (Render 무료 티어 512MB 대응)
RUN GRADLE_OPTS="-Xmx400m -Xms200m" ./gradlew build -x test --no-daemon

# 2단계: JRE만으로 실행 (이미지 경량화)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/strnd_be-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Xmx400m", "-jar", "app.jar"]
