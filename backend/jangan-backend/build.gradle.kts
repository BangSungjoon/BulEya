plugins {
	java
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ssafy"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("io.minio:minio:8.5.17")

	compileOnly("org.projectlombok:lombok")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	implementation("com.google.firebase:firebase-admin:9.4.3")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	//QueryDSL 설정
	val querydslVersion = "5.1.0"
	implementation("com.querydsl:querydsl-jpa:${querydslVersion}:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:${querydslVersion}:jakarta")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	// Springdoc OpenAPI 의존성 추가
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.3")

	//Spring Security
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation ("org.springframework.security:spring-security-crypto:6.4.4")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
