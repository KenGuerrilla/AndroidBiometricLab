package com.example.biometricbyfingerprintdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
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
        binding.btLogin.setOnClickListener {
            viewModel.startAuth(this)
        }

        binding.btLogout.setOnClickListener {

        }

        binding.tvStatus.setOnClickListener {
            viewModel.checkBiometricState(this)
        }
    }
}