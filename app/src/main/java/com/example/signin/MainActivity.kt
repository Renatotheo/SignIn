package com.example.signin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import android.widget.Toast
import android.content.Intent



class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Inicialize o FirebaseApp, se ainda não estiver inicializado
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
            Log.d("SignUpActivity", "Firebase inicializado com sucesso")
        }else{
            Log.d("SignUpActivity", "Firebase já estava inicializado")
        }

        auth = FirebaseAuth.getInstance()

        val btnSignUp: Button = findViewById(R.id.btnSignUp)
        btnSignUp.setOnClickListener {
            signUp()
        }
    }

    private fun signUp() {
        // Obtenha as referências dos campos após o clique no botão
        val editTextName: EditText = findViewById(R.id.editTextName)
        val editTextLastName: EditText = findViewById(R.id.editTextLastName)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextConfirmPassword: EditText = findViewById(R.id.editTextConfirmPassword)

        val name = editTextName.text.toString()
        val lastName = editTextLastName.text.toString()
        val email = editTextEmail.text.toString()
        val password = editTextPassword.text.toString()
        val confirmPassword = editTextConfirmPassword.text.toString()

        if (password != confirmPassword) {
            // Senhas não coincidem
            Log.e("SignUpActivity", "Senhas não coincidem")
            return
        }

        Log.d("SignUpActivity", "Iniciando criação de usuário com e-mail: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Cadastro bem-sucedido
                    // Limpeza dos campos
                    editTextName.text.clear()
                    editTextLastName.text.clear()
                    editTextEmail.text.clear()
                    editTextPassword.text.clear()
                    editTextConfirmPassword.text.clear()

                    // Mensagem de sucesso
                    showToast("Cadastro bem-sucedido")

                    Log.d("SignUpActivity", "Cadastro bem-sucedido")

                    // Iniciar a WelcomeActivity após o cadastro bem-sucedido
                    val intent = Intent(this, WelcomeActivity::class.java)
                    startActivity(intent)

                } else {
                    try {
                        throw task.exception!!
                    } catch (e: FirebaseAuthUserCollisionException) {
                        // E-mail já cadastrado
                        Log.e("SignUpActivity", "E-mail já cadastrado", e)
                    } catch (e: FirebaseAuthInvalidCredentialsException) {
                        // E-mail inválido
                        Log.e("SignUpActivity", "E-mail inválido", e)
                    } catch (e: Exception) {
                        // Outros erros
                        Log.e("SignUpActivity", "Erro durante o cadastro", e)
                    }
                }
            }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
