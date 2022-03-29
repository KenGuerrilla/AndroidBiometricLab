package com.example.biometricbyfingerprintdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import com.example.biometricbyfingerprintdemo.cryptography.CryptographicTools
import com.example.biometricbyfingerprintdemo.databinding.ActivityMainBinding

/**
 *
 *  架構使用MVVM
 *
 *  實驗目標：
 *      1. 點擊Login可以呼叫出生物辨識功能，成功後TextView顯示登入成功
 *      2. 點擊Logout可以重置TextView訊息
 *
 *  額外目標：
 *      1. 使用生物辨識功能解密指定加密資料，並顯示於TextView上
 *
 *  參考文件：
 *      https://developer.android.com/training/sign-in/biometric-auth
 *
 */

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {

        binding.btEncrypt.setOnClickListener {
            val msg = getInputText()
            binding.tvEncryptOutput.text = viewModel.encryptMessage(msg)
        }

        binding.btDecrypt.setOnClickListener {
            val encryptMsg = getEncryptText()
            binding.tvDecryptOutput.text = viewModel.decryptMessage(encryptMsg)
        }

        binding.btLogin.setOnClickListener {
            viewModel.startAuth(this) {
                binding.tvStatus.text = it
            }
        }

        binding.btLogout.setOnClickListener {
            binding.tvStatus.text = "尚未驗證"
        }

        binding.tvStatus.setOnClickListener {
            viewModel.checkBiometricState(this) { resp ->
                binding.tvStatus.text = resp.message

                if (resp.isNoneEnrolled) {
//                    startToSetBiometric()
                }
            }
        }
    }

    private fun getInputText(): String {
        return binding.etInput.text.toString()
    }

    private fun getEncryptText(): String {
        return binding.tvEncryptOutput.text.toString()
    }

    private fun startToSetBiometric() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        }
//        registerForActivityResult()
        // Deprecated
//        startActivityForResult(enrollIntent, 1000)
    }
}