// LoginActivity.kt
package com.example.signin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var textViewErrorMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        textViewErrorMessage = findViewById(R.id.textViewErrorMessage)

        // Verifica se já existe um usuário autenticado
        val currentUser = auth.currentUser
        val sharedPreferences = getSharedPreferences("MY_APP_PREFERENCES", Context.MODE_PRIVATE)
        val isUserLogged = sharedPreferences.getBoolean("IS_USER_LOGGED_IN", false)


        if (currentUser != null) {
            // Se existe, direcione para a WelcomeActivity
            goToWelcomeActivity() // Finaliza a LoginActivity para que o usuário não possa voltar pressionando o botão "Back"
        }
        /*if (currentUser != null && isUserLogged) {
            // Se já estiver autenticado e a flag indica que o usuário está logado, vá para a tela de boas-vindas
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Finalize a LoginActivity para que não possa ser acessada pressionando Voltar
        }*/

        val btnLogin: Button = findViewById(R.id.btnLogin)
        val btnSignUp: Button = findViewById(R.id.btnSignUp)

        // Adicionando o toggle de visibilidade de senha
        val imageViewPasswordVisibility: ImageView = findViewById(R.id.imageViewPasswordVisibility)
        val editTextPassword: EditText = findViewById(R.id.editTextLoginPassword)

        imageViewPasswordVisibility.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Mostra a senha enquanto o botão está pressionado
                    togglePasswordVisibility(editTextPassword, imageViewPasswordVisibility)
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // Volta à forma de senha padrão quando o botão é solto ou cancelado
                    togglePasswordVisibility(editTextPassword, imageViewPasswordVisibility)
                }
            }
            true
        }

        btnLogin.setOnClickListener {
            Log.d("LoginActivity", "Botão de login clicado")
            login()
        }

        btnSignUp.setOnClickListener {
            // Adicionar código para abrir a tela de cadastro
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
    private fun goToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        startActivity(intent)
        finish() // Finaliza a LoginActivity para que o usuário não possa voltar pressionando o botão "Back"
    }

    private fun login() {
        val editTextEmail: EditText = findViewById(R.id.editTextLoginEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextLoginPassword)

        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            // Se o e-mail ou a senha estiverem vazios, exiba uma mensagem de erro
            showError("Por favor, preencha todos os campos.")
            return
        }

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


    // Método para lidar com o clique no link "Esqueceu sua senha?"
    fun forgotPassword(view: View) {
        val emailEditText: EditText = findViewById(R.id.editTextLoginEmail)
        val email = emailEditText.text.toString()

        if (email.isNotEmpty()) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Email de redefinição de senha enviado com sucesso
                        Toast.makeText(this, "Um e-mail de redefinição de senha foi enviado para $email", Toast.LENGTH_LONG).show()
                    } else {
                        // Falha ao enviar o e-mail de redefinição de senha
                        showError("Falha ao enviar o e-mail de redefinição de senha. Verifique o endereço de e-mail.")
                    }
                }
        } else {
            // Campo de e-mail vazio
            showError("Por favor, insira seu endereço de e-mail.")
        }
    }

    private fun togglePasswordVisibility(editTextPassword: EditText, imageViewPasswordVisibility: ImageView) {
        // Adicione aqui o código para alternar entre visibilidade e invisibilidade de senha
        if (editTextPassword.transformationMethod == PasswordTransformationMethod.getInstance()) {
            // Se a senha estiver oculta, mostra a senha
            editTextPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            imageViewPasswordVisibility.setImageResource(R.drawable.ic_visibility_on)
        } else {
            // Se a senha estiver visível, oculta a senha
            editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            imageViewPasswordVisibility.setImageResource(R.drawable.ic_visibility_off)
        }

        // Move o cursor para o final do texto
        editTextPassword.setSelection(editTextPassword.text.length)
    }


    private fun showError(message: String) {
        Log.d("LoginActivity", "Mostrando erro: $message")

        textViewErrorMessage.text = message
        Log.d("LoginActivity", "Texto da mensagem de erro definido: ${textViewErrorMessage.text}")

        textViewErrorMessage.visibility = View.VISIBLE
        Log.d("LoginActivity", "Visibilidade da mensagem de erro: ${textViewErrorMessage.visibility}")
    }
}
