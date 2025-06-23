plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.minimarketapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.minimarketapp"
        minSdk = 21
        targetSdk = 35
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
    // Asegúrate de que la versión de appcompat sea la correcta
    implementation ("androidx.appcompat:appcompat:1.2.0") // Biblioteca para Toolbar

    // Asegúrate de que la versión de material sea la correcta
    implementation ("com.google.android.material:material:1.9.0")
    // Biblioteca para NavigationView y otros componentes de material design

    // Dependencia para el ciclo de vida de actividades
    implementation ("androidx.activity:activity:1.2.0") // Asegúrate de que esta sea la versión correcta

    // Biblioteca para diseños con ConstraintLayout
    implementation ("androidx.constraintlayout:constraintlayout:2.1.0") // Asegúrate de que esta sea la versión correcta

    // Para pruebas unitarias
    testImplementation ("junit:junit:4.13.2") // Asegúrate de que esta versión sea la correcta

    // Para pruebas de UI con JUnit
    androidTestImplementation ("androidx.test.ext:junit:1.1.2") // Asegúrate de que esta sea la versión correcta

    // Para pruebas de UI con Espresso
    androidTestImplementation ("androidx.espresso:espresso-core:3.3.0") // Asegúrate de que esta sea la versión correcta
}

