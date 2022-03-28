package com.example.biometricbyfingerprintdemo.biometric

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor


class BiometricTools(
    private val settingInfo: BiometricSettingInfo
) {

    companion object {
        private val TAG = "BiometricTools"

        const val RESULT_AUTH_SUCCESS = 0
        const val RESULT_AUTH_FAULT = -1
        const val RESULT_AUTH_CANCEL = BiometricPrompt.ERROR_USER_CANCELED
        const val RESULT_AUTH_CLICK_NEGATIVE = BiometricPrompt.ERROR_NEGATIVE_BUTTON
    }

    fun startAuth(fragmentActivity: FragmentActivity, callback:(BiometricAuthResp) -> Unit) {
        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            getExecutor(fragmentActivity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
//                    Log.d(TAG, "onAuthenticationError: $errorCode / $errString")
                    callback(BiometricAuthResp(errorCode, errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    Log.d(TAG, "onAuthenticationSucceeded")
                    val resp = BiometricAuthResp(RESULT_AUTH_SUCCESS, "Success")
                    callback(resp)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
//                    Log.d(TAG, "onAuthenticationFailed")
                    val resp = BiometricAuthResp(RESULT_AUTH_FAULT, "Failed")
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
            .setTitle(settingInfo.title)
            .setSubtitle(settingInfo.subTitle)
            .setNegativeButtonText(settingInfo.negativeButtonText)
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .build()
    }

    // 檢查目前生物辨識狀態
    fun checkBiometricStatus(context: Context, callback:(BiometricStatusResp) -> Unit) {
        val biometricManager = BiometricManager.from(context)
        when(biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                callback(BiometricStatusResp("裝置可執行生物辨識", false))
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                callback(BiometricStatusResp("裝置不支援生物辨識", false))
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                callback(BiometricStatusResp("裝置目前不支援生物辨識", false))
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                callback(BiometricStatusResp("裝置尚未設定生物辨識功能", true))
            }
            else ->
                callback(BiometricStatusResp("未定義錯誤", false))
        }
    }

}