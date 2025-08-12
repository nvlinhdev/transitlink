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
    val firebaseAdminVersion = "9.5.0"
    val mapstructLombokBindingVersion = "0.2.0"

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // Modulith
    implementation("org.springframework.modulith:spring-modulith-starter-core")
    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    // OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiVersion")
    // Firebase
    implementation("com.google.firebase:firebase-admin:$firebaseAdminVersion")
    // MapStruct
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")
    // Devtools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
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

