package com.meutevive.pmsearch.screens.reset_mdp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.meutevive.pmsearch.R

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        initializeViews()

    }

    private  fun initializeViews() {
        auth = FirebaseAuth.getInstance()
        emailEditText = findViewById(R.id.emailEditText)
        val resetPasswordButton: Button = findViewById(R.id.resetPasswordButton)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        resetPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isEmpty()) {
                emailEditText.error = "Veuillez entrer votre email"
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailEditText.error = "Veuillez entrer un email valide"
                return@setOnClickListener
            }

            resetPassword(email)
        }

    }
      //manage nack arrow click
      override fun onOptionsItemSelected(item: MenuItem): Boolean {
          return when (item.itemId) {
              android.R.id.home -> {
                  onBackPressed()
                  true
              }
              else -> super.onOptionsItemSelected(item)
          }
      }


    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Un email de réinitialisation de mot de passe a été envoyé à votre email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Une erreur s'est produite lors de l'envoi de l'email de réinitialisation", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
