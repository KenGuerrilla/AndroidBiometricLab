package com.example.biometricbyfingerprintdemo

import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    companion object {
        private val TAG = "MainViewModel"
    }

    private val biometricTools = BiometricTools()

    fun startAuth(fragmentActivity: FragmentActivity) {
        biometricTools.startAuth(fragmentActivity) { resp ->
            Log.d(TAG, "Auth Resp: $resp")
        }
    }

    fun checkBiometricState(context: Context) {
        biometricTools.checkBiometricStatus(context)
    }

}