package com.meutevive.pmsearch.screens.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.screens.reset_mdp.ResetPasswordActivity
import com.meutevive.pmsearch.screens.search.SearchPMActivity
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
        val forgetPassword: TextView = findViewById(R.id.forgotPasswordTextView)
        val sigup: Button = findViewById(R.id.signUpButton)


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

            login(email, password)
        }
        //redirect to sign up activity
        sigup.setOnClickListener {
            val signup = Intent(this, SignupActivity::class.java )
            startActivity(signup)
        }

        //redirect to reste mdp activity
        forgetPassword.setOnClickListener{
           val resetmdp =  Intent(this, ResetPasswordActivity::class.java)
            startActivity(resetmdp)
            finish()
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.reload()?.addOnSuccessListener {
                        if (user.isEmailVerified) {
                            // User is signed in and email is verified
                            // Redirect to main activity
                            val intent = Intent(this, SearchPMActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // Else, show an error message
                            Toast.makeText(this, "Veuillez vérifier votre email avant de continuer", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Erreur de connexion", Toast.LENGTH_SHORT).show()
                }
            }
    }



}
