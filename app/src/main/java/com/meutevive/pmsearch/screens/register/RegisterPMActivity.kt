package com.meutevive.pmsearch.screens.register


import BaseActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.data.repository.PMRepository
import com.meutevive.pmsearch.models.PM

class RegisterPMActivity : BaseActivity() {

    override fun getContentViewId(): Int {
        return R.layout.activity_register_pm
    }

    private lateinit var selectedImageUri: Uri


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pm)

        //register btn
        val registerButton: Button = findViewById(R.id.submit_button)
        registerButton.setOnClickListener {
            if (this::selectedImageUri.isInitialized) {
                registerPM()
            } else {
                Toast.makeText(this, "Veuillez sélectionner une image avant d'enregistrer.", Toast.LENGTH_SHORT).show()
            }
        }

        //upload btn

        val uploadButton: Button = findViewById(R.id.photoButton)
        uploadButton.setOnClickListener {
            openGallery()
        }



    }

    private fun registerPM() {
        val pmNumber = findViewById<EditText>(R.id.pmNumberEditText).text.toString()
        val city = findViewById<EditText>(R.id.cityEditText).text.toString()
        val address = findViewById<EditText>(R.id.locationEditText).text.toString()
        val comment = findViewById<EditText>(R.id.commentEditText).text.toString()
        val date = System.currentTimeMillis()

        // Appeler la fonction uploadPhoto ici
        uploadPhoto(selectedImageUri) { photoUrl ->
            val newPM = PM(
                pmNumber = pmNumber,
                city = city,
                address = address,
                comment = comment,
                date = date,
                photoUrl = photoUrl
            )

            val pmRepository: PMRepository = FirestorePMRepository()
            pmRepository.registerPM(newPM) { success ->
                if (success) {
                    Toast.makeText(this, "PM enregistré avec succès.", Toast.LENGTH_SHORT).show()
                    finish() // Ferme l'activité et retourne à l'écran précédent.
                } else {
                    Toast.makeText(this, "Erreur lors de l'enregistrement du PM. Veuillez réessayer.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    //La fonction uploadPhoto pour télécharger une image sur Firebase Storage
    private fun uploadPhoto(uri: Uri, callback: (String) -> Unit) {
        val storage = Firebase.storage
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${uri.lastPathSegment}")

        val uploadTask = imagesRef.putFile(uri)
        uploadTask.addOnSuccessListener { taskSnapshot ->
            // Récupérer l'URL de l'image téléchargée
            imagesRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                callback(downloadUrl.toString())
            }
        }.addOnFailureListener {
            // Gérer l'échec du téléchargement ici, si nécessaire
            Toast.makeText(this, "Erreur lors de téléchargement.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

   /* //Le code pour gérer le refus de permission :
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            // Gérer le refus de la permission ici, si nécessaire.
            Toast.makeText(this, "Permission refusée. Vous ne pourrez pas sélectionner une image.", Toast.LENGTH_SHORT).show()
        }
    }
*/
    //Le code pour mettre à jour l'interface utilisateur pour afficher l'image sélectionnée :
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            // Utilisez selectedImageUri dans la fonction uploadPhoto.
            // Mettez à jour l'interface utilisateur pour afficher l'image sélectionnée.
            findViewById<ImageView>(R.id.selectedImageView).setImageURI(selectedImageUri)
        }
    }


    companion object {
        const val GALLERY_REQUEST_CODE = 1000
    }
}

