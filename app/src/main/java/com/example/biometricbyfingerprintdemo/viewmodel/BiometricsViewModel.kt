package com.example.biometricbyfingerprintdemo.viewmodel

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.biometricbyfingerprintdemo.biometric.*

class BiometricsViewModel: ViewModel() {

    private val biometricTools = BiometricTools(
        getBiometricSettingInfo(),
        BiometricCryptoTools()
    )

    fun startEncryptAuth(
        message: String,
        fragmentActivity: FragmentActivity,
        authCallback: (String) -> Unit
    ) {
        val cryptoInfo = CryptoInfo(message, BiometricTools.ENCRYPT_MODE)
        biometricTools.startAuthWithCrypto(cryptoInfo, fragmentActivity) { resp ->
            when(resp.resultCode) {
                BiometricTools.RESULT_AUTH_SUCCESS -> {
                    authCallback(resp.output)
                }
                BiometricTools.RESULT_AUTH_FAULT -> {
                    authCallback(resp.message)
                }
                BiometricTools.RESULT_AUTH_CANCEL -> {
                    authCallback(resp.message)
                }
                BiometricTools.RESULT_AUTH_CLICK_NEGATIVE -> {
                    authCallback(resp.message)
                }
                else -> {
                    authCallback("未知狀態")
                }
            }
        }
    }

    fun startDecryptAuth(
        message: String,
        fragmentActivity: FragmentActivity,
        authCallback: (String) -> Unit
    ) {
        val cryptoInfo = CryptoInfo(message, BiometricTools.DECRYPT_MODE)
        biometricTools.startAuthWithCrypto(cryptoInfo, fragmentActivity) { resp ->
            when(resp.resultCode) {
                BiometricTools.RESULT_AUTH_SUCCESS -> {
                    authCallback(resp.output)
                }
                BiometricTools.RESULT_AUTH_FAULT -> {
                    authCallback(resp.message)
                }
                BiometricTools.RESULT_AUTH_CANCEL -> {
                    authCallback(resp.message)
                }
                BiometricTools.RESULT_AUTH_CLICK_NEGATIVE -> {
                    authCallback(resp.message)
                }
                else -> {
                    authCallback("未知狀態")
                }
            }
        }
    }

    fun checkBiometricState(context: Context, callback: BiometricsStateCallback) {
        val biometricManager = BiometricManager.from(context)
        when(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                callback.onSuccess()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                callback.onUnavailable("裝置不支援生物辨識")
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                callback.onUnavailable("裝置目前不支援生物辨識")
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                callback.onNoneEnrolled()
            }
            else ->
                callback.onUnknownState()
        }
    }

    private fun getBiometricSettingInfo(): BiometricSettingInfo {
        return BiometricSettingInfo(
            "使用生物辨識登入",
            "使用生物辨識登入App取得資訊",
            "取消"
        )
    }

}