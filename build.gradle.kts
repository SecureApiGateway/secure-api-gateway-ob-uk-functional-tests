import org.gradle.api.tasks.testing.logging.TestExceptionFormat

/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 */

/*
 * Global Variables
 */
// project version
// pom artifact version used when the built artifact is published
// Test jar library version used in the task 'generateTestJar'
version = "4.0.3"
val jaxbVersion = "4.0.1"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    // https://github.com/edeandrea/xjc-generation-gradle-plugin
    id("com.github.edeandrea.xjc-generation") version "1.6"
    id("maven-publish")
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

/*
 * In case that we publish the artifact
 */
publishing {
    publications {
        register("secure-api-gateway-ob-uk-functional-tests", MavenPublication::class) {
            pom {
                name.set("secure-api-gateway-ob-uk-functional-tests")
                groupId = "com.forgerock.sapi.gateway"
                artifactId = "secure-api-gateway-ob-uk-functional-tests"
                version = project.version.toString()
            }
        }
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://www.jitpack.io")
    maven("https://maven.forgerock.org/artifactory/community")
}

configurations.all {
    exclude("org.springframework.boot")
    exclude("org.springframework.data")
    exclude("org.springframework.plugin")
    exclude("io.springfox")
    exclude("io.swagger.core")
    exclude("io.swagger")
    exclude("org.projectlombok")
    exclude("org.slf4")
}

dependencies {
    // xjc generation plugin dependencies
    xjc("jakarta.xml.bind:jakarta.xml.bind-api:${jaxbVersion}")
    xjc("com.sun.xml.bind:jaxb-impl:${jaxbVersion}")
    xjc("com.sun.xml.bind:jaxb-xjc:${jaxbVersion}")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-bom:4.0.4-SNAPSHOT"))
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-shared")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-obie-datamodel")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-datamodel")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-error")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-obie-datamodel:jar:tests")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-datamodel:jar:tests")

    implementation("org.bouncycastle:bcprov-jdk15on:1.70")
    implementation("org.bouncycastle:bcpkix-jdk15on:1.70")

    implementation("jakarta.xml.bind:jakarta.xml.bind-api:${jaxbVersion}")
    implementation("com.sun.xml.bind:jaxb-impl:${jaxbVersion}")

    implementation("com.github.kittinunf.fuel:fuel:2.2.1")
    implementation("com.github.kittinunf.fuel:fuel-jackson:2.2.1")
    implementation("com.github.kittinunf.fuel:fuel-gson:2.2.1")
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.9.8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.willowtreeapps.assertk:assertk-jvm:0.17")
    implementation("io.jsonwebtoken:jjwt-api:0.10.7")
    implementation("io.jsonwebtoken:jjwt-impl:0.10.7")
    implementation("io.jsonwebtoken:jjwt-jackson:0.10.7")
    implementation("io.r2:simple-pem-keystore:0.3")
    implementation("org.apache.httpcomponents:httpclient:4.5.9")
    implementation("org.assertj:assertj-core:3.13.2")
    implementation("com.nimbusds:nimbus-jose-jwt:9.0.1")
    implementation("commons-io:commons-io:2.6")

    implementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
}

/*
 * Generate the payment objects from defined schema
 */
xjcGeneration {
    defaultBindingFile = null
    schemas.register("schema-pain.001.001.08")
    schemas {
        "schema-pain.001.001.08" {
            taskName = "gen-pain00100108-source"
            schemaRootDir = "src/main/resources/com/forgerock/sapi/gateway/ob/uk/payment/file"
            schemaFile = "pain.001.001.08.xsd"
            // In local environment run first the task schemaGen-xxx or xjcGeneration to generate the objects
            // Remember use this package in kotlin test to resolve the object reference
            // @see line 92 from FilePaymentTest.kt
            javaPackageName = "com.forgerock.sapi.gateway.ob.uk.generated.xml.model.pain00100108"
        }
    }
}

/** ************************************************* */
/**                    profiles                       */
/** ************************************************* */
// @See README file
// default environment profile
var profile = "dev-cdk-ob"
var profilePath = "gradle/profiles/profile-$profile.gradle.kts"

