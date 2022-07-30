package com.anetos.parkme.core.helper

import android.content.Intent
import com.anetos.parkme.Application
import com.anetos.parkme.view.activity.MainActivity
import com.anetos.parkme.view.widget.login_register.LoginActivity

object Navigator {
    fun toLoginActivity() {
        val intent = Intent(Application.context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        Application.context.startActivity(intent)
    }

    fun toMainActivity() {
        val intent = Intent(Application.context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        Application.context.startActivity(intent)
    }
}