package com.meutevive.pmsearch.screens.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.models.PM

import com.bumptech.glide.Glide
import com.meutevive.pmsearch.data.repository.FirestorePMRepository

class EditPMActivity : AppCompatActivity() {

    private val repository = FirestorePMRepository()

    // replace these with your actual input fields
    private lateinit var pmNumberInput: EditText
    private lateinit var cityInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var commentInput: EditText
    private lateinit var confirmButton: Button
    private lateinit var imageView: ImageView

    private lateinit var pm: PM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_pm)

        // replace these with your actual input fields
        pmNumberInput = findViewById(R.id.pmNumberEditText)
        cityInput = findViewById(R.id.cityEditText)
        addressInput = findViewById(R.id.locationEditText)
        commentInput = findViewById(R.id.commentEditText)
        confirmButton = findViewById(R.id.submit_button)
        imageView = findViewById(R.id.selectedImageView)

        pm = intent.getParcelableExtra<PM>("pm")!!

        // load PM data into input fields
        pmNumberInput.setText(pm.pmNumber)
        cityInput.setText(pm.city)
        addressInput.setText(pm.address)
        commentInput.setText(pm.comment)

        // load image with Glide
        Glide.with(this)
            .load(pm.photoUrl)
            .into(imageView)

        confirmButton.setOnClickListener {
            saveEdits()
        }
    }

    private fun saveEdits() {
        pm.pmNumber = pmNumberInput.text.toString()
        pm.city = cityInput.text.toString()
        pm.address = addressInput.text.toString()
        pm.comment = commentInput.text.toString()

        // save the edited PM
        repository.savePM(pm) { success ->
            if (success) {
                Log.d("EditPMActivity", "PM successfully updated!")
                // go back to the previous activity
                finish()
            } else {
                Log.w("EditPMActivity", "Error updating PM")
            }
        }

        // go back to the previous activity
        finish()
    }
}
