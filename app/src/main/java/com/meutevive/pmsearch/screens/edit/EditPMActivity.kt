package com.meutevive.pmsearch.screens.edit

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.models.PM

import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.screens.register.RegisterPMActivity.Companion.GALLERY_REQUEST_CODE
import com.meutevive.pmsearch.screens.search.SearchPMActivity

class EditPMActivity : AppCompatActivity() {

    private val repository = FirestorePMRepository()

    // déclare les propriété
    private lateinit var pmNumberInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var commentInput: EditText
    private lateinit var confirmButton: Button
    private lateinit var imageView: ImageView
    private lateinit var PhotoButton: Button
    private lateinit var selectedImageUri: Uri


    private lateinit var pm: PM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pm)

        // initialise
        pmNumberInput = findViewById(R.id.pmNumberEditText)
        cityInput = findViewById(R.id.cityEditText)
        addressInput = findViewById(R.id.locationEditText)
        commentInput = findViewById(R.id.commentEditText)
        confirmButton = findViewById(R.id.submit_button)
        imageView = findViewById(R.id.selectedImageView)
        PhotoButton = findViewById(R.id.photoButton)


        pm = intent.getParcelableExtra<PM>("pm")!!

        // load PM data into input fields
        pmNumberInput.setText(pm.pmNumber)
        cityInput.setText(pm.city)
        addressInput.setText(pm.address)
        commentInput.setText(pm.comment)

        // load image with Glide
        pm = intent.getParcelableExtra<PM>("pm")!!

        repository.getPM(pm.id!!) { updatedPm ->
            pm = updatedPm // update local PM object
            // load image with Glide
            Glide.with(this)
                .load(pm.photoUrl)
                .into(imageView)
        }



        //call openGallery to execute.
        PhotoButton.setOnClickListener {
            openGallery()
        }


        confirmButton.setOnClickListener {
            saveEdits()
        }
    }

    //updatePM (modified to add photo edits)
    private fun saveEdits() {
        pm.pmNumber = pmNumberInput.text.toString()
        pm.city = cityInput.text.toString()
        pm.address = addressInput.text.toString()
        pm.comment = commentInput.text.toString()

        if (this::selectedImageUri.isInitialized) {
            uploadPhoto(selectedImageUri) { photoUrl ->
                pm.photoUrl = photoUrl

                // update ImageView with the new photo
                Glide.with(this)
                    .load(photoUrl)
                    .into(imageView)

                repository.updatePM(pm) { success ->
                    if (success) {
                        Toast.makeText(this, "PM mis à jour avec succès.", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Erreur lors de la mise à jour du PM.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            repository.updatePM(pm) { success ->
                if (success) {
                    Toast.makeText(this, "PM mis à jour avec succès.", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erreur lors de la mise à jour du PM.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }





    //fun to open the gallery
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    //manage result to obtain photo url
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data!!
            imageView.setImageURI(selectedImageUri)
        }
    }

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




}
