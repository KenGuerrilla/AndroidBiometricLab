package com.example.biometricbyfingerprintdemo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.biometricbyfingerprintdemo.cryptography.SimpleCryptoTools

class SimpleCryptoViewModel: ViewModel() {


    private val cryptographicTools = SimpleCryptoTools()

    fun encryptMessage(msg: String): String {
        return cryptographicTools.encryptMessage(msg)
    }

    fun decryptMessage(decryptMsg: String): String {
        return cryptographicTools.decryptMessage(decryptMsg)
    }

}