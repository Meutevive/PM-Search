package com.meutevive.pmsearch.screens.manage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.models.PM
import com.meutevive.pmsearch.screens.edit.EditPMActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity

class PMDetailsActivity : AppCompatActivity() {

    private lateinit var pm: PM
    private val repository = FirestorePMRepository()
    private lateinit var pmNameTextView: TextView
    private lateinit var  pmDetailTextView:TextView
    private lateinit var pmAdresse: TextView
    private lateinit var pmCity:TextView
    private lateinit var  pmImageView: ImageView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var addButton: FloatingActionButton
    private lateinit var routeButton: FloatingActionButton

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pm_details)

        // retrieve the PM object from intent
        pm = intent.getParcelableExtra<PM>("pm")!!

        pmNameTextView = findViewById(R.id.pm_number)
        pmDetailTextView = findViewById(R.id.pm_comment)
        pmAdresse= findViewById(R.id.pm_address)
        pmCity = findViewById(R.id.pm_city)
        pmImageView = findViewById(R.id.pm_photo)
        editButton = findViewById(R.id.edit_button)
        deleteButton = findViewById(R.id.delete_button)
        addButton = findViewById(R.id.add_pm)
        routeButton  = findViewById(R.id.route_button)

        // set the text views with the PM information
        pmNameTextView.text = pm.pmNumber
        pmDetailTextView.text = pm.comment
        pmAdresse.text = pm.address

        // load image with Glide
        pm = intent.getParcelableExtra<PM>("pm")!!

        repository.getPM(pm.id!!) { updatedPm ->
            pm = updatedPm // update local PM object
            // load image with Glide
            Glide.with(this)
                .load(pm.photoUrl)
                .into(pmImageView)
        }

        // handle click on the edit button
        editButton.setOnClickListener {
            val editIntent = Intent(this, EditPMActivity::class.java)
            editIntent.putExtra("pm", pm)
            startActivity(editIntent)
        }


        // handle click on the delete button
        deleteButton.setOnClickListener {
            pm.id?.let { it1 ->
                FirestorePMRepository().deletePM(it1) { success ->
                    if (success) {
                        // PM was deleted successfully
                        Toast.makeText(this, "PM deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // PM could not be deleted
                        Toast.makeText(this, "Could not delete PM", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        // handle click on the add button
        addButton.setOnClickListener {
            // start the add PM activity
            val addintent = Intent(this, RegisterPMActivity::class.java)
            startActivity(addintent)
        }

        // New - handle click on the route button
        routeButton.setOnClickListener {
            // start the navigation to the PM location
            val gmmIntentUri = Uri.parse("geo:0,0?q=${pm.address}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "No map application found", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
