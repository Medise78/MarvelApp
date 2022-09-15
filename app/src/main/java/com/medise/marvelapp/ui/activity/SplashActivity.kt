package com.medise.marvelapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medise.marvelapp.MainActivity
import com.medise.marvelapp.R
import com.medise.marvelapp.databinding.ActivitySplashActivtyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding:ActivitySplashActivtyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashActivtyBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_splash_activty)
        val view = binding.root
        setContentView(view)
        splash()
        supportActionBar?.hide()
    }

    private fun splash() = with(binding){
        tvSplash.alpha = 0f
        tvSplash.animate().setDuration(1000).alpha(1f).withEndAction {
            val intent = Intent(this@SplashActivity , MainActivity::class.java)
            overridePendingTransition(android.R.anim.fade_in , android.R.anim.fade_out)
            startActivity(intent)
        }
    }
}