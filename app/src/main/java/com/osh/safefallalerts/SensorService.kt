package com.osh.safefallalerts

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.math.sqrt

class SensorService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var lastFallTimestamp: Long = 0L

    private var gyroRotationRate = 0f
    private var sensitivityThreshold = 2.0f // Default, adjustable from UI

    override fun onBind(intent: Intent): IBinder ? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_FASTEST)
        sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_FASTEST)

        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        sensitivityThreshold = sharedPreferences.getFloat("sensitivity_threshold", 2.0f) // Default if not set

        startForeground(1, createNotification())
    }

    override fun onSensorChanged(event: SensorEvent?) {

        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = it.values[0]
                    val y = it.values[1]
                    val z = it.values[2]

                    val accMagnitude = sqrt(x * x + y * y + z * z)

                    // Combine accelerometer + gyroscope for better accuracy
                    if (accMagnitude < sensitivityThreshold && gyroRotationRate > 3.0f) {
                        val now = System.currentTimeMillis()
                        if (now - lastFallTimestamp > 1000) {
                            lastFallTimestamp = now
                            Log.d("FallDetection", "Possible fall detected! Acc mag: $accMagnitude, Gyro: $gyroRotationRate")

                            Handler(Looper.getMainLooper()).post {
                                makeVibration()
                            }

                            val intent = Intent(this, FallConfirmationActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                            startActivity(intent)
                        }
                    }
                }

                Sensor.TYPE_GYROSCOPE -> {
                    val rx = it.values[0]
                    val ry = it.values[1]
                    val rz = it.values[2]
                    gyroRotationRate = sqrt(rx * rx + ry * ry + rz * rz)
                }
            }


//            if (it.sensor.type == Sensor.TYPE_ACCELEROMETER) {
//                val x = it.values[0]
//                val y = it.values[1]
//                val z = it.values[2]
//
//                val magnitude = sqrt(x * x + y * y + z * z)
//
//                if (magnitude < 2.0f) { // possible free fall
//                    val now = System.currentTimeMillis()
//                    if (now - lastFallTimestamp > 1000) {
//                        lastFallTimestamp = now
//                        Log.d("FallDetection", "Possible fall detected! Acc mag: $magnitude")
//                        // Trigger alert, vibration, etc.
//                        Handler(Looper.getMainLooper()).post {
//                            makeVibration()
//                        }
//                        // Start new activity
//                        val intent = Intent(this, FallConfirmationActivity::class.java).apply {
//                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
//                        }
//                        startActivity(intent)
//
//                    }
//                }
//            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }

    private fun createNotification(): Notification {
        val notificationChannelId = "sensor_service_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                notificationChannelId,
                "Sensor Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(chan)
        }

        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Fall Detection Active")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
    }

    @SuppressLint("ServiceCast")
    fun Context.makeVibration() {

        // Vibrate
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    500, // duration in ms
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }




    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

}