package com.mricefox.archmage.sample.kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.mricefox.archmage.annotation.Target

/**
 * <p>Author:MrIcefox
 * <p>Email:extremetsa@gmail.com
 * <p>Description:
 * <p>Date:2018/10/22
 */
@Target(path = "/kotlin/kotlin")
class KotlinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kotlin)
    }
}