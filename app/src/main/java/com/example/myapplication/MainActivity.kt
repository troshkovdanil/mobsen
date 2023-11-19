package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.*
import java.net.URL
import android.text.method.ScrollingMovementMethod


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
        txt.movementMethod = ScrollingMovementMethod()
        txt.text = "0"

        val btn_http = findViewById<Button>(R.id.btn_http)
        btn_http.setOnClickListener(){
            CoroutineScope(Dispatchers.Default).async { httpConnect(txt = txt) }
        }

        val btn_https = findViewById<Button>(R.id.btn_https)
        btn_https.setOnClickListener(){
            CoroutineScope(Dispatchers.Default).async { httpsConnect(txt = txt) }
        }

        val btn_test = findViewById<Button>(R.id.btn_test)
        var cntr = 0
        btn_test.setOnClickListener(){
            val text: String = txt.text.toString()
            var start = 0
            var end = 0
            var is_num = true
            do {
                end = text.indexOf("\n", startIndex = start)
                if (end != -1) {
                    is_num = text.substring(start,end).matches(Regex("[0-9]*"))
                    if (!is_num) {
                        cntr = 0;
                        txt.text = "0"
                        break
                    }
                    start = end + 1
                }
            } while (end != -1)
            if (is_num) {
                cntr++
                txt.append("\n")
                txt.append(cntr.toString())
            }
        }
    }
}
