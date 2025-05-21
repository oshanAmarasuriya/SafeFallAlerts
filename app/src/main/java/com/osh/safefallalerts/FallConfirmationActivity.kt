package com.osh.safefallalerts

import android.app.AlertDialog
import android.os.Bundle
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

class FallConfirmationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AlertDialog.Builder(this)
            .setTitle("Fall Detected")
            .setMessage("Did you fall?")
            .setPositiveButton("Yes") { _, _ ->
                // TODO: Handle actual fall confirmed
                Toast.makeText(this, "Emergency response activated", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setNegativeButton("No") { _, _ ->
                // TODO: False alarm
                Toast.makeText(this, "False alarm", Toast.LENGTH_SHORT).show()
                finish()
            }
            .setCancelable(false)
            .show()
    }
}

