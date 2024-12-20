plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.jvaax.filtrageinvites"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.jvaax.filtrageinvites"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    //noinspection GradlePath
    implementation(files("/Users/jvz/Downloads/mariadb-java-client-3.5.1.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.jsch)
    implementation("mysql:mysql-connector-java:8.0.33")
}