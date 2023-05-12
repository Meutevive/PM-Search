package com.meutevive.pmsearch.screens.home

import BaseActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.meutevive.pmsearch.R

class HomeActivity : BaseActivity() {
    override fun getContentViewId(): Int {
        return R.layout.activity_home
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //setup bottom navigation
        setupBottomNavigationView()
    }
}