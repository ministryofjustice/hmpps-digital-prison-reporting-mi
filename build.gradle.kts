plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.2.1"
  kotlin("jvm") version "2.3.21"
  kotlin("plugin.spring") version "2.3.21"
  kotlin("plugin.jpa") version "2.3.21"
  id("jacoco")
  id("org.barfuin.gradle.jacocolog") version "4.0.2"
  id("io.sentry.jvm.gradle") version "6.5.0"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

val testContainersVersion = "1.21.4"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("io.micrometer:micrometer-registry-prometheus")
  implementation("com.amazon.redshift:redshift-jdbc4-no-awssdk:1.2.45.1069")
  implementation("uk.gov.justice.service.hmpps:hmpps-digital-prison-reporting-lib:15.2.1")
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.1.0")

  // Swagger
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.3")

  // Testing
  testImplementation("org.flywaydb:flyway-core")
  testImplementation("com.h2database:h2")
  testImplementation("io.jsonwebtoken:jjwt:0.13.0")
  testImplementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
  testImplementation("com.marcinziolo:kotlin-wiremock:2.1.1")
  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:2.1.0")
  testImplementation("org.postgresql:postgresql:42.7.10")
  testImplementation("org.testcontainers:postgresql:$testContainersVersion")
  testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
  testImplementation("org.springframework.boot:spring-boot-test-autoconfigure")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

repositories {
  mavenLocal()
  mavenCentral()
  maven("https://s3.amazonaws.com/redshift-maven-repository/release")
}

kotlin {
  jvmToolchain(21)
}

tasks.test {
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
}
