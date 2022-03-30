package com.example.biometricbyfingerprintdemo.biometric

data class BiometricAuthResp(
    val resultCode: Int,
    val message: String,
    val output: String = ""
)
