package com.meutevive.pmsearch.screens.register


import BaseActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.data.repository.PMRepository
import com.meutevive.pmsearch.models.PM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        supportActionBar?.hide()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initializeViews()
    }

    private fun initializeViews() {
        // Register button
        val registerButton: Button = findViewById(R.id.submit_button)
        val toolbar: Toolbar = findViewById(R.id.register_pm_toolbar)

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        registerButton.setOnClickListener {
            if (this::selectedImageUri.isInitialized) {
                registerPM()
            } else {
                Toast.makeText(this, "Veuillez sélectionner une image avant d'enregistrer.", Toast.LENGTH_SHORT).show()
            }
        }

        // Location textview
        val locationEditText = findViewById<EditText>(R.id.locationEditText)
        locationEditText.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                requestLocation()
            }
        }

        // Upload button
        val uploadButton: Button = findViewById(R.id.photoButton)
        uploadButton.setOnClickListener {
            openGallery()
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

    private fun requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)

            return
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // Précise l'intervalle de mise à jour en millisecondes
            fastestInterval = 5000 // Précise l'intervalle de mise à jour le plus rapide en millisecondes
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                CoroutineScope(Dispatchers.IO).launch {
                    locationResult.lastLocation?.let { fillAddressFromLocation(it) }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }





    //obtien l'adresse
    private suspend fun fillAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())

        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            val address = addresses?.get(0)

            val addressText = address?.let {

                String.format("%s, %s, %s, %s, %s",
                    it.subThoroughfare, //numéro de rue
                    it.thoroughfare, // nom de rue
                    it.postalCode, // code postal
                    it.locality, //  ville
                    it.countryName) // pays
            }

            withContext(Dispatchers.Main) {
                val locationEditText = findViewById<EditText>(R.id.locationEditText)
                locationEditText.setText(addressText)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }




    private fun registerPM() {
        val pmNumber = findViewById<EditText>(R.id.pmNumberEditText).text.toString()
        val address = findViewById<EditText>(R.id.locationEditText).text.toString()
        val comment = findViewById<EditText>(R.id.commentEditText).text.toString()
        val date = System.currentTimeMillis()

        // Appele la fonction uploadPhoto
        uploadPhoto(selectedImageUri) { photoUrl ->
            val newPM = PM(
                pmNumber = pmNumber,
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

