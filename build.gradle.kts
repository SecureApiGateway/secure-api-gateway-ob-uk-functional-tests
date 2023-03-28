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
version = "1.0.0"
// commit hash or release name to find the proper library aligned with the services deployed on a cluster
val release = "1.0.0"
val jaxbVersion = "2.2.11"

plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    // https://github.com/edeandrea/xjc-generation-gradle-plugin
    id("com.github.edeandrea.xjc-generation") version "1.6"
    id("maven-publish")
}

/*
* skip unused tasks
*/

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
    maven("https://maven.forgerock.org/repo/community")
}

configurations.all {
    exclude("org.springframework.boot")
    exclude("org.springframework")
    exclude("org.springframework.plugin")
    exclude("io.springfox")
    exclude("io.swagger.core")
    exclude("io.swagger")
    exclude("org.projectlombok")
    exclude("org.slf4")
}

dependencies {
    // xjc generation plugin dependencies
    xjc("javax.xml.bind:jaxb-api:${jaxbVersion}")
    xjc("com.sun.xml.bind:jaxb-impl:${jaxbVersion}")
    xjc("com.sun.xml.bind:jaxb-xjc:${jaxbVersion}")
    xjc("com.sun.xml.bind:jaxb-core:${jaxbVersion}")
    xjc("javax.activation:activation:1.1.1")

    // Align versions of all Kotlin components
    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("javax.xml.bind:jaxb-api:2.3.1")
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(platform("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-bom:0.9.0-SNAPSHOT"))
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-shared")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-obie-datamodel")
    implementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-datamodel")
    testImplementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-obie-datamodel:jar:tests")
    testImplementation("com.forgerock.sapi.gateway:secure-api-gateway-ob-uk-common-datamodel:jar:tests")

    testImplementation("org.bouncycastle:bcprov-jdk15on:1.70")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    testImplementation("org.glassfish.jaxb:jaxb-runtime:2.3.0")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-api:5.7.0")

    // Test libraries
    testImplementation("com.github.kittinunf.fuel:fuel:2.2.1")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.2.1")
    testImplementation("com.github.kittinunf.fuel:fuel-gson:2.2.1")
    testImplementation("com.google.code.gson:gson:2.9.0")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-joda:2.9.8")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.17")
    testImplementation("io.jsonwebtoken:jjwt-api:0.10.7")
    testImplementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    testImplementation("io.jsonwebtoken:jjwt-jackson:0.10.7")
    testImplementation("io.r2:simple-pem-keystore:0.1")
    testImplementation("org.apache.httpcomponents:httpclient:4.5.9")
    testImplementation("org.assertj:assertj-core:3.13.2")
    testImplementation("com.nimbusds:nimbus-jose-jwt:9.0.1")
    testImplementation("commons-io:commons-io:2.6")
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
            schemaRootDir = "src/test/resources/com/forgerock/sapi/gateway/ob/uk/payment/file"
            schemaFile = "pain.001.001.08.xsd"
            // In local environment run first the task schemaGen-xxx or xjcGeneration to generate the objects
            // Remember use this package in kotlin test to resolve the object reference
            // @see line 92 from FilePaymentTest.kt
            javaPackageName = "com.forgerock.sapi.gateway.ob.uk.generated.xml.model.pain00100108"
        }
    }
}

/*
 * Java definitions
 */
java {
    sourceCompatibility = JavaVersion.VERSION_14
    targetCompatibility = JavaVersion.VERSION_14
}

/** ************************************************* */
/**                    profiles                       */
/** ************************************************* */
// @See README file
// default environment profile
var profile = "dev"
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
    archiveFileName.set("${project.name}-${project.version}-$release.jar")
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
val apiVersions = arrayOf("v3_1_8", "v3_1_9", "v3_1_10")

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

tasks.register<Test>("singleTest") {
    description = "Runs open banking single functional tests"
}