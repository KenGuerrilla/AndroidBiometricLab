package com.example.biometricbyfingerprintdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.biometricbyfingerprintdemo.databinding.ActivitySimpleCryptoBinding
import com.example.biometricbyfingerprintdemo.viewmodel.SimpleCryptoViewModel

class SimpleCryptoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySimpleCryptoBinding

    private val viewModel: SimpleCryptoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleCryptoBinding.inflate(layoutInflater)
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
    }

    private fun getInputText(): String {
        return binding.etInput.text.toString()
    }

    private fun getEncryptText(): String {
        return binding.tvEncryptOutput.text.toString()
    }
}