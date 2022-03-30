package com.example.biometricbyfingerprintdemo.biometric

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class BiometricCryptoTools {

    private val keyStoreProvider = "AndroidKeyStore"
    private val bioKeyName = "BioAndroidLabKT"

    private var ivByteArray: ByteArray? = null

    // 產生Key
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            // 產生後的Key會直接存入AndroidKeyStore
            KeyProperties.KEY_ALGORITHM_AES, keyStoreProvider
        )
        keyGenerator.init(getKeyGenParameterSpec())
        keyGenerator.generateKey()
    }

    // Key的相關設定
    private fun getKeyGenParameterSpec(): KeyGenParameterSpec {
        return KeyGenParameterSpec.Builder(
            bioKeyName,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setKeySize(256)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            // TODO 需要研究為何加上setUserAuthenticationRequired(true)在加解密時會產生
            //  IllegalBlockSizeException
//            .setUserAuthenticationRequired(true)
            // Invalidate the keys if the user has registered a new biometric
            // credential, such as a new fingerprint. Can call this method only
            // on Android 7.0 (API level 24) or higher. The variable
            // "invalidatedByBiometricEnrollment" is true by default.
//            .setInvalidatedByBiometricEnrollment(true)

            .build()
    }

    // 檢查該Key是否存在
    private fun isKeyExist(): Boolean {
        return getKeyStoreInst().isKeyEntry(bioKeyName)
    }

    // 取得KeyStore實例
    private fun getKeyStoreInst(): KeyStore {
        return KeyStore.getInstance(keyStoreProvider).apply {
            load(null) // 一定要load
        }
    }

    // 取得SecretKey
    private fun getSecretKey(): SecretKey {
        if (!isKeyExist()) {
            generateKey()
        }
        return getKeyStoreInst().getKey(bioKeyName, null) as SecretKey
    }


    fun getEncryptCipher() = getCipher().apply {
        init(Cipher.ENCRYPT_MODE, getSecretKey())
        ivByteArray = this.iv
    }

    fun getDecryptCipher() = getCipher().apply {
        init(Cipher.DECRYPT_MODE, getSecretKey(), IvParameterSpec(ivByteArray))
    }

    // 取得Cipher實例
    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}