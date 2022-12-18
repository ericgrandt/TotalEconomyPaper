plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "1.0.6"
    checkstyle
}

group = "com.ericgrandt"
version = "0.3.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
    testImplementation("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")

    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    testImplementation("com.github.MilkBowl:VaultAPI:1.7")

    implementation("org.mybatis:mybatis:3.5.11")
    testImplementation("org.mybatis:mybatis:3.5.11")

    testImplementation("com.h2database:h2:2.1.214")
    testImplementation("com.zaxxer:HikariCP:5.0.1")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")

    testImplementation("org.mockito:mockito-core:4.8.0")
    testImplementation("org.mockito:mockito-junit-jupiter:4.8.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
}