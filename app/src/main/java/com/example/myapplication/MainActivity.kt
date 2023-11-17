package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*
import java.net.URL


fun httpConnect(txt: TextView) {
    txt.text = try { URL("http://www.example.com/").readText() } catch (e: Exception) { e.toString() }
}

fun httpsConnect(txt: TextView) {
    txt.text = try { URL("https://tls13.1d.pw/").readText() } catch (e: Exception) { e.toString() }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt = findViewById<TextView>(R.id.txtview)
        txt.text = "0"

        val btn_http = findViewById<Button>(R.id.btn_http)
        btn_http.setOnClickListener(){
            CoroutineScope(Dispatchers.Default).async { httpConnect(txt = txt) }
        }

        val btn_https = findViewById<Button>(R.id.btn_https)
        btn_https.setOnClickListener(){
            CoroutineScope(Dispatchers.Default).async { httpsConnect(txt = txt) }
        }
    }
}
