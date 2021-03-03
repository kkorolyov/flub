tasks.wrapper {
	distributionType = Wrapper.DistributionType.ALL
}

plugins {
	`java-library`
	groovy
	`maven-publish`
}

group = "dev.kkorolyov"
description = "Collection of additional generic data structures, procedures, and utility functions"

java {
	sourceCompatibility = JavaVersion.VERSION_14
	targetCompatibility = JavaVersion.VERSION_14

	withSourcesJar()
	withJavadocJar()
}

tasks.test {
	useJUnitPlatform()
}

repositories {
	jcenter()
}
dependencies {
	val spockVersion: String by project
	val byteBuddyVersion: String by project
	testImplementation("org.spockframework:spock-core:$spockVersion")
	testImplementation("net.bytebuddy:byte-buddy:$byteBuddyVersion")

	dependencyLocking {
		lockAllConfigurations()
	}
}

publishing {
	publications {
		create<MavenPublication>("mvn") {
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "GitHubPackages"
			url = uri("https://maven.pkg.github.com/kkorolyov/flopple")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}
