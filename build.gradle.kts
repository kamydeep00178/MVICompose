// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.hilt) apply false     //  <-- HILT IS HERE
    //id("com.google.dagger.hilt.android") version "2.54" apply false
    alias(libs.plugins.ksp) apply false      //  <-- KSP MUST BE HERE, TOO
}