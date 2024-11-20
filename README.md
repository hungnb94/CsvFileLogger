# Write Log to File for Android
[![](https://jitpack.io/v/hungnb94/csv-file-loging.svg)](https://jitpack.io/#hungnb94/csv-file-loging)

This project demonstrates how to write application logs to a file in an Android app.
Logging to a file is useful for debugging, tracking application behavior, or collecting analytics for specific events.


## Installation

**Step 1.** Add the JitPack repository to your build.gradle file

```groovy
allprojects {
	repositories {
		google()
		mavenCentral()
		maven { url = uri('https://jitpack.io') } // Add this line
	}
}
```

**Step 2.** Add the dependency

```groovy
dependencies {
	implementation("com.github.hungnb94:csv-file-loging:1.0.1")
}
```


## Usage

```kotlin
class LogApplication : Application() {
	override fun onCreate() {
		super.onCreate()
		obbDir.mkdirs()
		Timber.plant(CsvFileTree(obbDir))
	}
}
```
