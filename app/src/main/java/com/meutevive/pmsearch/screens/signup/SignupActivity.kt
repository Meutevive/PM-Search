package com.meutevive.pmsearch.screens.signup

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.screens.login.LoginActivity


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
        val toolbar: Toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }



        signUpButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirmation = passwordConfirmationEditText.text.toString()
            val fullname = firstNameEditText.text.toString()
            val company = companyEditText.text.toString()

            // Vérifications des champs
            if (fullname.isEmpty() || !fullname.matches(Regex("^[a-zA-Z- ]+$"))) {
                firstNameEditText.error = "Veuillez entrer un prénom valide"
                return@setOnClickListener
            }

            if (company.isEmpty() || !company.matches(Regex("^[a-zA-Z0-9- ]+$"))) {
                companyEditText.error = "Veuillez entrer un nom d'entreprise valide"
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

            if (password.isEmpty() || password.length < 8) {
                passwordEditText.error = "Le mot de passe doit avoir au moins 8 caractères"
                return@setOnClickListener
            }

            if (passwordConfirmation.isEmpty() || password != passwordConfirmation) {
                passwordConfirmationEditText.error = "Les mots de passe ne correspondent pas"
                return@setOnClickListener
            }

            createAccount(email, password, fullname, company)
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


    private fun createAccount(email: String, password: String, fullname: String, company: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification() //email de vérification envoyer dans la création de compte
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                saveUserInfoInFirestore(user.uid, fullname, company)
                            }
                        }
                } else {
                    Toast.makeText(this, "La création de compte a échoué", Toast.LENGTH_SHORT).show()
                }
            }
    }


    private fun saveUserInfoInFirestore(uid: String, fullname: String, company: String) {
        val user = HashMap<String, Any>()
        user["fullname"] = fullname
        user["company"] = company

        firestore.collection("users").document(uid)
            .set(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Les informations de l'utilisateur ont été enregistrées avec succès", Toast.LENGTH_SHORT).show()

                // Redirige l'utilisateur vers LoginActivity après l'inscription
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "L'enregistrement des informations de l'utilisateur a échoué", Toast.LENGTH_SHORT).show()
            }
    }

}
