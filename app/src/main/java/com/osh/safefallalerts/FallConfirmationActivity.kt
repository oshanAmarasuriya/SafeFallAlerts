package com.osh.safefallalerts

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.osh.safefallalerts.ui.theme.SafeFallAlertsTheme

private lateinit var alertDialog: AlertDialog
private var countdown = 10
private val handler = Handler(Looper.getMainLooper())
private lateinit var countdownRunnable: Runnable


class FallConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AlertDialog.Builder(this)
            .setTitle("Fall Detected")
            .setMessage("Did you fall?")
            .setPositiveButton("Yes") { _, _ ->
                // TODO: Handle actual fall confirmed
                //Toast.makeText(this, "Emergency response activated", Toast.LENGTH_SHORT).show()
                handleConfirmedFall()
                //finish()
            }
            .setNegativeButton("No") { _, _ ->
                // TODO: False alarm
                Toast.makeText(this, "False alarm", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setCancelable(false)
            .show()


        /*
        countdown = 10
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Fall Detected")
        builder.setMessage("Did you fall? (Auto-confirming in $countdown seconds)")
        builder.setCancelable(false)

        builder.setPositiveButton("Yes") { _, _ ->
            handleConfirmedFall()
        }

        builder.setNegativeButton("No") { _, _ ->
            handleFalseAlarm()
        }

        alertDialog = builder.create()
        alertDialog.show()

        // Start countdown updates
        startCountdown()

         */
    }


    private fun startCountdown() {
        countdownRunnable = object : Runnable {
            override fun run() {
                countdown--
                alertDialog.setMessage("Did you fall? (Auto-confirming in $countdown seconds)")
                if (countdown > 0) {
                    handler.postDelayed(this, 1000)
                } else {
                    alertDialog.dismiss()
                    handleConfirmedFall()
                }
            }
        }
        handler.post(countdownRunnable)
    }

    private fun handleConfirmedFall() {
        getLastKnownLocation(applicationContext) { lat, lon ->
            if (lat != null && lon != null) {
                Toast.makeText(this, "Emergency response activated. lat is $lat and lon is $lon", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Emergency response activated. BUT NO LOCATION DATA", Toast.LENGTH_LONG).show()
            }
        }

//        Toast.makeText(this, "Emergency response activated", Toast.LENGTH_LONG).show()
        // TODO: Replace with emergency logic (e.g., send SMS, share location, etc.)
        finish()
    }

    private fun handleFalseAlarm() {
        Toast.makeText(this, "False alarm", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(countdownRunnable)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(countdownRunnable)
        //countdown=10
    }

}

