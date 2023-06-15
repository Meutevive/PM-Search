package com.meutevive.pmsearch.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.screens.signup.SignupActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initializeViews()
    }

    private fun initializeViews() {

        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)

        val loginButton: Button = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty()) {
                emailEditText.error = "Veuillez entrer votre email"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Veuillez entrer un email valide"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordEditText.error = "Veuillez entrer votre mot de passe"
                return@setOnClickListener
            }

            signIn(email, password)
        }

        val signup = Intent(this, SignupActivity::class.java )
        startActivity(signup)


    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Authentification réussie", Toast.LENGTH_SHORT).show()
                    // You can also navigate to another activity here
                } else {
                    Toast.makeText(this, "Échec de l'authentification", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
