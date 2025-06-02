package com.osh.safefallalerts

import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import android.telephony.SmsManager
import kotlinx.coroutines.*
import com.osh.safefallalerts.db.ContactDao
import com.osh.safefallalerts.db.ContactDatabase
import com.osh.safefallalerts.utils.getLastKnownLocation

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
            .setTitle("Possible Fall/Emergency Detected")
            .setMessage("Are you in an emergency??")
            .setPositiveButton("Yes") { _, _ ->
                handleConfirmedFall()
                //finish()
            }
            .setNegativeButton("No") { _, _ ->
                Toast.makeText(this, "False alarm!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setCancelable(false)
            .show()

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

    private fun sendLocationToAllContacts(latitude: Double, longitude: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val contacts = dao.getAll()
            withContext(Dispatchers.Main) {
                val message = "An emergency detected! Location: https://maps.google.com/?q=$latitude,$longitude"

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
    }

}