if (project.hasProperty("profile")) {
    profile = project.property("profile").toString()
    profilePath = "gradle/profiles/profile-$profile.gradle.kts"
    apply(from = profilePath)
    println("Profile has been provided, profile [$profile][$profilePath] applied")
} else {
    apply(from = profilePath)
    println("No profile provided, profile [$profile][$profilePath] applied")
}
// override profile properties from command line or system properties set
println("Overriding properties by command line....")
project.extra.properties.forEach { (key, _) ->
    if (System.getProperties().containsKey(key)) {
        println("* overriding [$key] sys prop")
        project.extra[key] = System.getProperty(key)
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}

/**
 ********************************************************************
 * TASKS
 ********************************************************************
 */


/*
 * scope generic tasks
 */
tasks {
    test {
        useJUnitPlatform()
        description = "Runs ALL tests"
    }
}

// To generate the tests library
tasks.register<Jar>("generateTestJar") {
    group = "specific"
    description = "Generate a non-executable jar library tests"
    archiveClassifier.set("tests")
    archiveFileName.set("${project.name}-${project.version}-$version.jar")
    from(sourceSets.test.get().allSource)
    from(sourceSets.main.get().allSource)
    dependsOn("testClasses")
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to "Secure API Gateway Functional Tests",
                "Implementation-Title" to project.name,
                "Implementation-Version" to project.version,
                "Created-by" to "${project.version} (forgerock)",
                "Built-by" to System.getProperty("user.name"),
                "Build-Jdk" to JavaVersion.current(),
                "Source-Compatibility" to project.properties["sourceCompatibility"],
                "Target-Compatibility" to project.properties["targetCompatibility"]
            )
        )
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()

    println("PROFILE --> $profile")
    println("PROFILE FILE --> $profilePath")
    println("RUNNING task [$name]")
    environment = project.extra.properties

    group = "special-tasks-4tests"

    // execution conditions (see readme file)
    systemProperty("junit.platform.output.capture.stdout", "true")
    systemProperty("junit.jupiter.extensions.autodetection.enabled", "true")
    /* execution properties */
    // Indicates if this task will fail on the first failed test
    failFast = true
    minHeapSize = "512M"
    maxHeapSize = "2G"
    // You can run your tests in parallel by setting this property to a value greater than 1
    // default value when isn't set in the task
    maxParallelForks = 1
    testLogging.showStandardStreams = true
    testLogging.exceptionFormat = TestExceptionFormat.FULL

    // Disable test output caching as these are integration tests and therefore are environment dependent
    outputs.upToDateWhen { false }
}
/* ********************************************* */
/*                 TEST TASKS                    */
/* ********************************************* */
// tests properties
val packagePrefix = "com.forgerock.sapi.gateway.ob.uk.tests.functional."
val suffixPattern = ".*"
val apiVersions = arrayOf("v3_1_8", "v3_1_9", "v3_1_10", "v4_0_0")

// Add test tasks for each supported apiVersion
for (apiVersion in apiVersions) {
    /* ACCOUNTS */
    tasks.register<Test>("accounts_$apiVersion") {
        group = "accounts-tests"
        description = "Runs the account tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "account" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    /* DOMESTIC PAYMENTS */
    tasks.register<Test>("domestic_payments_$apiVersion") {
        group = "payments-tests"
        description = "Runs the domestic payments tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.domestic.payments" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    tasks.register<Test>("domestic_scheduled_payments_$apiVersion") {
        group = "payments-tests"
        description = "Runs the domestic scheduled payments tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.domestic.scheduled.payments" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    tasks.register<Test>("domestic_standing_order_$apiVersion") {
        group = "payments-tests"
        description = "Runs the domestic standing order tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.domestic.standing.order" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    tasks.register<Test>("international_payments_$apiVersion") {
        group = "payments-tests"
        description = "Runs the international payments tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.international.payments" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    tasks.register<Test>("international_scheduled_payments_$apiVersion") {
        group = "payments-tests"
        description = "Runs the international scheduled payments tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.international.scheduled.payments" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    tasks.register<Test>("international_standing_orders_$apiVersion") {
        group = "payments-tests"
        description = "Runs the international standing order tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.international.standing.orders" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    tasks.register<Test>("file_payments_$apiVersion") {
        group = "payments-tests"
        description = "Runs the file payments tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "payment.file.payments" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    /* FUNDS CONFIRMATIONS TESTS */
    tasks.register<Test>("funds_confirmations_$apiVersion") {
        group = "funds-confirmations-tests"
        description = "Runs the funds confirmation tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "funds" + suffixPattern + apiVersion)
        }
        failFast = false
    }

    /* ALL IMPLEMENTED TESTS */
    tasks.register<Test>("tests_$apiVersion") {
        group = "tests"
        description = "Runs the tests with the version $apiVersion"
        filter {
            includeTestsMatching(packagePrefix + "account" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.domestic.payments" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.domestic.scheduled.payments" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.domestic.standing.order" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.international.payments" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.international.scheduled.payments" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.international.standing.orders" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.file.payments" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "payment.domestic.vrp" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "funds" + suffixPattern + apiVersion)
            includeTestsMatching(packagePrefix + "events" + suffixPattern + apiVersion)
        }
        failFast = false
    }
}

tasks.register<Test>("domestic_vrps_v3_1_10") {
    group = "payments-tests"
    description = "Runs the domestic vrps tests with the version v3_1_10"
    filter {
        includeTestsMatching(packagePrefix + "payment.domestic.vrp" + suffixPattern + "v3_1_10")
    }
    failFast = false
}

tasks.register<Test>("domestic_vrps_v4_0_0") {
    group = "payments-tests"
    description = "Runs the domestic vrps tests with the version v4_0_0"
    filter {
        includeTestsMatching(packagePrefix + "payment.domestic.vrp" + suffixPattern + "v4_0_0")
    }
    failFast = false
}

tasks.register<Test>("events_v3_1_10") {
    group = "events-tests"
    description = "Runs the events notification tests with the version v3_1_10"
    filter {
        includeTestsMatching(packagePrefix + "events" + suffixPattern + "v3_1_10")
    }
    failFast = false
}

tasks.register<Test>("events_v4_0_0") {
    group = "events-tests"
    description = "Runs the events notification tests with the version v4_0_0"
    filter {
        includeTestsMatching(packagePrefix + "events" + suffixPattern + "v4_0_0")
    }
    failFast = false
}

tasks.register<Test>("singleTest") {
    description = "Runs open banking single functional tests"
    failFast = false
}
