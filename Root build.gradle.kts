plugins {
id("com.android.application")
id("org.jetbrains.kotlin.android")
}

android {
namespace = "com.mahesh.gdmi"
compileSdk = 35

defaultConfig {  
    applicationId = "com.mahesh.gdmi"  
    minSdk = 24  
    targetSdk = 35  
    versionCode = 1  
    versionName = "1.0"  
}  

buildFeatures {  
    compose = true  
}  

composeOptions {  
    kotlinCompilerExtensionVersion = "1.5.15"  
}

}

dependencies {

implementation("androidx.core:core-ktx:1.15.0")  
implementation("androidx.activity:activity-compose:1.10.1")  

implementation("androidx.compose.ui:ui:1.7.8")  
implementation("androidx.compose.ui:ui-tooling-preview:1.7.8")  

implementation("androidx.compose.material3:material3:1.3.1")  

implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")  

debugImplementation("androidx.compose.ui:ui-tooling:1.7.8")

}
