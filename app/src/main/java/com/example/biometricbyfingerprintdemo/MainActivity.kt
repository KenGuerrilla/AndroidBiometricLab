package com.example.biometricbyfingerprintdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.biometricbyfingerprintdemo.databinding.ActivityMainBinding
import com.example.biometricbyfingerprintdemo.viewmodel.MainViewModel

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

        binding.btGotoSimpleCryptoActivity.setOnClickListener {
            val intent = Intent(this, SimpleCryptoActivity::class.java)
            startActivity(intent)
        }

        binding.btGotoBiometricsActivity.setOnClickListener {
            val intent = Intent(this, BiometricsActivity::class.java)
            startActivity(intent)
        }

        binding.btGotoRegisterActivity.setOnClickListener {
            // 可使用Settings丟入Intent導入指定的設定頁面
            val intent = Intent(this@MainActivity, RegisterResultActivity::class.java)
            getContent.launch(intent)
        }

    }

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        result?.let {
            if (it.resultCode == RESULT_OK) {
                Log.d("TAG", "${it.data?.getStringExtra("Test")}")
            }
        }
    }

}