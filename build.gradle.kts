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
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/jacocoCombinedTestReport/jacocoCombinedTestReport.xml")
                .get().asFile.absolutePath
        )
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
val openApiVersion = "2.8.9"
val mapstructVersion = "1.6.3"
val firebaseAdminVersion = "9.5.0"
val mapstructLombokBindingVersion = "0.2.0"

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
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")
    // firebase-admin
    implementation("com.google.firebase:firebase-admin:${firebaseAdminVersion}")
    // MapStruct for object mapping
    implementation("org.mapstruct:mapstruct:${mapstructVersion}")
    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql")
    // Devtools for hot reload (development only)
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // Lombok (for annotations like @Getter, @Builder, etc.)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    // MapStruct Lombok binding for better integration with Lombok
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$mapstructLombokBindingVersion")

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
        ignoreFailures = false // Continue running tests even if some fail Or throw exceptions
        useJUnitPlatform()

        outputs.upToDateWhen {
            // Check if jacoco exec file exists and is newer than source files
            val execFile = layout.buildDirectory.file("jacoco/unitTest.exec").get().asFile
            execFile.exists() &&
                    execFile.lastModified() > sourceSets["main"].allSource.files.maxOfOrNull { it.lastModified() } ?: 0
        }

        // JaCoCo configuration for unit tests
        extensions.configure(JacocoTaskExtension::class) {
            isEnabled = true
            setDestinationFile(layout.buildDirectory.file("jacoco/unitTest.exec").get().asFile)
            includes = emptyList()
            excludes = listOf(
                "**/shared/**",
                "**/config/**",
                "**/dto/**",
                "**/entity/**",
                "**/exception/**",
                "**/mapper/**",
                "**/repository/**",
                "**/*Application*",
                "**/unitTest/**",
                "**/integrationTest/**"
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
        ignoreFailures = false // Continue running tests even if some fail Or throw exceptions
        useJUnitPlatform()
        shouldRunAfter(unitTest)

        outputs.upToDateWhen {
            val execFile = layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile
            execFile.exists() &&
                    execFile.lastModified() > sourceSets["main"].allSource.files.maxOfOrNull { it.lastModified() } ?: 0
        }

        // JaCoCo configuration for integration tests
        extensions.configure(JacocoTaskExtension::class) {
            isEnabled = true
            setDestinationFile(layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile)
            includes = emptyList()
            excludes = listOf(
                "**/shared/**",
                "**/config/**",
                "**/dto/**",
                "**/entity/**",
                "**/exception/**",
                "**/mapper/**",
                "**/repository/**",
                "**/*Application*",
                "**/unitTest/**",
                "**/integrationTest/**"
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

        doFirst {
            val unitExec = layout.buildDirectory.file("jacoco/unitTest.exec").get().asFile
            val integExec = layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile

            if (!unitExec.exists() || !integExec.exists()) {
                throw GradleException("""
                    |JaCoCo execution files not found. Please run tests first:
                    |  ./gradlew unitTest integrationTest jacocoCombinedTestReport
                """.trimMargin())
            }
        }

        executionData.from(
            fileTree(layout.buildDirectory.dir("jacoco")).include("unitTest.exec", "integrationTest.exec")
        )
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
                    minimum = "0.85".toBigDecimal()
                }
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.85".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.8".toBigDecimal()
                }
                isFailOnViolation = true // Set to false to allow builds to pass even if coverage is below thresholds
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
                    minimum = "0.7".toBigDecimal()
                }
                limit {
                    counter = "LINE"
                    value = "COVEREDRATIO"
                    minimum = "0.75".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.6".toBigDecimal()
                }
                isFailOnViolation = true // Set to false to allow builds to pass even if coverage is below thresholds
            }

        }
    }

    // Disable default test task
    test {
        enabled = false
    }

    // Configure check task to run unit test coverage verification
    unitTest() {
        finalizedBy(jacocoUnitTestCoverageVerification)
    }

    // Configure check task to run integration test coverage verification
    integrationTest() {
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