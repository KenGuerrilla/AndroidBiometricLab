package com.example.biometricbyfingerprintdemo

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor


class BiometricTools {

    companion object {
        private val TAG = "BiometricTools"
    }

    fun startAuth(fragmentActivity: FragmentActivity, callback:(BiometricResp) -> Unit) {
        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            getExecutor(fragmentActivity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.d(TAG, "onAuthenticationError: $errorCode / $errString")
                    val resp = BiometricResp(-1, errorCode, errString.toString())
                    callback(resp)
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Log.d(TAG, "onAuthenticationSucceeded")
                    val resp = BiometricResp(0, 0, "Success")
                    callback(resp)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.d(TAG, "onAuthenticationFailed")
                    val resp = BiometricResp(1, 0, "Failed")
                    callback(resp)
                }
            })

        biometricPrompt.authenticate(getPromptInfo())
    }

    private fun getExecutor(context: Context): Executor {
        return ContextCompat.getMainExecutor(context)
    }

    private fun getPromptInfo(): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle("使用生物辨識登入")
            .setSubtitle("使用生物辨識登入App取得資訊")
            .setNegativeButtonText("Cancel")
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    // 檢查目前生物辨識狀態
    fun checkBiometricStatus(context: Context) {
        val biometricManager = BiometricManager.from(context)

        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS ->
                Log.d(TAG, "App can authenticate using biometrics.")
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                Log.e(TAG, "No biometric features available on this device.")
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                Log.e(TAG, "Biometric features are currently unavailable.")
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.e(TAG, "請註冊指紋")
            }
            else ->
                Log.e(TAG, "未定義錯誤")
        }
    }

}