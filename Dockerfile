FROM amazoncorretto:17
WORKDIR /app

RUN yum install -y dos2unix && yum clean all

COPY . .

RUN ./gradlew dependencies --no-daemon || return 0

ENTRYPOINT ["./gradlew", ":api:bootRun", "--no-daemon"]
