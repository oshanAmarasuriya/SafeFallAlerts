package com.osh.safefallalerts

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.telephony.SmsManager
import kotlinx.coroutines.*
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.osh.safefallalerts.db.ContactDao
import com.osh.safefallalerts.db.ContactDatabase
import com.osh.safefallalerts.ui.theme.SafeFallAlertsTheme

private lateinit var alertDialog: AlertDialog
private var countdown = 10
private val handler = Handler(Looper.getMainLooper())
private lateinit var countdownRunnable: Runnable


class FallConfirmationActivity : ComponentActivity() {
    private lateinit var db: ContactDatabase
    private lateinit var dao: ContactDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = ContactDatabase.getDatabase(this)
        dao = db.contactDao()

        AlertDialog.Builder(this)
            .setTitle("Fall Detected")
            .setMessage("Did you fall?")
            .setPositiveButton("Yes") { _, _ ->
                handleConfirmedFall()
                //finish()
            }
            .setNegativeButton("No") { _, _ ->
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
                //Toast.makeText(this, "Emergency response activated. lat is $lat and lon is $lon", Toast.LENGTH_LONG).show()
                sendLocationToAllContacts(lat,lon)
            } else {
                Toast.makeText(this, "Emergency response activated. BUT NO LOCATION DATA", Toast.LENGTH_LONG).show()
            }
        }
        finish()
    }

    private fun handleFalseAlarm() {
        Toast.makeText(this, "False alarm", Toast.LENGTH_SHORT).show()
        handler.removeCallbacks(countdownRunnable)
        finish()
    }

    private fun sendLocationToAllContacts(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = dao.getAll() // assuming dao is your ContactDao
            withContext(Dispatchers.Main) {
                val message = "Fall detected! Location: https://maps.google.com/?q=$latitude,$longitude"

                try {
                    val smsManager = SmsManager.getDefault()
                    for (contact in contacts) {
                        smsManager.sendTextMessage(contact.phoneNumber, null, message, null, null)
                    }
                    Toast.makeText(this@FallConfirmationActivity, "Alert sent to contacts", Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(this@FallConfirmationActivity, "Failed to send SMS: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(countdownRunnable)
        //countdown=10
    }

}

