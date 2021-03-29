package com.example.lab7

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.lab7.databinding.ActivityMain2Binding
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity() {
    private val url = "https://upload.wikimedia.org/wikipedia/commons/0/0d/Reflexion_der_Augen_einer_Katze.JPG"
    lateinit var messenger: Messenger

    inner class mConnection : ServiceConnection {
        var messengerConnect : Messenger? = null

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            messengerConnect = Messenger(service)
            Log.w("SC", "onServiceConnected happened")
        }

        override fun onServiceDisconnected(className: ComponentName) {
            messengerConnect = null
            Log.w("SC", "onServiceDisconnected happened")
        }
    }

    inner class ActivityHandler(looper: Looper) : Handler(looper) {
        override fun handleMessage(msg: Message) {
            if (msg.what != 42) {
                super.handleMessage(msg)
                return
            }
            textView.text = msg.obj as String
        }
    }

    val myConnectionService = mConnection()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        Log.d("CP","Task 3 running")
        messenger = Messenger(ActivityHandler(mainLooper))

        bindService(Intent(this, MyService2::class.java),
            myConnectionService, Context.BIND_AUTO_CREATE)

        button.setOnClickListener {
            startService(Intent(this, MyService2::class.java).putExtra("URL", url))
        }

        button2.setOnClickListener {
            val msg: Message = Message.obtain()
            msg.obj = url
            msg.what = 42
            msg.replyTo = messenger
            try {
                myConnectionService.messengerConnect?.send(msg)
            } catch (e: RemoteException) {
                Log.e("BT","Message not sent")
                e.printStackTrace()
            }
        }
    }
}