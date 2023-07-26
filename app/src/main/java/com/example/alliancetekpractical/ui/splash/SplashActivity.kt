package com.example.alliancetekpractical.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.alliancetekpractical.R
import com.example.alliancetekpractical.ui.users.UsersActivity
import com.example.alliancetekpractical.ui.login.LoginActivity
import com.example.alliancetekpractical.utility.IS_USER_LOGIN
import com.example.alliancetekpractical.utility.SharedPref

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler(Looper.getMainLooper()).postDelayed({
            if (SharedPref[IS_USER_LOGIN,false]) {
                startActivity(Intent(this, UsersActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 3000)
    }
}