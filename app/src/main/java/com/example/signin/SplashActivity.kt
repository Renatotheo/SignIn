// SplashActivity.kt
package com.example.signin

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val SPLASH_DELAY: Long = 2000 // 2 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // Este código será executado após o atraso
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, SPLASH_DELAY)
    }
}
