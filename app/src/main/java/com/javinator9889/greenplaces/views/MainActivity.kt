package com.javinator9889.greenplaces.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.javinator9889.greenplaces.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        with(Intent(this, FirebaseUIActivity::class.java)) {
            startActivity(this)
        }
    }
}