tasks.wrapper {
	distributionType = Wrapper.DistributionType.ALL
}

plugins {
	`java-library`
	groovy
	`maven-publish`
}

repositories {
	mavenCentral()
}
dependencies {
	testImplementation(libs.bundles.test)

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
			url = uri("https://maven.pkg.github.com/kkorolyov/flub")
			credentials {
				username = System.getenv("GITHUB_ACTOR")
				password = System.getenv("GITHUB_TOKEN")
			}
		}
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17

	withSourcesJar()
	withJavadocJar()
}

tasks.test {
	useJUnitPlatform()
}
