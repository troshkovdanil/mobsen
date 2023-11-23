package com.example.myapplication

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import java.net.URL
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection


fun httpConnect(txt: TextView) {
    txt.text = try { URL("http://www.example.com/").readText() } catch (e: Exception) { e.toString() }
}
@Throws(IOException::class)
private fun encodeParams(params: JSONObject): String? {
    val result = StringBuilder()
    var first = true
    val itr = params.keys()
    while (itr.hasNext()) {
        val key = itr.next()
        val value = params[key]
        if (first) first = false else result.append("&")
        result.append(URLEncoder.encode(key, "UTF-8"))
        result.append("=")
        result.append(URLEncoder.encode(value.toString(), "UTF-8"))
    }
    return result.toString()
}

fun httpsConnect(txt: TextView) {
    //txt.text = try { URL("https://tls13.1d.pw/").readText() } catch (e: Exception) { e.toString() }
    //txt.text = try { URL("https://tls13.akamai.io/").readText() } catch (e: Exception) { e.toString() }
    val d = try { URL("https://ptsv3.com/t/gps_dt/d/").readText() } catch (e: Exception) { e.toString() }
    if (d != "[]")
        return
    val postDataParams = JSONObject()
    postDataParams.put("AuthUsername", "")
    postDataParams.put("AuthPassword", "")
    postDataParams.put("ResponseCode", "200")
    postDataParams.put("ResponseBody", txt.text)
    postDataParams.put("ResponseDelay", 0)
    val url = URL("https://ptsv3.com/t/gps_dt/edit/")
    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
    conn.readTimeout = 3000
    conn.connectTimeout = 3000
    conn.requestMethod = "POST"
    conn.doInput = true
    conn.doOutput = true
    val os: OutputStream = conn.outputStream
    val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
    writer.write(encodeParams(postDataParams))
    writer.flush()
    writer.close()
    os.close()
    txt.text = "Posting..."
    val responseCode: Int = conn.responseCode // To Check for 200
    txt.text = responseCode.toString()
    if (responseCode == HttpsURLConnection.HTTP_OK) {
        txt.text = "Posting...OK"
    }
}

class MainActivity : ComponentActivity(), LocationListener {
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //(getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()

        val txt = findViewById<TextView>(R.id.txtview)
        txt.movementMethod = ScrollingMovementMethod()
        txt.text = ""

        val btn_gps = findViewById<Button>(R.id.btn_gps)
        btn_gps.setOnClickListener(){
            getLocation()
        }

        val btn_https = findViewById<Button>(R.id.btn_https)
        btn_https.setOnClickListener(){
            CoroutineScope(Dispatchers.Default).async { httpsConnect(txt = txt) }
        }

        val btn_clear = findViewById<Button>(R.id.btn_clear)
        //var cntr = 0
        btn_clear.setOnClickListener(){
            txt.text = ""
            /*
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
            */
        }
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }
    override fun onLocationChanged(location: Location) {
        val txt = findViewById<TextView>(R.id.txtview)
        txt.append(location.time.toString() + "," +
                   location.latitude.toString() + "," +
                   location.longitude.toString() + "\n")
    }

    /*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == locationPermissionCode) {
            val txt = findViewById<TextView>(R.id.txtview)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                txt.text = "Permission Granted"
            } else {
                txt.text = "Permission Denied"
            }
        }
    }
    */
}
