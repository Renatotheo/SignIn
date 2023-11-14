// LoginActivity.kt
package com.example.signin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textViewErrorMessage: TextView  // Adicione esta linha

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage)  // Adicione esta linha

        val btnLogin: Button = findViewById(R.id.btnLogin)
        btnLogin.setOnClickListener {
            Log.d("LoginActivity", "Botão de login clicado")
            login()
        }
    }

    private fun login() {
        val editTextEmail: EditText = findViewById(R.id.editTextLoginEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextLoginPassword)

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login bem-sucedido
                    Log.d("LoginActivity", "Login bem-sucedido")
                    val user: FirebaseUser? = auth.currentUser
                    if (user != null) {
                        // Verifique se o usuário já existe no banco de dados
                        // Você pode fazer uma verificação adicional aqui, se necessário

                        // Direcione para a WelcomeActivity
                        val intent = Intent(this, WelcomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        Log.e("LoginActivity", "Usuário nulo após login bem-sucedido")
                    }
                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        if (e.errorCode == "ERROR_INVALID_EMAIL" || e.errorCode == "ERROR_WRONG_PASSWORD") {
                            // Trate aqui como se as credenciais fossem inválidas sem especificar se é e-mail ou senha incorretos
                            showError("Credenciais inválidas")
                        } else if (e.errorCode == "ERROR_USER_NOT_FOUND") {
                            // Trate aqui como se o usuário não estivesse cadastrado
                            showError("Credenciais inválidas")
                        } else {
                            // Trate outros casos de exceção
                            showError("Erro durante o login: ${e.message}")
                        }

                        editTextEmail.text.clear()
                        editTextPassword.text.clear()
                    } catch (e: FirebaseException) {
                        // Trate outras exceções do Firebase
                        Log.e("LoginActivity", "Erro durante o login: ${e.message}", e)

                        if (e.message?.contains("[ INVALID_LOGIN_CREDENTIALS ]") == true) {
                            // Tratar o erro específico [ INVALID_LOGIN_CREDENTIALS ]
                            showError("Credenciais inválidas")
                            editTextEmail.text.clear()
                            editTextPassword.text.clear()
                        } else {
                            // Tratar outros casos de exceção
                            showError("Erro durante o login: ${e.message}")
                        }
                    } catch (e: Exception) {
                        // Trate outros erros
                        Log.e("LoginActivity", "Erro durante o login: ${e.javaClass.simpleName}", e)
                        showError("Erro durante o login: ${e.javaClass.simpleName}")
                    }
                }
            }
    }

    private fun showError(message: String) {
        Log.d("LoginActivity", "Mostrando erro: $message")

        textViewErrorMessage.text = message
        Log.d("LoginActivity", "Texto da mensagem de erro definido: ${textViewErrorMessage.text}")

        textViewErrorMessage.visibility = View.VISIBLE
        Log.d("LoginActivity", "Visibilidade da mensagem de erro: ${textViewErrorMessage.visibility}")
    }
}
