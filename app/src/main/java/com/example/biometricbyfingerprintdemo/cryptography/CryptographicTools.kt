package com.example.biometricbyfingerprintdemo.cryptography

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class CryptographicTools {

    companion object {
        private val TAG = "CryptographicTools"
    }

    private val keyStoreProvider = "AndroidKeyStore"
    private val bioKeyName = "BioAndroidLabKT"

    private val keyStore = KeyStore.getInstance(keyStoreProvider).apply {
        load(null)
    }

    private var ivByteArray: ByteArray? = null

    init {
        generateKey()
    }

    // 列出目前的KeyAlias
    private fun printKeyAliases() {
        val list = getKeyStoreInst().aliases()
        for (alias in list) {
            Log.d(TAG, "printKeyAliases: $alias")
        }
    }

    private fun isKeyExist(): Boolean {
        return getKeyStoreInst().isKeyEntry(bioKeyName)
    }

    private fun getKeyStoreInst(): KeyStore {
        return KeyStore.getInstance(keyStoreProvider).apply {
            load(null)
        }
    }

    fun encryptMessage(msg: String): String {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptMessage = cipher.doFinal(msg.toByteArray(Charsets.UTF_8))
        ivByteArray = cipher.iv
//        return String(encryptMessage)
        return String(Base64.encode(encryptMessage, 0))
    }

    fun decryptMessage(encryptMessage: String): String {
        val msg = Base64.decode(encryptMessage, 0)
        val result = ivByteArray?.let {
            val cipher = getCipher()
            val secretKey = getSecretKey()
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(it))
//            val messageByteArray = cipher.doFinal(encryptMessage.toByteArray(Charsets.UTF_8))
            val messageByteArray = cipher.doFinal(msg)
            String(messageByteArray)
        } ?: ""
        ivByteArray = null
        return result
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, keyStoreProvider
        )
        keyGenerator.init(getKeyGenParameterSpec())
        keyGenerator.generateKey()
    }

    private fun getKeyGenParameterSpec(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            bioKeyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//            .setUserAuthenticationRequired(true)
            // Invalidate the keys if the user has registered a new biometric
            // credential, such as a new fingerprint. Can call this method only
            // on Android 7.0 (API level 24) or higher. The variable
//            .setInvalidatedByBiometricEnrollment(true)
            .build()
    }

    private fun getSecretKey(): SecretKey {
        return keyStore.getKey(bioKeyName, null) as SecretKey
//        return getKeyStoreInst().getKey(bioKeyName, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}