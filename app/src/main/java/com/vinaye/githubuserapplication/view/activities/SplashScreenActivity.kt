package com.vinaye.githubuserapplication.view.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.vinaye.githubuserapplication.R

class SplashScreenActivity : AppCompatActivity() {
    private lateinit var runnable: Runnable
    private lateinit var handler: Handler
    private lateinit var ivSplashScreen: ImageView

    companion object {
        const val TIME_IN_MILLIS = 3000L
    }

    @SuppressLint("UseCompatLoadingForDrawables", "CheckResult", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //action bar hide
        hideActionBar()

        //initialize widgets
        initWidgets()

        // set widgets
        setComponents()

        // splash action
        handlerAction()

    }

    private fun handlerAction() {
        runnable = Runnable {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
        handler = Handler(mainLooper).apply {
            postDelayed(runnable, TIME_IN_MILLIS)
        }
    }

    private fun hideActionBar() {
        window.statusBarColor =
            ContextCompat.getColor(applicationContext, R.color.primaryDark)
        supportActionBar?.hide()
    }

    private fun setComponents() {

        Glide.with(this)
            .load(getDrawable(R.drawable.ic_logo_splash))
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivSplashScreen)
    }

    private fun initWidgets() {
        ivSplashScreen = findViewById(R.id.iv_splash_screen)
    }

    override fun onDestroy() {
        super.onDestroy()
        // avoiding memory leaks
        handler.removeCallbacks(runnable)
    }
}