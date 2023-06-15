plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.1.4"
  kotlin("plugin.spring") version "1.8.21"
  id("org.springframework.boot") version "3.1.0"
  kotlin("plugin.jpa") version "1.7.21"
}

apply(plugin = "org.springframework.boot")
apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("com.amazon.redshift:redshift-jdbc4-no-awssdk:1.2.45.1069")
  testImplementation("com.h2database:h2:2.1.212")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "19"
    }
  }
}
repositories {
  mavenCentral()
  maven("https://s3.amazonaws.com/redshift-maven-repository/release")
}
kotlin {
  jvmToolchain(19)
}
