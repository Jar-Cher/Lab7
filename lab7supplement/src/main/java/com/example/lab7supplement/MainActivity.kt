package com.example.lab7supplement

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.textView

class MainActivity : AppCompatActivity() {
    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val path = intent.getStringExtra("path")
            Log.v("BR", "Path to file recieved $path")
            textView.text = path
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        registerReceiver(MyReceiver(), IntentFilter("Sending path to image"))
    }
}