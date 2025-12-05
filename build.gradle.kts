plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "8.0.0"
  kotlin("jvm") version "2.1.10"
  kotlin("plugin.spring") version "2.1.20"
  kotlin("plugin.jpa") version "2.1.10"
  id("jacoco")
  id("org.barfuin.gradle.jacocolog") version "3.1.0"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

val testContainersVersion = "1.20.6"

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("com.amazon.redshift:redshift-jdbc4-no-awssdk:1.2.45.1069")
  implementation("uk.gov.justice.service.hmpps:hmpps-digital-prison-reporting-lib:9.8.3")

  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:1.4.0")

  implementation("io.sentry:sentry-spring-boot-starter-jakarta:7.11.0")

  // Swagger
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

  // Testing
  testImplementation("org.flywaydb:flyway-core")
  testImplementation("com.h2database:h2")
  testImplementation("io.jsonwebtoken:jjwt:0.12.6")
  testImplementation("javax.xml.bind:jaxb-api:2.4.0-b180830.0359")
  testImplementation("com.marcinziolo:kotlin-wiremock:2.1.1")
  testImplementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter-test:1.4.2")
  testImplementation("org.postgresql:postgresql:42.7.5")
  testImplementation("org.testcontainers:postgresql:$testContainersVersion")
  testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
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
