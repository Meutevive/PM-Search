package com.meutevive.pmsearch.screens.home

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.adapters.PMAdapters
import com.meutevive.pmsearch.models.PM
import com.meutevive.pmsearch.models.User
import com.meutevive.pmsearch.screens.login.LoginActivity
import com.meutevive.pmsearch.screens.manage.PMDetailsActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity
import com.meutevive.pmsearch.screens.search.SearchPMActivity

class HomeActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var user_full_name: TextView
    private lateinit var user_company: TextView
    private lateinit var recentPMRecyclerView: RecyclerView
    private lateinit var popularPMRecyclerView: RecyclerView
    private lateinit var recentPMAdapter: PMAdapters
    private lateinit var popularPMAdapter: PMAdapters
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: Toolbar
    private lateinit var content: CoordinatorLayout
    private lateinit var drawerToggle: ActionBarDrawerToggle
    private lateinit var drawerOpenIcon: Drawable
    private lateinit var drawerCloseIcon: Drawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initializeViews()
        setupDrawerMenu()
        fetchUserData()
        fetchPMsData()
    }

    private fun initializeViews() {

        // Initialize the NavigationView
        navigationView = findViewById(R.id.navigation_view)

        // Get the header view from the NavigationView
        val headerView = navigationView.getHeaderView(0)

        // Now find the TextViews from the header view
        user_full_name = headerView.findViewById(R.id.user_full_name)
        user_company = headerView.findViewById(R.id.user_company)

        //for draweer
        toolbar = findViewById(R.id.home_toolbar)
        content = findViewById(R.id.content)
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerOpenIcon = resources.getDrawable(R.drawable.ic_burger, theme)
        drawerCloseIcon = resources.getDrawable(R.drawable.ic_back, theme)


        // For Recent PMs
        recentPMRecyclerView = findViewById(R.id.recent_pm_recycler_view)
        recentPMAdapter = PMAdapters(emptyList()) { pm ->
            pm.id?.let { navigateToPMDetails(it) }
        }
        recentPMRecyclerView.adapter = recentPMAdapter
        recentPMRecyclerView.layoutManager = LinearLayoutManager(this)

        // For Popular PMs
        popularPMRecyclerView = findViewById(R.id.popular_pm_recycler_view)
        popularPMAdapter = PMAdapters(emptyList()) { pm ->
            pm.id?.let { navigateToPMDetails(it) }
        }
        popularPMRecyclerView.adapter = popularPMAdapter
        popularPMRecyclerView.layoutManager = LinearLayoutManager(this)


        //for navigation
        navigationView.setNavigationItemSelectedListener { menuItem ->
            menuItem.isChecked = true
            drawerLayout.closeDrawers()

            Handler(Looper.getMainLooper()).postDelayed({
                when (menuItem.itemId) {
                    R.id.nav_search_pm -> {
                        //handle search click
                        val searchIntent = Intent(this, SearchPMActivity::class.java)
                        startActivity(searchIntent)
                    }
                    R.id.nav_add_pm -> {
                        // Handle add pm click
                        val addPMIntent = Intent(this, RegisterPMActivity::class.java)
                        startActivity(addPMIntent)
                    }
                    R.id.nav_logout -> {
                        // Clear stored user data
                        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE)
                        with (sharedPref.edit()) {
                            clear()
                            apply()
                        }
                        // Sign out from Firebase Auth
                        FirebaseAuth.getInstance().signOut()

                        // Redirect the user to the Login Activity
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()
                    }
                    // Handle other drawer items...
                }
            }, 200) // le délai est défini à 200ms ici, mais vous pouvez l'ajuster en fonction de vos besoins

            true
        }


    }


    private fun navigateToPMDetails(pmId: String) {
        val intent = Intent(this, PMDetailsActivity::class.java)
        intent.putExtra("PM_ID", pmId)
        startActivity(intent)
    }

    private fun setupDrawerMenu() {
        setSupportActionBar(toolbar)

        val toggle = object : ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val slideX = drawerView.width * slideOffset
                content.translationX = slideX
                toolbar.translationX = slideX
            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }


    private fun fetchUserData() {
        val user = FirebaseAuth.getInstance().currentUser
        val userId = user?.uid

        if (userId != null) {
            firestore.collection("users").document(userId).get().addOnSuccessListener { document ->
                if (document != null) {
                    val fullname = document.getString("fullname")
                    val company = document.getString("company")

                    val user = User(fullname ?: "", company ?: "")
                    updateUIWithUserData(user)
                } else {
                    Log.d("HomeActivity", "No such document")
                    Toast.makeText(this, "Erreur : pas de document", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Log.d("HomeActivity", "Error getting documents: ", exception)
                Toast.makeText(this, "Erreur : ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.d("HomeActivity", "User is not logged in")
            Toast.makeText(this, "Erreur : utilisateur non connecté", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchPMsData() {
        firestore.collection("LesPM").get().addOnSuccessListener { result ->
            val pms = result.map { it.toObject(PM::class.java) }
            updateUIWithPMsData(pms)
        }
    }
    //get initials
    fun getInitials(fullName: String): String {
        val parts = fullName.split(" ")
        return parts.joinToString("") { it.firstOrNull()?.toUpperCase().toString() }
    }




    private fun updateUIWithUserData(user: User) {
        user_full_name.text = user.fullname
        user_company.text = user.company

        val initials = getInitials(user.fullname)

        val colorGenerator = ColorGenerator.MATERIAL // or another ColorGenerator
        val drawable = TextDrawable.builder()
            .beginConfig()
            .width(60)  // width in px
            .height(60) // height in px
            .endConfig()
            .buildRound(initials, colorGenerator.randomColor)

        val profileImage = findViewById<de.hdodenhof.circleimageview.CircleImageView>(R.id.profile_image)
        profileImage.setImageDrawable(drawable)
    }

    private fun updateUIWithPMsData(pms: List<PM>) {
        val recentPMs = pms.sortedBy { it.date }.takeLast(5)
        val popularPMs = pms.sortedBy { it.date }.take(5)

        recentPMAdapter.updatePMList(recentPMs)
        popularPMAdapter.updatePMList(popularPMs)
    }
}
