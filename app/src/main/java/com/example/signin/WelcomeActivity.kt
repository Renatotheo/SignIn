package com.example.signin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Recupere a mensagem de saudação do layout
        val welcomeMessage: TextView = findViewById(R.id.textViewWelcomeMessage)
        welcomeMessage.text = "Olá, bem-vindo!"
    }
}
