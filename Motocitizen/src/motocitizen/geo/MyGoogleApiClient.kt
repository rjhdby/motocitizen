package motocitizen.geo

import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import motocitizen.utils.toLocation

object MyGoogleApiClient {
    private var client: GoogleApiClient? = null
    private var delayedJob: () -> Unit = {}

    fun isConnected() = client?.isConnected.let { false }

    fun initialize(context: Context) {
        client = GoogleApiClient.Builder(context).addConnectionCallbacks(connectionCallback()).addApi(LocationServices.API).build()
        client?.connect()
    }

    fun getLastLocation(): Location? {
        if (client == null) return motocitizen.datasources.preferences.Preferences.savedLatLng.toLocation()
        return LocationServices.FusedLocationApi.getLastLocation(client)
    }

    fun runLocationService(locationRequest: LocationRequest, locationListener: (Location) -> Unit) {
        queryJob {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, locationListener)
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, locationListener)
        }
    }

    private fun queryJob(job: () -> Unit) {
        if (isConnected()) job()
        else delayedJob = job
    }

    private fun connectionCallback(): GoogleApiClient.ConnectionCallbacks {
        return object : GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(connectionHint: Bundle?) {
                delayedJob()
            }

            override fun onConnectionSuspended(arg0: Int) {}
        }
    }
}