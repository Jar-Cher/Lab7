package com.example.lab7

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InputStream
import java.net.URL

class MyService : Service() {
    lateinit var downloadingJob: Job

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lateinit var image: Bitmap
        lateinit var path: String
        val imageName = "newImage"
        downloadingJob = CoroutineScope(Dispatchers.IO).launch(Dispatchers.IO) {
            try {
                val input: InputStream = URL(intent?.getStringExtra("URL")).openStream()
                image = BitmapFactory.decodeStream(input)
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e("SP", "Service problem with downloading / decoding")
            }
            try {
                image.compress(Bitmap.CompressFormat.PNG, 100, applicationContext.openFileOutput(imageName, Context.MODE_PRIVATE))
                path = applicationContext.getFileStreamPath(imageName).absolutePath
                sendBroadcast(Intent("Sending path to image").putExtra("path", path))
            } catch(e: Exception) {
                e.printStackTrace()
                Log.e("SP", "Service problem with saving file / broadcasting")
            }
            Log.d("SS", "Full path: $path")
        }
        Log.d("SS", "Service success!")
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        downloadingJob.cancel()
    }

}
