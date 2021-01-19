[<img src="https://raw.githubusercontent.com/ForgeRock/forgerock-logo-dev/master/Logo-fr-dev.png" align="right" width="220px"/>](https://developer.forgerock.com/)

| |Current Status|
|---|---|
|Build|[![Build Status](https://img.shields.io/endpoint.svg?url=https%3A%2F%2Factions-badge.atrox.dev%2FSecureBankingAcceleratorToolkit%2Fsecurebanking-openbanking-uk-functional-tests%2Fbadge%3Fref%3Dmaster&style=flat)](https://actions-badge.atrox.dev/SecureBankingAcceleratorToolkit/securebanking-openbanking-uk-functional-tests/goto?ref=master)|
|Release|[![GitHub release (latest by date)](https://img.shields.io/github/v/release/SecureBankingAcceleratorToolkit/securebanking-openbanking-uk-functional-tests.svg)](https://img.shields.io/github/v/release/SecureBankingAcceleratorToolkit/securebanking-openbanking-uk-functional-tests)
|License|![license](https://img.shields.io/github/license/ACRA/acra.svg)|

# securebanking functional tests (Open Banking UK)
Software testing to validate and coverage the implemented open banking functionalities and ensure that these satisfies the functional requirements.

## Setup
- Gradle 6.8
- Kotlin 1.4.20
- Java 14

It's important to have the VM argument `-Djunit.jupiter.extensions.autodetection.enabled=true` when running the tests.
Fortunately this is already in the tasks defined in the gradle configuration in the [build.gradle.kts](./build.gradle.kts). This VM argument tells JUnit to automatically load the ExecutionCondition
[modules](./src/test/kotlin/com/forgerock/openbanking/junit) defined in [org.junit.jupiter.api.extension.Extension](./src/test/resources/META-INF/services/org.junit.jupiter.api.extension.Extension).

For more information https://junit.org/junit5/docs/5.7.0/user-guide/index.html#extensions-registration-automatic

## Adding a new ExecutionCondition

1. Copy from [examples](./src/test/kotlin/com/forgerock/openbanking/junit)
1. Update the execution condition logic
1. Add FQN of ExecutionCondition class to [org.junit.jupiter.api.extension.Extension](./src/test/resources/META-INF/services/org.junit.jupiter.api.extension.Extension)
1. Add your annotations to classes

## Run single Test on Intellij using JUnit platform
1. Go to `IntelliJ IDEA > preferences > build, execution, deployment > build tools > Gradle`
1. Set `Run tests using` to `IntelliJ IDEA`
1. Set `Gradle JVM` to `java version 14`
   ![gradle-config](docs/assets/img/gradle-config.png)
1. Go to `Run/Debug configuration` and `Edit configuration`
   ![edit-config](docs/assets/img/edit-config.png)
1. Go to properties for `JUnit` template on `Edit templates`
   ![edit-junit-template](docs/assets/img/edit-junit-template.png)
1. Set the `DOMAIN` environment variable value

> This template will use every time you run a new `single test`.
> You can change the Junit template all the times you want

> If you run a test on IntelliJ you can change later the DOMAIN value editing the test configuration
> to run the test against another DOMAIN
![reedit-config](docs/assets/img/reedit-config.png)

## Run tests on IntelliJ using Gradle
![run-gradle-task](docs/assets/img/run-gradle-task.png)

## Run gradle tests manually
- All test
  ```bash
  DOMAIN=the.domain gradle clean [build | test]
  ``` 
  Example 
  ```bash
  DOMAIN=master.forgerock.financial gradle clean build
  ```

- Single test 
  ```bash
  DOMAIN=the.domain gradle test --tests "x.x.y.y.TestClass.testMethod"
  ```
  Example 
  ```bash
  DOMAIN=master.forgerock.financial gradle test --tests "com.forgerock.openbanking.payment.domestic.SingleDomesticPaymentTest.shouldCreateSingleDomesticPayment_v3_1_2"
  ```
