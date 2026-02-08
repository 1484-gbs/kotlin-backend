# ベースイメージとしてOpenJDKを使用
FROM amazoncorretto:17 AS build
WORKDIR /app

RUN yum install -y dos2unix && yum clean all

# Gradle/Mavenのラッパーと設定ファイルをコピー
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .

COPY ./api/src/ ./api/src/
COPY ./api/build.gradle ./api/
COPY ./api/settings.gradle ./api/

COPY ./db/src/ ./db/src/
COPY ./db/build.gradle ./db/
COPY ./db/settings.gradle ./db/

COPY ./batch/src/ ./batch/src/
COPY ./batch/build.gradle ./batch/
COPY ./batch/settings.gradle ./batch/

# 改行コード修正と権限付与
RUN find . -type f -name "gradlew" -exec dos2unix {} + && chmod +x ./gradlew

# 依存関係を先にダウンロード（キャッシュ利用のため）
RUN ./gradlew dependencies --no-daemon

# ビルド
RUN ./gradlew :api:bootJar --no-daemon

FROM amazoncorretto:17
WORKDIR /app

# ビルドステージから作成されたjarのみをコピー
COPY --from=build /app/api/build/libs/api-0.0.1-SNAPSHOT.jar api.jar

ENTRYPOINT ["java", "-jar", "api.jar"]
# ENTRYPOINT ["java", "-cp", "/app/api/build/resources:/app/api/build/classes:/app/api/build/libs/*", "com.example.demo.DemoApplication"]