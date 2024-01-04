package com.example.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class WelcomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private var isUserLoggedIn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()

        // Recupere a mensagem de saudação do layout
        val welcomeMessage: TextView = findViewById(R.id.textViewWelcomeMessage)
        val btnLogout: Button = findViewById(R.id.btnLogout)



        welcomeMessage.text = "Olá, bem-vindo!"

        btnLogout.setOnClickListener {
            logout()
        }
    }
    private fun logout() {
        auth.signOut()

        // Limpar a persistência local (exemplo usando SharedPreferences)
        val sharedPreferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.putBoolean("IS_USER_LOGGED_IN", false)  // Defina a flag como false
        editor.apply()

        // Vá para a tela de login e limpe a pilha de atividades anteriores
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

}
