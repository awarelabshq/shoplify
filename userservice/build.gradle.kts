plugins {
    java
    id("org.springframework.boot") version "2.7.4"
    id("io.spring.dependency-management") version "1.1.0"
}

version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":protogen"))
    implementation("ch.qos.logback:logback-classic")
    implementation(platform("com.google.cloud:spring-cloud-gcp-dependencies:3.7.2"))
    implementation("com.google.cloud:spring-cloud-gcp-starter-security-firebase")
    implementation("com.google.cloud:spring-cloud-gcp-starter-secretmanager") {
        exclude("com.google.protobuf:protobuf-java")
        exclude("com.google.protobuf:protobuf-java-util")
    }
    implementation("com.github.wnameless.json:json-flattener:0.16.6")
    implementation("com.google.protobuf:protobuf-java:3.24.4")
    implementation("com.google.protobuf:protobuf-java-util:3.24.4")
    implementation("io.opentelemetry:opentelemetry-api:1.31.0")
    implementation("io.opentelemetry:opentelemetry-exporter-otlp:1.33.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.29.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:1.29.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api-annotation-support:1.15.0-alpha")
    implementation("io.opentelemetry.semconv:opentelemetry-semconv:1.23.1-alpha")
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure:1.33.0");
    implementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure-spi:1.33.0");
    implementation("io.opentelemetry:opentelemetry-proto:1.7.1-alpha")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-core")
    implementation("com.theokanning.openai-gpt3-java:service:0.18.2")
    implementation("org.ehcache:ehcache:3.10.8")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.github.awarelabshq:tracked-tests:0.0.8843")
    implementation("com.github.awarelabshq:guards:0.0.363")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-logging") {
        exclude("org.springframework.boot:spring-boot-starter-logging")
    }
    implementation("com.jayway.jsonpath:json-path:2.9.0")
    implementation("org.apache.ignite:ignite-spring-data-2.2-ext:1.0.0")
    implementation("org.springframework.data:spring-data-commons")
    implementation("org.hibernate:hibernate-core:5.4.20.Final")
    implementation("org.postgresql:postgresql")
    implementation("com.clickhouse:clickhouse-http-client:0.4.0")
    implementation("org.hibernate:hibernate-entitymanager:5.4.20.Final")
    implementation("com.h2database:h2:1.4.197")
    implementation("net.jpountz.lz4:lz4:1.3.0")
    implementation("org.hibernate:hibernate-jpamodelgen:5.4.20.Final")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.3.70")
    implementation("com.google.cloud:google-cloud-storage:2.32.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    compileOnly("org.projectlombok:lombok:1.18.20")
    annotationProcessor("org.projectlombok:lombok:1.18.20")
}


tasks {
    // Configure the processResources task to filter the correct application.properties file
    val processResources by getting(Copy::class) {
        val profile = project.findProperty("profile")?.toString()?.toLowerCase() ?: "local"
        include("**/application-${profile}.properties")
        include("**/*")
        exclude { details ->
            val fileName = details.file.name
            fileName.startsWith("application-") && !fileName.endsWith("-$profile.properties")
        }
        rename { fileName ->
            if (fileName == "application-${profile}.properties") {
                "application.properties"
            } else {
                fileName
            }
        }
        // Handle duplicate entries by overwriting them
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Test> {
        useJUnitPlatform()
    }

    // Make the build task depend on the processResources task
    build {
        dependsOn(processResources)
        // Exclude the original resource files to avoid duplication in the output JAR
    }
}
