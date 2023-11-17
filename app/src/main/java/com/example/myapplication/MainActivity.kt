package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*
import java.net.URL


fun myConnect(txt: TextView) {
    txt.text = try { URL("http://www.example.com/").readText() } catch (e: Exception) { e.toString() }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt = findViewById<TextView>(R.id.txtview)
        txt.text = "0"

        val btn = findViewById<Button>(R.id.btn)
        btn.setOnClickListener(){
            CoroutineScope(Dispatchers.Default).async { myConnect(txt = txt) }
        }
    }
}
