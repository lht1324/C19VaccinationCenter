package com.overeasy.c19vaccinationcenter.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.overeasy.c19vaccinationcenter.databinding.ActivitySplashBinding
import com.overeasy.c19vaccinationcenter.viewmodel.SplashViewModel

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel by lazy {
        ViewModelProvider(this, SplashViewModel.Factory(application)).get(SplashViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.apply {
            getDownloadFinished().observe(this@SplashActivity, {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            })
            downloadDatas()
        }
    }
}