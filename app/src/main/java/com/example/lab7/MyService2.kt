package com.example.lab7

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.URL

class MyService2 : Service() {

    private lateinit var myMessenger: Messenger
    lateinit var downloadingJob: Job
    val imageName = "newImage2"

    inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            if (msg.what != 42) {
                Log.e("IH", "Wrong Message received!")
                super.handleMessage(msg)
                return
            }
            Log.v("IH", "Message received!")
            val url = msg.obj as String
            val replyTo = msg.replyTo

            CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
                val newMsg = Message.obtain()
                val path = getPath(url, imageName)
                newMsg.obj = path
                newMsg.what = 42
                replyTo.send(newMsg)
            }
        }
    }

    fun getPath(url: String, imageName: String): String {
        lateinit var image: Bitmap
        lateinit var path: String

        Log.i("GP", url)
        try {
            val input: InputStream = URL(url).openStream()
            image = BitmapFactory.decodeStream(input)
        } catch(e: Exception) {
            e.printStackTrace()
            Log.e("SP", "Service problem with downloading / decoding")
        }
        try {
            image.compress(Bitmap.CompressFormat.PNG, 100, applicationContext.openFileOutput(imageName, Context.MODE_PRIVATE))
            path = applicationContext.getFileStreamPath(imageName).absolutePath
        } catch(err: Exception) {
            err.printStackTrace()
            Log.e("SP", "Service problem with saving file / broadcasting")
        }
        Log.d("SS", "getPath successful, full path: $path")
        return path
    }

    fun getPath(intent: Intent?, imageName: String): String {
        return getPath(intent?.getStringExtra("URL") ?: throw IOException(), imageName)
    }

    override fun onBind(intent: Intent): IBinder? {
        return Messenger(ServiceHandler(Looper.getMainLooper())).binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lateinit var path: String

        downloadingJob = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            path = getPath(intent, imageName)
            sendBroadcast(Intent("Sending path to image").putExtra("path", path))
        }

        Log.d("SS", "Service success!")
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        downloadingJob.cancel()
    }

}
