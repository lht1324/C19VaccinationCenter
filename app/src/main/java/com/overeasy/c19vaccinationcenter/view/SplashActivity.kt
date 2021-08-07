package com.overeasy.c19vaccinationcenter.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.overeasy.c19vaccinationcenter.R
import com.overeasy.c19vaccinationcenter.databinding.ActivitySplashBinding
import com.overeasy.c19vaccinationcenter.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private val viewModel by lazy {
        ViewModelProvider(this, SplashViewModel.Factory(application)).get(SplashViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.apply {
            getDownloadFinished().observe(this@SplashActivity, {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            })
            downloadDatas()
        }
    }

    private fun println(data: String) = Log.d("SplashActivity", data)
}