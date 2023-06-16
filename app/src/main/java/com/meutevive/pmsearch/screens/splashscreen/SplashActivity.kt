package com.meutevive.pmsearch.screens.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.screens.login.LoginActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        startAnimation()
    }

    private fun startAnimation(){
        val anim = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val imageView = findViewById<ImageView>(R.id.logo)
        anim.reset()
        imageView.clearAnimation()
        imageView.startAnimation(anim)

        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationStart(animation: Animation) {}
        })
    }
}
