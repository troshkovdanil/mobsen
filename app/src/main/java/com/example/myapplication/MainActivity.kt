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


fun httpConnect(txt: TextView) {
    txt.text = try { URL("http://www.example.com/").readText() } catch (e: Exception) { e.toString() }
}

fun httpsConnect(txt: TextView) {
    //txt.text = try { URL("https://tls13.1d.pw/").readText() } catch (e: Exception) { e.toString() }
    txt.text = try { URL("https://tls13.akamai.io/").readText() } catch (e: Exception) { e.toString() }
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

        val btn_test = findViewById<Button>(R.id.btn_clear)
        //var cntr = 0
        btn_test.setOnClickListener(){
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
        txt.append(location.latitude.toString() + "," + location.longitude.toString() + "\n")
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
