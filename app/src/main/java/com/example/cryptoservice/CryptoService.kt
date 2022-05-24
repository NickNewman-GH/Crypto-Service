package com.example.cryptoservice

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import java.net.UnknownHostException

class CryptoService : Service() {

    private lateinit var mHandler: Handler
    private lateinit var mRunnable: Runnable

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mHandler = Handler()
        mRunnable = Runnable {
            GlobalScope.launch(Dispatchers.IO) {
                loadCrypto()
            }
        }
        mHandler.postDelayed(mRunnable, 5000)
        return Service.START_STICKY
    }

    fun notificate_user(is_rises: Boolean){
        val CHANNEL_ID = "CHANNEL_ID"
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANNEL_ID,"name", NotificationManager.IMPORTANCE_DEFAULT)
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        channel.description = "channelDescription"
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        val notification_text = if (is_rises) "Crypto rate rises! You can sell it with profit!" else "Bad news. Crypto rate falls!"

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.crypto)
            .setContentTitle("Crypto rate monitoring service")
            .setContentText(notification_text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        if (Build.VERSION.SDK_INT >= 21) builder.setVibrate(LongArray(0))
        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    suspend fun loadCrypto() {
        val API_KEY = getString(R.string.api_key)
        val CRYPTO = getString(R.string.crypto)
        val CURRENCIES = getString(R.string.currencies)
        val URL = "https://min-api.cryptocompare.com/data/price?fsym=$CRYPTO&tsyms=$CURRENCIES&api_key=$API_KEY"
        try {
            val data = URL(URL).readText()//.getContent() as InputStream
            Log.d("stream", data)
            val jObject = JSONObject(data)
            val currency_crypto_exchange = jObject.getDouble("USD");
            if (MainActivity.prev_crypto_rate < 0){
                MainActivity.prev_crypto_rate = currency_crypto_exchange
            }
            if (MainActivity.prev_crypto_rate < currency_crypto_exchange){
                Log.d("mytag", "Crypto is rising")
                notificate_user(true)
            } else if (MainActivity.prev_crypto_rate > currency_crypto_exchange){
                Log.d("mytag", "Crypto is decreasing")
                notificate_user(false)
            }
        } catch (e : UnknownHostException){
            Log.d("mytag", "No internet")
        } catch (e : Exception) {
            Log.d("mytag", "Unknown exception: $e")
        }
        mHandler.postDelayed(mRunnable, 5000)
    }
}