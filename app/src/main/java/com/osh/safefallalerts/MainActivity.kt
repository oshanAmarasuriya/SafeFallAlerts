package com.osh.safefallalerts

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.osh.safefallalerts.db.Contact
import com.osh.safefallalerts.db.ContactDao
import com.osh.safefallalerts.db.ContactDatabase
import com.osh.safefallalerts.utils.ContactAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    private lateinit var db: ContactDatabase
    private lateinit var dao: ContactDao
    private lateinit var adapter: ContactAdapter
    private val PREF_KEY = "sensitivity_value"
    private val SERVICE_RUNNING_KEY = "service_running"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = ContactDatabase.getDatabase(this)
        dao = db.contactDao()

        val startButton: Button = findViewById(R.id.btn_start)
        val stopButton: Button = findViewById(R.id.btn_stop)
        val sensitivitySeekBar = findViewById<SeekBar>(R.id.sensitivitySeekBar)
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        val nameEditText: EditText = findViewById(R.id.edit_name)
        val phoneEditText: EditText = findViewById(R.id.edit_phone)
        val addButton: Button = findViewById(R.id.btn_add_contact)

        // Load sensitivity and service state
        val savedValue = sharedPreferences.getInt(PREF_KEY, 50)
        sensitivitySeekBar.progress = savedValue

        val serviceRunning = sharedPreferences.getBoolean(SERVICE_RUNNING_KEY, false)
        sensitivitySeekBar.isEnabled = !serviceRunning

        startButton.setOnClickListener {
            val intent = Intent(this, SensorService::class.java)
            ContextCompat.startForegroundService(this, intent)
            //update service running status
            sharedPreferences.edit().putBoolean(SERVICE_RUNNING_KEY, true).apply()
            sensitivitySeekBar.isEnabled = false
            Toast.makeText(this, "Tracking started", Toast.LENGTH_SHORT).show()
        }

        stopButton.setOnClickListener {
            val intent = Intent(this, SensorService::class.java)
            stopService(intent)
            sharedPreferences.edit().putBoolean(SERVICE_RUNNING_KEY, false).apply()
            sensitivitySeekBar.isEnabled = true
            Toast.makeText(this, "Tracking stopped", Toast.LENGTH_SHORT).show()
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ),
            1001
        )

        val recyclerView: RecyclerView = findViewById(R.id.recycler_contacts)
        adapter = ContactAdapter(listOf())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fun loadContacts() {
            CoroutineScope(Dispatchers.IO).launch {
                val contacts = dao.getAll()
                withContext(Dispatchers.Main) {
                    adapter.updateData(contacts)
                }
            }
        }

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()

            if (name.isNotBlank() && phone.isNotBlank()) {
                val contact = Contact(name = name, phoneNumber = phone)

                CoroutineScope(Dispatchers.IO).launch {
                    dao.insert(contact)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Contact added!", Toast.LENGTH_SHORT).show()
                        nameEditText.text.clear()
                        phoneEditText.text.clear()
                        loadContacts()
                    }
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
        loadContacts()

        sensitivitySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Map 0–100 to a threshold range, e.g., 0.5 to 3.0
                val sensitivityThreshold = 0.5f + (progress / 100f) * 2.5f
                // Save the threshold to SharedPreferences
                with(sharedPreferences.edit()) {
                    putFloat("sensitivity_threshold", sensitivityThreshold)
                    putInt(PREF_KEY, progress)
                    apply()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}
