package motocitizen.geo

import android.content.Context
import android.location.Location
import android.os.Bundle
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import motocitizen.datasources.preferences.Preferences
import motocitizen.permissions.Permissions
import motocitizen.utils.toLatLng

object MyGoogleApiClient {
    private var client: GoogleApiClient? = null
    private var delayedJob: () -> Unit = {}

    private fun isConnected() = client?.isConnected ?: false

    fun initialize(context: Context) {
        client = GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallback())
                .addApi(LocationServices.API).build()
        client?.connect()
    }

    fun getLastLocation(): LatLng {
        if (client == null || !Permissions.locationEnabled) return Preferences.savedLatLng
        return LocationServices.FusedLocationApi.getLastLocation(client)?.toLatLng() ?: Preferences.savedLatLng
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