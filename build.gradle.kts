plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.1.4"
  kotlin("plugin.spring") version "1.8.21"
  id("org.springframework.boot") version "3.1.0"
}

apply(plugin = "org.springframework.boot")

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
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
}
kotlin {
  jvmToolchain(19)
}
