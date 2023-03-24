plugins {
	`java-library`
	groovy
	`maven-publish`
	id("org.ajoberstar.reckon") version "0.+"
}

tasks.wrapper {
	distributionType = Wrapper.DistributionType.ALL
}

reckon {
	stages("rc", "final")
	setScopeCalc(calcScopeFromProp())
	setStageCalc(calcStageFromProp())
}
tasks.reckonTagCreate {
	dependsOn(tasks.check)
}

repositories {
	mavenCentral()
}
dependencies {
	dependencyLocking {
		lockAllConfigurations()
	}

	testImplementation(libs.bundles.test)
}

tasks.test {
	useJUnitPlatform()
}

java {
	withSourcesJar()
	withJavadocJar()
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
