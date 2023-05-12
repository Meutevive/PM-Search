package com.meutevive.pmsearch.screens.home

import BaseActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.adapters.PMAdapters
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.data.repository.PMRepository
import com.meutevive.pmsearch.screens.manage.ManagePMActivity

class HomeActivity : BaseActivity() {
    override fun getContentViewId(): Int {
        return R.layout.activity_home
    }

    private lateinit var recentPmRecyclerView: RecyclerView
    private lateinit var popularPmRecyclerView: RecyclerView
    private lateinit var pmRepository: PMRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialiser les RecyclerViews
        recentPmRecyclerView = findViewById(R.id.recent_pm_recycler_view)
        popularPmRecyclerView = findViewById(R.id.popular_pm_recycler_view)


        //setup bottom navigation
        setupBottomNavigationView()

        //initialise les Repository
        pmRepository = FirestorePMRepository()

        // Chargement des données
        loadData()
    }

    private fun loadData() {
        // Récupérer les PM de la base de données
        pmRepository.getAllPMs { pmList ->
            if (pmList != null) {
                // Créer un adaptateur avec la liste de PM
                val pmAdapter = PMAdapters(pmList) { pm ->
                    // Ouvrir ManagePMActivity lorsque l'utilisateur clique sur un élément de la liste
                    val intent = Intent(this, ManagePMActivity::class.java)
                    intent.putExtra("PM", pm) // Passer le PM à ManagePMActivity
                    startActivity(intent)
                }

                // Définir l'adaptateur pour le RecyclerView
                recentPmRecyclerView.adapter = pmAdapter
                popularPmRecyclerView.adapter = pmAdapter

                // Définir un LayoutManager pour le RecyclerView
                // Ici, nous utilisons un LinearLayoutManager pour afficher les éléments de la liste verticalement
                recentPmRecyclerView.layoutManager = LinearLayoutManager(this)
                popularPmRecyclerView.layoutManager = LinearLayoutManager(this)
            }
        }
    }




}