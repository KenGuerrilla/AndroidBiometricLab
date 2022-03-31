package com.example.biometricbyfingerprintdemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.biometricbyfingerprintdemo.databinding.ActivityRegisterResultBinding

/**
 *  由於原先 startActivityForResult() 已經被標註 Deprecated
 *  因此測試新的 registerForActivityResult() 方法
 *
 *  參考資料 -
 *      https://developer.android.com/training/basics/intents/result
 */

class RegisterResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.btSendResult.setOnClickListener {
            // 回傳Result照舊
            val intent = Intent().apply {
                putExtra("Test", "Test")
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }

}