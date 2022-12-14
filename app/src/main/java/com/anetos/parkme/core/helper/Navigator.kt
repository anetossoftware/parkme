package com.anetos.parkme.core.helper

import android.content.Intent
import com.anetos.parkme.MyApplication
import com.anetos.parkme.view.activity.MainActivity
import com.anetos.parkme.view.widget.login_register.LoginActivity

object Navigator {
    fun toLoginActivity(clearStack: Boolean = false) {
        val intent = Intent(MyApplication.context, LoginActivity::class.java).apply {
            if (clearStack)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            else
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        MyApplication.context.startActivity(intent)
    }

    fun toMainActivity(clearStack: Boolean = false) {
        val intent = Intent(MyApplication.context, MainActivity::class.java).apply {
            if (clearStack)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            else
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        MyApplication.context.startActivity(intent)
    }
}