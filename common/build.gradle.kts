/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    java
    id("io.spring.dependency-management") version "1.1.0"
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

dependencies {
    implementation(project(":protogen"))
    implementation("com.google.protobuf:protobuf-java:3.24.4")
    implementation("com.google.protobuf:protobuf-java-util:3.24.4")
    implementation("io.opentelemetry:opentelemetry-proto:1.7.1-alpha")
    implementation("org.apache.commons:commons-compress:1.26.1")
    implementation("org.ehcache:ehcache:3.10.8")
    implementation("com.github.luben:zstd-jni:1.5.6-3")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.hibernate:hibernate-core:5.4.20.Final")
    implementation("com.clickhouse:clickhouse-http-client:0.4.0")
    implementation("org.hibernate:hibernate-entitymanager:5.4.20.Final")
    implementation("com.h2database:h2:1.4.197")
    implementation("net.jpountz.lz4:lz4:1.3.0")
    implementation("org.springframework.data:spring-data-jpa:3.2.5")
    implementation("org.hibernate:hibernate-jpamodelgen:5.4.20.Final")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.70")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}
