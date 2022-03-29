package com.example.biometricbyfingerprintdemo

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.biometricbyfingerprintdemo.biometric.BiometricSettingInfo
import com.example.biometricbyfingerprintdemo.biometric.BiometricStatusResp
import com.example.biometricbyfingerprintdemo.biometric.BiometricTools
import com.example.biometricbyfingerprintdemo.cryptography.CryptographicTools

class MainViewModel: ViewModel() {

    companion object {
        private val TAG = "MainViewModel"
    }

    private val biometricTools = BiometricTools(getBiometricSettingInfo())

    private val cryptographicTools = CryptographicTools()

    fun encryptMessage(msg: String): String {
        return cryptographicTools.encryptMessage(msg)
    }

    fun decryptMessage(decryptMsg: String): String {
        return cryptographicTools.decryptMessage(decryptMsg)
    }

    fun startAuth(fragmentActivity: FragmentActivity, authCallback:(String) -> Unit) {
        biometricTools.startAuth(fragmentActivity) { resp ->
            when(resp.result) {
                BiometricTools.RESULT_AUTH_SUCCESS -> {
                    authCallback("驗證成功")
                }
                BiometricTools.RESULT_AUTH_FAULT -> {
                    authCallback("驗證失敗")
                }
                BiometricTools.RESULT_AUTH_CANCEL -> {
                    authCallback("驗證取消")
                }
                BiometricTools.RESULT_AUTH_CLICK_NEGATIVE -> {
                    authCallback("點擊取消鈕")
                }
                else -> {
                    authCallback(resp.message)
                }
            }
        }
    }

    fun checkBiometricState(context: Context, callback:(BiometricStatusResp) -> Unit) {
        biometricTools.checkBiometricStatus(context, callback)
    }

    private fun getBiometricSettingInfo(): BiometricSettingInfo {
        return BiometricSettingInfo(
            "使用生物辨識登入",
            "使用生物辨識登入App取得資訊",
            "取消"
        )
    }

}