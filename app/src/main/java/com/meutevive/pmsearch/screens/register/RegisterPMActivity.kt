package com.meutevive.pmsearch.screens.register


import BaseActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.data.repository.PMRepository
import com.meutevive.pmsearch.models.PM
import java.io.IOException
import java.util.Locale


class RegisterPMActivity : BaseActivity() {

    override fun getContentViewId(): Int {
        return R.layout.activity_register_pm
    }

    private lateinit var selectedImageUri: Uri
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_pm)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //register btn
        val registerButton: Button = findViewById(R.id.submit_button)
        registerButton.setOnClickListener {
            if (this::selectedImageUri.isInitialized) {
                registerPM()
            } else {
                Toast.makeText(this, "Veuillez sélectionner une image avant d'enregistrer.", Toast.LENGTH_SHORT).show()
            }
        }
        //location textview.
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        locationEditText.setOnClickListener {
            requestLocation()
        }

        //upload btn

        val uploadButton: Button = findViewById(R.id.photoButton)
        uploadButton.setOnClickListener {
            openGallery()
        }



    }

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)

            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                // Obtenu le dernier emplacement connu. Dans de rares situations, cela peut être null.
                if (location != null) {
                    fillAddressFromLocation(location)
                }
            }
    }




    //obtien l'adresse
    private fun fillAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses?.get(0)

            val addressText = address?.let {
                String.format("%s, %s, %s",
                    if (address.maxAddressLineIndex > 0) address.getAddressLine(0) else "",
                    it.locality,
                    address.countryName)
            }

            val locationEditText = findViewById<EditText>(R.id.locationEditText)
            locationEditText.setText(addressText)

        } catch (e: IOException) {
            e.printStackTrace()
        }
    }



    private fun registerPM() {
        val pmNumber = findViewById<EditText>(R.id.pmNumberEditText).text.toString()
        val city = findViewById<EditText>(R.id.cityEditText).text.toString()
        val address = findViewById<EditText>(R.id.locationEditText).text.toString()
        val comment = findViewById<EditText>(R.id.commentEditText).text.toString()
        val date = System.currentTimeMillis()

        // Appele la fonction uploadPhoto
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
            pmRepository.registerPM(newPM) { success, id ->
                if (success) {
                    newPM.id = id
                    Toast.makeText(this, "PM enregistré avec succès.", Toast.LENGTH_SHORT).show()
                    finish()
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
            // Récupére l'URL de l'image téléchargée
            imagesRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                callback(downloadUrl.toString())
            }
        }.addOnFailureListener {
            // Gére l'échec du téléchargement ici, si nécessaire
            Toast.makeText(this, "Erreur lors de téléchargement.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    //Le code pour gérer le refus de permission :
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            GALLERY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {

                    Toast.makeText(this, "Permission refusée. Vous ne pourrez pas sélectionner une image.", Toast.LENGTH_SHORT).show()
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    requestLocation()
                } else {
                    Toast.makeText(this, "Permission refusée. La localisation ne sera pas automatiquement remplie.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "Une permission a été refusée.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    //Le code pour mettre à jour l'interface utilisateur pour afficher l'image sélectionnée :
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Utilise selectedImageUri dans la fonction uploadPhoto.
            selectedImageUri = data.data!!
            // Met à jour l'interface utilisateur pour afficher l'image sélectionnée.
            findViewById<ImageView>(R.id.selectedImageView).setImageURI(selectedImageUri)
        }
    }


    companion object {
        const val GALLERY_REQUEST_CODE = 1000
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}

