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

/**
 *  簡易的AES字串加密工具 - AES/CBC/PKCS7Padding
 *
 *  參考文件：
 *      https://developer.android.com/guide/topics/security/cryptography#read-file
 */

class CryptographicTools {

    companion object {
        private val TAG = "CryptographicTools"
    }

    private val keyStoreProvider = "AndroidKeyStore"
    private val bioKeyName = "BioAndroidLabKT"

    // 每次Cipher加密後產生的iv需要存下來供後續解密使用
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

    // 加密訊息
    fun encryptMessage(msg: String): String {
        val cipher = getCipher()
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptMessage = cipher.doFinal(msg.toByteArray(Charsets.UTF_8))
        ivByteArray = cipher.iv
        // 編碼為Base64後回傳字串，後面使用trim移除多餘的空格
        return String(Base64.encode(encryptMessage, 0)).trim()
    }

    // 解密訊息
    fun decryptMessage(encryptMessage: String): String {
        // Base64解碼
        val msg = Base64.decode(encryptMessage, 0)
        val result = ivByteArray?.let {
            val cipher = getCipher()
            val secretKey = getSecretKey()
            // 記得把存下的來iv丟進去
            cipher.init(Cipher.DECRYPT_MODE, secretKey, IvParameterSpec(it))
            val messageByteArray = cipher.doFinal(msg)
            String(messageByteArray)
        } ?: ""
        ivByteArray = null
        return result
    }

    // 產生Key
    private fun generateKey() {
        if (isKeyExist()) return // 如果存在則返回
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
        return getKeyStoreInst().getKey(bioKeyName, null) as SecretKey
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