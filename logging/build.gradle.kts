plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.kotlin.android)
	id("maven-publish")
}

android {
	namespace = "com.leoh.logging"
	compileSdk = 34

	defaultConfig {
		minSdk = 24

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		consumerProguardFiles("consumer-rules.pro")
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
}

dependencies {
	implementation(libs.timber)
	api(libs.xlog)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
}

afterEvaluate {
	publishing {
		publications {
			register("release", MavenPublication::class) {
				from(components["release"])
				groupId = providers.gradleProperty("GROUP_ID").get()

				artifactId = providers.gradleProperty("ARTIFACT_ID").get()

				version = providers.gradleProperty("PUBLISH_VERSION").get()
			}
		}
	}
}
