package com.example.lab7

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.io.InputStream
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val url = "https://upload.wikimedia.org/wikipedia/commons/0/0d/Reflexion_der_Augen_einer_Katze.JPG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("CP","Task 1 running")
        button.setOnClickListener {
            startService(Intent(this, MyService::class.java).putExtra("URL", url))
        }
    }
}