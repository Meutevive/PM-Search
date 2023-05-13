package com.meutevive.pmsearch.screens.manage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.models.PM
import com.meutevive.pmsearch.screens.edit.EditPMActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity

class PM_DetailsActivity : AppCompatActivity() {

    private lateinit var pm: PM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pm_details)

        // retrieve the PM object from intent
        pm = intent.getParcelableExtra<PM>("pm")!!

        val pmNameTextView: TextView = findViewById(R.id.pm_name)
        val pmDetailTextView: TextView = findViewById(R.id.pm_detail)
        val editButton: Button = findViewById(R.id.edit_button)
        val deleteButton: Button = findViewById(R.id.delete_button)
        val addButton: FloatingActionButton = findViewById(R.id.add_pm)

        // set the text views with the PM information
        pmNameTextView.text = pm.pmNumber
        pmDetailTextView.text = pm.comment

        // handle click on the edit button
        editButton.setOnClickListener {
            val editIntent = Intent(this, EditPMActivity::class.java)
            editIntent.putExtra("pm", pm)
            startActivity(editIntent)
        }


        // handle click on the delete button
        deleteButton.setOnClickListener {
            FirestorePMRepository().deletePM(pm.id) { success ->
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



        // handle click on the add button
        addButton.setOnClickListener {
            // start the add PM activity
            val addintent = Intent(this, RegisterPMActivity::class.java)
            startActivity(addintent)
        }
    }
}
