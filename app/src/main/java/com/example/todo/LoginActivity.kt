package com.example.todo

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {


    private lateinit var mScale : Animation


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setUpListener()
        setUpAnim()
    }

    private fun setUpAnim() {
        mScale = AnimationUtils.loadAnimation(this , R.anim.scale_animation)
    }

    private fun setUpListener() {
        loginBt.setOnClickListener {
            loginBt.startAnimation(mScale)

            

        }
    }


}