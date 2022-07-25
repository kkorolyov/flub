plugins {
	`java-library`
	groovy
	`maven-publish`
	id("org.ajoberstar.reckon") version "0.+"
}

tasks.wrapper {
	distributionType = Wrapper.DistributionType.ALL
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
	stages("rc", "final")
	setScopeCalc(calcScopeFromProp())
	setStageCalc(calcStageFromProp())
}
tasks.reckonTagCreate {
	dependsOn(tasks.check)
}

publishing {
	publications {
		create<MavenPublication>("main") {
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
