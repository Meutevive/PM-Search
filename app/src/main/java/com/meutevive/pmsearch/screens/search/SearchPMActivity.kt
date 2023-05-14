package com.meutevive.pmsearch.screens.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.adapters.PMAdapters
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.screens.manage.PMDetailsActivity

class SearchPMActivity : AppCompatActivity() {

    private lateinit var pmAdapter: PMAdapters
    private lateinit var firestorePMRepository: FirestorePMRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pm)

        val searchResultsRecyclerView: RecyclerView = findViewById(R.id.search_results_recycler_view)
        val pmSearchView: SearchView = findViewById(R.id.pmSearchView)

        firestorePMRepository = FirestorePMRepository()

        pmAdapter = PMAdapters(listOf()) { pm ->
            // Handle click on a PM
            val intent = Intent(this, PMDetailsActivity::class.java)
            intent.putExtra("pm", pm)
            startActivity(intent)
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
    }
    private fun performSearch(query: String) {
        firestorePMRepository.searchPM(query) { results, exception ->
            if (exception != null) {
                // Handle the error
                Toast.makeText(this, "An error occurred: $exception", Toast.LENGTH_SHORT).show()
            } else {
                // Update the RecyclerView with the search results
                pmAdapter.updatePMList(results)
            }
        }
    }
}


