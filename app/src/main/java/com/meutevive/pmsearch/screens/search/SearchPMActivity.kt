package com.meutevive.pmsearch.screens.search

import BaseActivity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.net.Uri
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.adapters.PMAdapters
import com.meutevive.pmsearch.models.PM
import androidx.appcompat.widget.Toolbar


class SearchPMActivity : BaseActivity() {
    override fun getContentViewId(): Int {
        return R.layout.activity_search_pm
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_pm)


    }





}