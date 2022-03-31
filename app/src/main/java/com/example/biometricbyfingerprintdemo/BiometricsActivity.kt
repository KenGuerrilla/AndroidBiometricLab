package com.example.biometricbyfingerprintdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.viewModels
import androidx.biometric.BiometricManager
import com.example.biometricbyfingerprintdemo.biometric.BiometricsStateCallback
import com.example.biometricbyfingerprintdemo.databinding.ActivityBiometricsBinding
import com.example.biometricbyfingerprintdemo.viewmodel.BiometricsViewModel

class BiometricsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBiometricsBinding

    private val viewModel: BiometricsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBiometricsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.btEncryptWithBio.setOnClickListener {
            if (checkBiometricsState()) {
                viewModel.startEncryptAuth(getBioInputText(), this) {
                    binding.tvBioEncryptOutput.text = it
                }
            }
        }

        binding.btDecryptWithBio.setOnClickListener {
            if (checkBiometricsState()) {
                viewModel.startDecryptAuth(getBioEncryptText(), this) {
                    binding.tvBioDecryptOutput.text = it
                }
            }
        }
    }

    private fun checkBiometricsState(): Boolean {
        var state = false
        viewModel.checkBiometricState(this, object : BiometricsStateCallback {
            override fun onSuccess() {
                state = true
            }

            override fun onUnavailable(message: String) {
                Toast.makeText(this@BiometricsActivity, message, Toast.LENGTH_SHORT).show()
            }

            override fun onNoneEnrolled() {
//                startToSetBiometric()
            }

            override fun onUnknownState() {
                Toast.makeText(this@BiometricsActivity, "生物辨識裝置狀態錯誤", Toast.LENGTH_SHORT).show()
            }
        })

        return state
    }

    private fun getBioInputText(): String {
        return binding.tvBioEncryptInput.text.toString()
    }

    private fun getBioEncryptText(): String {
        return binding.tvBioEncryptOutput.text.toString()
    }

    private fun startToSetBiometric() {
        val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        }
//        registerForActivityResult()
        // Deprecated
//        startActivityForResult(enrollIntent, 1000)
    }
}