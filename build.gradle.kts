plugins {
    java
    jacoco
    idea
    id("org.springframework.boot") version "3.4.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
    id("io.freefair.lombok") version "8.14"
    id("org.sonarqube") version "6.2.0.5505"
}

group = "vn.edu.fpt"
version = "1.0.0-SNAPSHOT"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

// JaCoCo
jacoco {
    toolVersion = "0.8.13"
    reportsDirectory = layout.buildDirectory.dir("reports/jacoco")
}

// SonarQube
sonar {
    properties {
        property("sonar.projectKey", "sep490_g80_transit-link-backend")
        property("sonar.organization", "sep490-g80")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/jacocoCombinedTestReport/jacocoCombinedTestReport.xml")
                .get().asFile.absolutePath
        )
    }
}
sourceSets {
    val unitTest by creating {
        java.srcDir("src/unitTest/java")
        resources.srcDir("src/unitTest/resources")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }

    val integrationTest by creating {
        java.srcDir("src/integrationTest/java")
        resources.srcDir("src/integrationTest/resources")
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }
}

// ---------------- Configurations ----------------
val unitTestImplementation = configurations[sourceSets["unitTest"].implementationConfigurationName]
val unitTestRuntimeOnly = configurations[sourceSets["unitTest"].runtimeOnlyConfigurationName]
val integrationTestImplementation = configurations[sourceSets["integrationTest"].implementationConfigurationName]
val integrationTestRuntimeOnly = configurations[sourceSets["integrationTest"].runtimeOnlyConfigurationName]

configurations {
    val unitTest = sourceSets["unitTest"]
    getByName(unitTest.implementationConfigurationName).extendsFrom(getByName("testImplementation"))
    getByName(unitTest.runtimeOnlyConfigurationName).extendsFrom(getByName("testRuntimeOnly"))

    val integrationTest = sourceSets["integrationTest"]
    getByName(integrationTest.implementationConfigurationName).extendsFrom(getByName("testImplementation"))
    getByName(integrationTest.runtimeOnlyConfigurationName).extendsFrom(getByName("testRuntimeOnly"))
}

idea {
    module {
        testSources.from(sourceSets["unitTest"].java.srcDirs)
        testSources.from(sourceSets["integrationTest"].java.srcDirs)
        testResources.from(sourceSets["unitTest"].resources.srcDirs)
        testResources.from(sourceSets["integrationTest"].resources.srcDirs)
    }
}

repositories { mavenCentral() }

dependencies {
    val openApiVersion = "2.8.9"
    val mapstructVersion = "1.6.3"
    val mapstructLombokBindingVersion = "0.2.0"
    val jwtVersion = "0.12.7"
    val springCrytoVersion = "6.5.3"
//    val spatialVersion = "6.6.18.Final"

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // mail sending support
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")

    // Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    // Jwt
    implementation("io.jsonwebtoken:jjwt-api:${jwtVersion}")
    implementation("io.jsonwebtoken:jjwt-impl:${jwtVersion}")
    implementation("io.jsonwebtoken:jjwt-jackson:${jwtVersion}")
    // Oauth2
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("com.google.api-client:google-api-client:2.8.1")
    // password encoder
    implementation("org.springframework.security:spring-security-crypto:${springCrytoVersion}")
    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")
    //  Access Microsoft Format Files
    implementation("org.apache.poi:poi-ooxml:5.4.1")
    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    // Mapbox geoJson
    implementation("com.mapbox.mapboxsdk:mapbox-sdk-geojson:5.8.0")
    // https://mvnrepository.com/artifact/com.google.http-client/google-http-client-jackson2
    implementation("com.google.http-client:google-http-client-jackson2:2.0.0")
    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")
    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // jackson datatypes for JSR-310 (Java 8 Date/Time API)
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.19.2")
    // Lombok + Annotation Processors
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:$mapstructLombokBindingVersion")
    // Thư viện test chung
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
    // Thư viện chỉ cho integration test
    integrationTestImplementation("org.testcontainers:junit-jupiter")
    integrationTestImplementation("org.testcontainers:postgresql")
    integrationTestImplementation("org.springframework.boot:spring-boot-testcontainers")
    integrationTestImplementation("com.redis:testcontainers-redis")
}

dependencyManagement {
    val springModulithVersion = "1.4.1"
    imports { mavenBom("org.springframework.modulith:spring-modulith-bom:$springModulithVersion") }
}

// ================== COMMON CONFIG ==================
val excludedClassesForCoverage = listOf(
    "**/shared/**",
    "**/storage/**",
    "**/config/**",
    "**/dto/**",
    "**/entity/**",
    "**/exception/**",
    "**/mapper/**",
    "**/repository/**",
    "**/*Application*",
    "**/*Module*"
)

fun JacocoReport.applyCommonExcludes() {
    classDirectories.setFrom(
        sourceSets["main"].output.classesDirs.map { fileTree(it).exclude(excludedClassesForCoverage) }
    )
}

