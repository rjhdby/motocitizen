package motocitizen.geo.geocoder

import com.google.android.gms.maps.model.LatLng
import motocitizen.datasources.preferences.Preferences

class AccidentLocation(val address: String = "", val coordinates: LatLng = Preferences.savedLatLng)