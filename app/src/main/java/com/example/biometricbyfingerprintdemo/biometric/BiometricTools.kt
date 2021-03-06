package com.example.biometricbyfingerprintdemo.biometric

import android.content.Context
import android.util.Base64
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.lang.IllegalArgumentException
import java.util.concurrent.Executor


class BiometricTools(
    private val settingInfo: BiometricSettingInfo,
    private val cryptoTools: BiometricCryptoTools
) {

    companion object {
        private val TAG = "BiometricTools"

        const val RESULT_AUTH_SUCCESS = 0
        const val RESULT_AUTH_FAULT = -1
        const val RESULT_AUTH_CANCEL = BiometricPrompt.ERROR_USER_CANCELED
        const val RESULT_AUTH_CLICK_NEGATIVE = BiometricPrompt.ERROR_NEGATIVE_BUTTON

        const val ENCRYPT_MODE = 1
        const val DECRYPT_MODE = 2
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

    fun startAuthWithCrypto(
        cryptoInfo: CryptoInfo,
        fragmentActivity: FragmentActivity,
        callback:(BiometricAuthResp) -> Unit ) {

        val biometricPrompt = BiometricPrompt(
            fragmentActivity,
            getExecutor(fragmentActivity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    callback(BiometricAuthResp(errorCode, errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    // ??????CryptoObject????????????????????????
                    val output = process(cryptoInfo, result)
                    callback(
                        BiometricAuthResp(
                            RESULT_AUTH_SUCCESS,
                            "Success",
                            output
                        )
                    )
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    callback(BiometricAuthResp(RESULT_AUTH_FAULT, "Auth Fault"))
                }
            }
        )

        // ???????????????Cipher

        val cipher = when(cryptoInfo.cryptoMode) {
            ENCRYPT_MODE -> cryptoTools.getEncryptCipher()
            DECRYPT_MODE -> cryptoTools.getDecryptCipher()
            else -> throw IllegalArgumentException("CryptoInfo????????????")
        }

        // ????????????
        biometricPrompt.authenticate(
            getPromptInfo(),
            BiometricPrompt.CryptoObject(cipher)
        )

    }

    private fun process(cryptoInfo: CryptoInfo, result: BiometricPrompt.AuthenticationResult): String {
        return when(cryptoInfo.cryptoMode) {
            ENCRYPT_MODE -> {
                val msg = cryptoInfo.message
                result.cryptoObject?.cipher?.let {
                    val encryptMsg = it.doFinal(msg.toByteArray(Charsets.UTF_8))
                    String(Base64.encode(encryptMsg, 0)).trim()
                } ?: ""
            }
            DECRYPT_MODE -> {
                val encryptMsg = cryptoInfo.message
                val enMsgByteArray = Base64.decode(encryptMsg, 0)
                result.cryptoObject?.cipher?.let {
                    val decryptMsg = it.doFinal(enMsgByteArray)
                    String(decryptMsg)
                } ?: ""
            }
            else -> {
                "????????????"
            }
        }
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

}