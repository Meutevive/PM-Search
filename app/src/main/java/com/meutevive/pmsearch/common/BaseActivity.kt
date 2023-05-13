import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.screens.home.HomeActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity
import com.meutevive.pmsearch.screens.search.SearchPMActivity

abstract class BaseActivity : AppCompatActivity() {

    abstract fun getContentViewId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getContentViewId())

    }
    var currentMenuItemId: Int = 0
    protected fun setupBottomNavigationView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        currentMenuItemId = bottomNavigationView.selectedItemId
        bottomNavigationView.selectedItemId = R.id.nav_item_search

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_item_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.nav_item_search -> {
                    // This will be called when the search item is clicked
                    val intent = Intent(this, SearchPMActivity::class.java)
                    intent.putExtra("currentUserId", FirebaseAuth.getInstance().currentUser?.uid)
                    startActivity(intent)
                    true
                }
                R.id.nav_item_add -> {
                    startActivity(Intent(this, RegisterPMActivity::class.java))
                    true
                }


             /*   R.id.nav_item_likes -> {
                    startActivity(Intent(this, ManagePMActivity::class.java))
                    true
                }*/
                /*R.id.nav_item_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }*/

                else -> false
            }
        }

    }




}


