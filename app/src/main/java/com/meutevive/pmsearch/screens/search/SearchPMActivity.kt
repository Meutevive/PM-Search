package com.meutevive.pmsearch.screens.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.adapters.PMAdapters
import com.meutevive.pmsearch.data.repository.AlgoliaClient
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.models.PM
import com.meutevive.pmsearch.screens.manage.PMDetailsActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity

class SearchPMActivity : AppCompatActivity() {

    private lateinit var pmAdapter: PMAdapters
    private lateinit var firebasePMRepository: FirestorePMRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pm)
        supportActionBar?.hide()

        firebasePMRepository = FirestorePMRepository()

        initializeViews()
    }


    private fun initializeViews() {
        val searchResultsRecyclerView: RecyclerView = findViewById(R.id.search_results_recycler_view)
        val pmSearchView: SearchView = findViewById(R.id.pmSearchView)
        val addPM: FloatingActionButton = findViewById(R.id.add_pm)

        pmAdapter = PMAdapters(listOf()) { pm ->
            // Handle click on a PM
            val intent = Intent(this, PMDetailsActivity::class.java)
            intent.putExtra("pm", pm)
            startActivity(intent)
        }

        //floatingAction btn add pm fun
        addPM.setOnClickListener{
            val addIntent = Intent(this, RegisterPMActivity::class.java)
            startActivity(addIntent)
        }

        searchResultsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchPMActivity)
            adapter = pmAdapter
        }

        pmSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Do nothing on submit, we update as the user types
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrBlank()) {
                    performSearch(newText)
                }
                return true
            }
        })

        val searchPmButton: FloatingActionButton = findViewById(R.id.searchPmButton)
        searchPmButton.setOnClickListener {
            val query = pmSearchView.query.toString()
            if (!query.isBlank()) {
                performSearch(query)
            }
        }
    }

    //update search by keyword
    private fun performSearch(query: String) {
        firebasePMRepository.searchPM(query) { results: List<PM>?, exception: Exception? ->
            if (exception != null) {
                // Handle the error
                Toast.makeText(this, "Une erreur s'est produite: $exception", Toast.LENGTH_SHORT).show()
            } else {
                // Update the RecyclerView with the search results
                if (results != null) {
                    if (results.isEmpty()) {
                        Toast.makeText(this, "Aucun PM trouvé", Toast.LENGTH_SHORT).show()
                    } else {
                        pmAdapter.updatePMList(results)
                    }
                }
            }
        }
    }
    //cycle de vie de la recherche
    override fun onResume() {
        super.onResume()
        val pmSearchView: SearchView = findViewById(R.id.pmSearchView)
        val query = pmSearchView.query.toString()
        if (!query.isBlank()) {
            performSearch(query)
        }
    }








}


