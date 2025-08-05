plugins {
    java
    jacoco
    id("org.springframework.boot") version "3.4.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    id("io.freefair.lombok") version "8.14"
    id("org.sonarqube") version "6.2.0.5505"
}

group = "vn.edu.fpt"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// JaCoCo Configuration
jacoco {
    toolVersion = "0.8.13"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

// SonarQube Configuration
sonar {
    properties {
        property("sonar.projectKey", "sep490_g80_transit-link-backend")
        property("sonar.organization", "sep490-g80")
        property ("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/jacocoCombinedTestReport/jacocoCombinedTestReport.xml").get().asFile.absolutePath)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val springModulithVersion = "1.4.1"

sourceSets {
    create("unitTest") {
        java.srcDir("src/unitTest/java")
        resources.srcDir("src/unitTest/resources")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += output + compileClasspath
    }

    create("integrationTest") {
        java.srcDir("src/integrationTest/java")
        resources.srcDir("src/integrationTest/resources")
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += output + compileClasspath
    }
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Spring Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")

    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql")

    // Devtools for hot reload (development only)
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Lombok (for annotations like @Getter, @Builder, etc.)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // MapStruct for object mapping
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    // firebase-admin
    implementation("com.google.firebase:firebase-admin:9.5.0")

    val sharedTestDeps = listOf(
        "org.springframework.boot:spring-boot-starter-test",
        "org.springframework.modulith:spring-modulith-starter-test",
        "org.springframework.security:spring-security-test",
        "org.junit.platform:junit-platform-launcher"
    )

    sharedTestDeps.forEach {
        add("unitTestImplementation", it)
        add("integrationTestImplementation", it)
    }
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.modulith:spring-modulith-bom:$springModulithVersion")
    }
}

tasks {
    // Unit Test Task
    val unitTest by registering(Test::class) {
        description = "Run unit tests"
        group = "verification"
        testClassesDirs = sourceSets["unitTest"].output.classesDirs
        classpath = sourceSets["unitTest"].runtimeClasspath
        useJUnitPlatform()

        // JaCoCo configuration for unit tests
        extensions.configure(JacocoTaskExtension::class) {
            isEnabled = true
            setDestinationFile(layout.buildDirectory.file("jacoco/unitTest.exec").get().asFile)
            includes = emptyList()
            excludes = listOf(
                "**/config/**",
                "**/dto/**",
                "**/entity/**",
                "**/*Application*",
                "**/*Config*"
            )
            isIncludeNoLocationClasses = false
            isDumpOnExit = true
        }
    }

    // Integration Test Task
    val integrationTest by registering(Test::class) {
        description = "Run integration tests"
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        useJUnitPlatform()
        shouldRunAfter(unitTest)

        // JaCoCo configuration for integration tests
        extensions.configure(JacocoTaskExtension::class) {
            isEnabled = true
            setDestinationFile(layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile)
            includes = emptyList()
            excludes = listOf(
                "**/config/**",
                "**/dto/**",
                "**/entity/**",
                "**/*Application*",
                "**/test/**"
            )
            isIncludeNoLocationClasses = false
            isDumpOnExit = true
        }
    }

    // JaCoCo Report for Unit Tests
    val jacocoUnitTestReport by registering(JacocoReport::class) {
        description = "Generate JaCoCo coverage report for unit tests"
        group = "reporting"

        dependsOn(unitTest)
        executionData(unitTest.get())
        sourceSets(sourceSets["main"])

        reports {
            xml.required = true
            csv.required = false
            html.required = true
            html.outputLocation = layout.buildDirectory.dir("reports/jacoco/unitTest")
        }
    }

    // JaCoCo Report for Integration Tests
    val jacocoIntegrationTestReport by registering(JacocoReport::class) {
        description = "Generate JaCoCo coverage report for integration tests"
        group = "reporting"

        dependsOn(integrationTest)
        executionData(integrationTest.get())
        sourceSets(sourceSets["main"])

        reports {
            xml.required = true
            csv.required = false
            html.required = true
            html.outputLocation = layout.buildDirectory.dir("reports/jacoco/integrationTest")
        }
    }

    // Combined JaCoCo Report (Unit + Integration Tests)
    val jacocoCombinedTestReport by registering(JacocoReport::class) {
        description = "Generate combined JaCoCo coverage report"
        group = "reporting"

        dependsOn(unitTest, integrationTest)
        executionData(unitTest.get(), integrationTest.get())
        sourceSets(sourceSets["main"])

        reports {
            xml.required = true
            csv.required = false
            html.required = true
            html.outputLocation = layout.buildDirectory.dir("reports/jacoco/combined")
        }
    }

    // Coverage Verification for Unit Tests
    val jacocoUnitTestCoverageVerification by registering(JacocoCoverageVerification::class) {
        description = "Verify unit test code coverage metrics"
        group = "verification"

        dependsOn(unitTest)
        executionData(unitTest.get())
        sourceSets(sourceSets["main"])

        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.60".toBigDecimal() // 60% instruction coverage for unit tests
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.50".toBigDecimal() // 50% branch coverage for unit tests
                }
            }
        }
    }

    // Coverage Verification for Integration Tests
    val jacocoIntegrationTestCoverageVerification by registering(JacocoCoverageVerification::class) {
        description = "Verify integration test code coverage metrics"
        group = "verification"

        dependsOn(integrationTest)
        executionData(integrationTest.get())
        sourceSets(sourceSets["main"])

        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.40".toBigDecimal() // 40% instruction coverage for integration tests
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.30".toBigDecimal() // 30% branch coverage for integration tests
                }
            }
        }
    }

    // Disable default test task
    test {
        enabled = false
    }

    // Configure unit test reporting
    unitTest {
        finalizedBy(jacocoUnitTestReport)
    }

    jacocoUnitTestReport {
        finalizedBy(jacocoUnitTestCoverageVerification)
    }

    // Configure integration test reporting
    integrationTest {
        finalizedBy(jacocoIntegrationTestReport)
    }

    jacocoIntegrationTestReport {
        finalizedBy(jacocoIntegrationTestCoverageVerification)
    }

    // Configure check task to run all tests and generate combined report
    check {
        dependsOn(unitTest, integrationTest)
        finalizedBy(jacocoCombinedTestReport)
    }

    // Configure build task
    build {
        dependsOn(check)
    }
}