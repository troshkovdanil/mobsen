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
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import android.widget.ToggleButton
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection
import android.net.Uri
import android.content.Intent
import android.provider.MediaStore;



class MainActivity : ComponentActivity(), LocationListener {
    val REQUEST_VIDEO_CAPTURE = 123
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt = findViewById<TextView>(R.id.txtview)
        txt.movementMethod = ScrollingMovementMethod()
        txt.text = ""

        val btn_video = findViewById<Button>(R.id.btn_video)
        btn_video.setOnClickListener() {
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE)
        }

        val btn_reset_permissions = findViewById<Button>(R.id.btn_reset_permissions)
        btn_reset_permissions.setOnClickListener() {
            (getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = intent?.data
            val videoView = findViewById<VideoView>(R.id.video_view)
            videoView.setVideoURI(videoUri)
            videoView.requestFocus()
            videoView.start()
        }
    }

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionCode)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        val telemetry = location.time.toString() + "," +
                location.latitude.toString() + "," +
                location.longitude.toString() + "\n"
        val txt = findViewById<TextView>(R.id.txtview)
        txt.text = telemetry
    }

    private lateinit var gpsJob: Job
    fun onGPSToggleClicked(view: View) {
        val on = (view as ToggleButton).isChecked
        if (on) {
            gpsJob = CoroutineScope(Dispatchers.Main,).launch {
                while (true) {
                    yield()
                    val txt = findViewById<TextView>(R.id.txtview)
                    val telemetry = txt.text.toString()
                    if (telemetry == "") {
                        getLocation()
                    }
                }
            }
        } else {
            gpsJob.cancel()
        }
    }

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
    private fun postTelemetry(txt: TextView, telemetry: String) {
        val postDataParams = JSONObject()
        postDataParams.put("AuthUsername", "")
        postDataParams.put("AuthPassword", "")
        postDataParams.put("ResponseCode", "200")
        postDataParams.put("ResponseBody", telemetry)
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
        val responseCode: Int = conn.responseCode
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            txt.text = ""
        }
    }

    private lateinit var httpsJob: Job
    fun onHTTPSToggleClicked(view: View) {
        val on = (view as ToggleButton).isChecked
        if (on) {
            httpsJob = CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    yield()
                    val txt = findViewById<TextView>(R.id.txtview)
                    val telemetry = txt.text.toString()
                    if (telemetry == "") {
                        continue
                    }
                    val dump = try {
                        URL("https://ptsv3.com/t/gps_dt/d/").readText()
                    } catch (e: Exception) {
                        e.toString()
                    }
                    if (dump == "[]") {
                        postTelemetry(txt, telemetry)
                    }
                }
            }
        } else {
            httpsJob.cancel()
        }
    }
}
