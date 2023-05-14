package com.meutevive.pmsearch.screens.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.adapters.PMAdapters
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.screens.manage.PMDetailsActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity

class SearchPMActivity : AppCompatActivity() {

    private lateinit var pmAdapter: PMAdapters
    private lateinit var firestorePMRepository: FirestorePMRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pm)

        val searchResultsRecyclerView: RecyclerView = findViewById(R.id.search_results_recycler_view)
        val pmSearchView: SearchView = findViewById(R.id.pmSearchView)
        val addPM: FloatingActionButton = findViewById(R.id.add_pm)

        firestorePMRepository = FirestorePMRepository()

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


        searchPmButton.setOnClickListener {
            val query = pmSearchView.query.toString()
            if (!query.isBlank()) {
                performSearch(query)
            }
        }

    }


    private fun performSearch(query: String) {
        firestorePMRepository.searchPM(query) { results, exception ->
            if (exception != null) {
                // Handle the error
                Toast.makeText(this, "An error occurred: $exception", Toast.LENGTH_SHORT).show()
            } else {
                // Update the RecyclerView with the search results
                if (results.isNotEmpty()) {
                    pmAdapter.updatePMList(results)
                } else {
                    Toast.makeText(this, "No PMs found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}


