FROM azul/zulu-openjdk-alpine:25-latest

COPY src /opt/functional-tests/src
COPY build.gradle.kts /opt/functional-tests/build.gradle.kts
COPY settings.gradle.kts /opt/functional-tests/settings.gradle.kts
COPY gradle /opt/functional-tests/gradle
COPY gradlew /opt/functional-tests/gradlew

# In your Dockerfile
ARG FR_ARTIFACTORY_USER
ARG FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD

# Set them as ENV so Gradle can see them
ENV FR_ARTIFACTORY_USER=$FR_ARTIFACTORY_USER
ENV FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD=$FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD

WORKDIR /opt/functional-tests

RUN ./gradlew compileTestKotlin

CMD ["./gradlew", "cleanTest", "tests_v3_1_10", "-i", "-Djunit.jupiter.extensions.autodetection.enabled=true"]
