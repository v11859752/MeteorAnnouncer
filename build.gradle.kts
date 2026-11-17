plugins {
    java
}

group = "ru.example"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.plasmoverse.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("su.plo.voice.api:server:2.1.17")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}