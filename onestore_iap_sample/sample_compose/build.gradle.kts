// ONE Store IAP Sample - Compose Module
// Jetpack Compose를 사용한 인앱 결제 샘플 앱

// 필요한 Gradle 플러그인 설정
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22" // JSON 직렬화를 위한 플러그인
}

// 루트 build.gradle의 ext 변수 참조
val onestore_iap_version: String by rootProject.extra
val onestore_licensing_version: String by rootProject.extra

android {
    namespace = "com.onestore.sample.compose"
    compileSdk = 35

    defaultConfig {
        // 실제 앱에 적용할 때는 아래 값들을 반드시 변경해야 합니다
        // - applicationId: 귀사의 고유한 패키지명으로 변경 (예: com.yourcompany.yourapp)
        // - versionCode: 앱 버전 코드 (정수, 업데이트 시 증가)
        // - versionName: 사용자에게 표시되는 버전명 (예: "1.0.0")
        applicationId = "com.onestore.sample.compose"
        versionCode = 1
        versionName = "1.0.0"
        minSdk = 25
        targetSdk = 35
    }

    // Jetpack Compose 활성화
    buildFeatures {
        compose = true
        buildConfig = true
    }

    // Compose Compiler 설정 (Kotlin 1.9.22와 호환)
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    // Java 컴파일 옵션
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin 컴파일 옵션
    kotlinOptions {
        jvmTarget = "17"
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
}

dependencies {
    // Android Core
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.1")

    // Jetpack Compose
    implementation(platform("androidx.compose:compose-bom:2025.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.8.9")
    implementation("androidx.compose.material:material-icons-extended")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Debug Tools
    debugImplementation("androidx.compose.ui:ui-tooling")

    // ONE Store SDK
    implementation("com.onestorecorp.sdk:sdk-iap:$onestore_iap_version") // 인앱 결제 SDK
    implementation("com.onestorecorp.sdk:sdk-licensing:$onestore_licensing_version") // 라이선스 검증 SDK
}
