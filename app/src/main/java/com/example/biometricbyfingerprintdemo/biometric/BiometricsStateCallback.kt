package com.example.biometricbyfingerprintdemo.biometric

interface BiometricsStateCallback {
    fun onSuccess()
    fun onUnavailable(message: String)
    fun onNoneEnrolled()
    fun onUnknownState()
}