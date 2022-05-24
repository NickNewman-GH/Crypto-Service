package com.example.cryptoservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL
import java.net.UnknownHostException
import java.util.*



class MainActivity : AppCompatActivity() {
    var is_service_running = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.beginService).setOnClickListener {
            onClick()
        }
        //val data = Scanner(stream).nextLine()
        //val jObject = JSONObject(data)
        //Log.d("mytag", data)
    }

    public fun onClick() {
        // Используем IO-диспетчер вместо Main (основного потока)
        //GlobalScope.launch (Dispatchers.IO) {
        if (!is_service_running){
            intent = Intent(this, CryptoService::class.java)
            startService(intent)
        }
            //loadCrypto()
        //}
    }

    companion object{
        var prev_crypto_rate = (-1).toDouble();
    }
}