package com.osh.safefallalerts.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission") // You must still request permissions at runtime
fun getLastKnownLocation(context: Context, callback: (latitude: Double?, longitude: Double?) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            if (location != null) {
                callback(location.latitude, location.longitude)
            } else {
                callback(null, null)
            }
        }
        .addOnFailureListener { e ->
            Log.e("LocationError", "Failed to get location", e)
            callback(null, null)
        }
}
