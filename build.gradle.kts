tasks.wrapper {
	distributionType = Wrapper.DistributionType.ALL
}

plugins {
	`java-library`
	groovy
	`maven-publish`
	id("org.ajoberstar.reckon") version "0.+"
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

java {
	withSourcesJar()
	withJavadocJar()
}

tasks.test {
	useJUnitPlatform()
}

reckon {
	scopeFromProp()
	snapshotFromProp()
}
tasks.reckonTagCreate {
	dependsOn(tasks.check)
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
