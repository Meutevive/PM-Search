package com.meutevive.pmsearch.screens.signup

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.R


class SignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firstNameEditText: TextInputEditText
    private lateinit var companyEditText: TextInputEditText
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordConfirmationEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        initializeViews()

    }


    private fun initializeViews() {

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        firstNameEditText = findViewById(R.id.firstnameEditText)
        companyEditText = findViewById(R.id.companyEditText)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordConfirmationEditText = findViewById(R.id.passwordConfirmationEditText)

        val signUpButton: Button = findViewById(R.id.signUpButton)
        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirmation = passwordConfirmationEditText.text.toString()
            val firstName = firstNameEditText.text.toString()
            val company = companyEditText.text.toString()

            //verifications des champs
            if (firstName.isEmpty()) {
                firstNameEditText.error = "Veuillez entrer votre prénom"
                return@setOnClickListener
            }

            if (company.isEmpty()) {
                companyEditText.error = "Veuillez entrer le nom de votre entreprise"
                return@setOnClickListener
            }

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

            if (passwordConfirmation.isEmpty()) {
                passwordConfirmationEditText.error = "Veuillez confirmer votre mot de passe"
                return@setOnClickListener
            }

            if (password != passwordConfirmation) {
                passwordConfirmationEditText.error = "Les mots de passe ne correspondent pas"
                return@setOnClickListener
            }

            createAccount(email, password, firstName, company)
        }

    }

    private fun createAccount(email: String, password: String, firstName: String, company: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        saveUserInfoInFirestore(user.uid, firstName, company)
                    }
                } else {
                    Toast.makeText(this, "La création de compte a échoué", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserInfoInFirestore(uid: String, firstName: String, company: String) {
        val user = HashMap<String, Any>()
        user["firstName"] = firstName
        user["company"] = company

        firestore.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Les informations de l'utilisateur ont été enregistrées avec succès", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "L'enregistrement des informations de l'utilisateur a échoué", Toast.LENGTH_SHORT).show()
            }
    }
}
