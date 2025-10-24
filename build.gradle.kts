import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25" // JPA 플러그인 추가
    id("org.springframework.boot") version "3.5.6" // Spring Boot 버전을 최신 안정화 버전으로 가정
    id("io.spring.dependency-management") version "1.1.7"
}

group = "event"
version = "0.0.1-SNAPSHOT"
description = "oms"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2025.0.0")
    }
}

dependencies {
    // 웹, JPA, Redis 관련 의존성 추가
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    // MySQL 드라이버
    runtimeOnly("com.mysql:mysql-connector-j")
    // Swagger (Springdoc OpenAPI) 의존성 추가
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.13")
    // Feign Client 의존성 추가
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// JPA Entity 클래스의 프록시 생성을 위해 all-open 플러그인 설정
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

// JPA Entity는 기본 생성자가 필요하므로 no-arg 플러그인 설정
noArg {
    annotation("jakarta.persistence.Entity")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}