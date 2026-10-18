plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.athar.athar"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.athar.athar"
        // ⚠️ ٢٦ = أندرويد ٨: يغطي ≈٩٨٪ من الأجهزة، ويسمح بالودجت
        // والإشعارات الحديثة بلا مكتبات توافق.
        minSdk = 26
        targetSdk = 35
        versionCode = 8
        versionName = "0.8.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    // ⚠️ الأيقونات حزمة منفصلة عن material3. و`core` فيها مجموعة
    // صغيرة فقط: Home و Star تعمل، أما MenuBook و Explore و MoreHoriz
    // ففي `extended` وحدها. نستخدم extended الآن ونقلم الحجم لاحقًا.
    implementation("androidx.compose.material:material-icons-extended")

    // ── المواقيت ──────────────────────────────────────────
    // مكتبة Adhan الرسمية (نفس خوارزمية adhan_dart). مكتبة Java نقية
    // بلا اعتماد على أندرويد — لذا تُختبر على JVM بلا محاكي.
    implementation("com.batoulapps.adhan:adhan:1.2.1")

    // ⚠️ اختبارات على JVM: تتحقق من صحة المواقيت بلا جهاز
    testImplementation("junit:junit:4.13.2")
    // org.json مدمج في أندرويد لكنه غير متاح في اختبارات JVM
    // (android.jar يعيد قيمًا افتراضية) — فنضيفه للاختبارات فقط
    testImplementation("org.json:json:20240303")

    // قراءة أصول القرآن بلا تعليق الواجهة
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    debugImplementation("androidx.compose.ui:ui-tooling")
}