fun JacocoCoverageVerification.applyCommonExcludes() {
    classDirectories.setFrom(
        sourceSets["main"].output.classesDirs.map { fileTree(it).exclude(excludedClassesForCoverage) }
    )
}

// ================== TASKS ==================
tasks {
    test { enabled = false } // disable default test

    val unitTest by registering(Test::class) {
        description = "Run unit tests"
        group = "verification"
        testClassesDirs = sourceSets["unitTest"].output.classesDirs
        classpath = sourceSets["unitTest"].runtimeClasspath
        ignoreFailures = true
        useJUnitPlatform()
        extensions.configure(JacocoTaskExtension::class) {
            isEnabled = true
            setDestinationFile(layout.buildDirectory.file("jacoco/unitTest.exec").get().asFile)
            excludes = excludedClassesForCoverage
            isIncludeNoLocationClasses = false
            isDumpOnExit = true
        }
    }

    val integrationTest by registering(Test::class) {
        description = "Run integration tests"
        group = "verification"
        testClassesDirs = sourceSets["integrationTest"].output.classesDirs
        classpath = sourceSets["integrationTest"].runtimeClasspath
        ignoreFailures = true
        useJUnitPlatform()
        shouldRunAfter(unitTest)
        extensions.configure(JacocoTaskExtension::class) {
            isEnabled = true
            setDestinationFile(layout.buildDirectory.file("jacoco/integrationTest.exec").get().asFile)
            excludes = excludedClassesForCoverage
            isIncludeNoLocationClasses = false
            isDumpOnExit = true
        }
    }

    val jacocoUnitTestReport by registering(JacocoReport::class) {
        dependsOn(unitTest)
        executionData(unitTest.get())
        sourceSets(sourceSets["main"])
        applyCommonExcludes()
        reports {
            xml.required = true
            html.required = true
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/unitTest"))
        }
    }

    val jacocoIntegrationTestReport by registering(JacocoReport::class) {
        dependsOn(integrationTest)
        executionData(integrationTest.get())
        sourceSets(sourceSets["main"])
        applyCommonExcludes()
        reports {
            xml.required = true
            html.required = true
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/integrationTest"))
        }
    }

    val jacocoCombinedTestReport by registering(JacocoReport::class) {
        dependsOn(unitTest, integrationTest)
        executionData.from(
            fileTree(layout.buildDirectory.dir("jacoco")).include(
                "unitTest.exec",
                "integrationTest.exec"
            )
        )
        sourceSets(sourceSets["main"])
        applyCommonExcludes()
        reports {
            xml.required = true
            html.required = true
            html.outputLocation.set(layout.buildDirectory.dir("reports/jacoco/combined"))
        }
    }

    val jacocoUnitTestCoverageVerification by registering(JacocoCoverageVerification::class) {
        dependsOn(unitTest)
        executionData(unitTest.get())
        sourceSets(sourceSets["main"])
        applyCommonExcludes()
        violationRules {
            rule {
                limit { counter = "INSTRUCTION"; value = "COVEREDRATIO"; minimum = "0.85".toBigDecimal() }
                limit { counter = "LINE"; value = "COVEREDRATIO"; minimum = "0.85".toBigDecimal() }
                limit { counter = "BRANCH"; value = "COVEREDRATIO"; minimum = "0.8".toBigDecimal() }
                isFailOnViolation = true
            }
        }
    }

    val jacocoIntegrationTestCoverageVerification by registering(JacocoCoverageVerification::class) {
        dependsOn(integrationTest)
        executionData(integrationTest.get())
        sourceSets(sourceSets["main"])
        applyCommonExcludes()
        violationRules {
            rule {
                limit { counter = "INSTRUCTION"; value = "COVEREDRATIO"; minimum = "0.7".toBigDecimal() }
                limit { counter = "LINE"; value = "COVEREDRATIO"; minimum = "0.75".toBigDecimal() }
                limit { counter = "BRANCH"; value = "COVEREDRATIO"; minimum = "0.6".toBigDecimal() }
                isFailOnViolation = true
            }
        }
    }

    unitTest { finalizedBy(jacocoUnitTestCoverageVerification) }
    integrationTest { finalizedBy(jacocoIntegrationTestCoverageVerification) }
    check {
        dependsOn(unitTest, integrationTest)
        finalizedBy(jacocoCombinedTestReport)
    }
    build { dependsOn(check) }
}

tasks.named<ProcessResources>("processIntegrationTestResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.named<ProcessResources>("processUnitTestResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.withType<Test> {
    jvmArgs(
        "-XX:+EnableDynamicAgentLoading",
        "-Xshare:off",
        "-Djdk.attach.allowAttachSelf=true",
        "-javaagent:" + configurations.testRuntimeClasspath.get()
            .find { it.name.contains("mockito-core") }!!.absolutePath
    )
}

