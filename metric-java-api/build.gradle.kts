plugins {
    id("java")
    id("java-library")
    id("maven-publish")
}

group = "pl.minecodes"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing {
    repositories {
        maven {
            name = "minecodesRepositorySnapshots"
            url = uri("https://maven.minecodes.pl/snapshots")
            credentials {
                username = project.findProperty("mavenUsername") as String? ?: System.getenv("MAVEN_USERNAME")
                password = project.findProperty("mavenPassword") as String? ?: System.getenv("MAVEN_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "pl.minecodes"
            artifactId = "metric-java-api"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

tasks.jar {
    // To exclude signing related files from other JAR files
    exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")

    // To merge duplicate resources properly
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    // To include all dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

dependencies {
    implementation("org.json:json:20231013")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}