package co.edu.unal.reto09

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.koushikdutta.ion.Ion
import org.json.JSONArray
import java.util.concurrent.ExecutionException
import kotlin.math.ln


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private var map: GoogleMap? = null
    private var cameraPosition: CameraPosition? = null

    // The entry point to the Places API.
    private lateinit var placesClient: PlacesClient

    // The entry point to the Fused Location Provider.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    private var locationPermissionGranted = false

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var lastKnownLocation: Location? = null
    private var selectedPlace: Place? = null

    private var radius = 0

    //Search view
    private lateinit var searchView: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION)
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION)
        }

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps)

        // Construct a PlacesClient
        Places.initialize(
            applicationContext,
            getString(R.string.maps_api_key)
        )

        placesClient = Places.createClient(this)

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        // Build the map.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


        // Search view
        searchView = findViewById(R.id.search_location)
        searchView.isFocusable = false
        searchView.setOnClickListener {

            val fieldList =  listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME)
            val intent: Intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                fieldList
            ).build(this@MapsActivity)
            startActivityForResult(intent, 100)

        }


        radius =  setupSharedPreferences()?.getString("radius", "200")?.toInt() ?: 500

        Log.i("RADIUS", "RADIO CREADO CON VALOR ${radius}")
    }


    private fun setupSharedPreferences(): SharedPreferences? {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        return sharedPreferences
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK){

            val place = data?.let { Autocomplete.getPlaceFromIntent(it) }
            searchView.setText(place?.address)
            selectedPlace = place

            locationType = "query"

            place?.latLng?.let {selectCurrentPlace(it)}
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * Sets up the options menu.
     * @param menu The options menu.
     * @return Boolean.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.current_place_menu, menu)
        return true
    }

    /**
     * Handles a click on the menu option to get a place.
     * @param item The menu item to handle.
     * @return Boolean.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.option_settings) {
            val intent = Intent(this@MapsActivity, SettingsActivity::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

     override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String){
        if (key == "radius") {
            Log.i("RADIUS", key)
            Log.i("RADIUS", sharedPreferences.getString("radius", "NO SE ENCUENTRA") ?: "NOOOOOOO SE ENCUENTRA")
            radius = sharedPreferences.getString("radius", "200")?.toInt() ?: 500
            Log.i("RADIUS", radius.toString())

            selectCurrentPlace()
        }

         Log.i("RADIUS", key)
    }


    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)

    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    override fun onMapReady(map: GoogleMap) {
        this.map = map

        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.

        map.clear()
        this.map?.setInfoWindowAdapter(object : InfoWindowAdapter {
            // Return null here, so that getInfoContents() is called next.
            override fun getInfoWindow(arg0: Marker): View? {
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                // Inflate the layouts for the info window, title and snippet.
                val infoWindow = layoutInflater.inflate(
                    R.layout.custom_info_contents,
                    findViewById<FrameLayout>(R.id.map), false
                )
                val title = infoWindow.findViewById<TextView>(R.id.title)
                title.text = marker.title
                val snippet = infoWindow.findViewById<TextView>(R.id.snippet)
                snippet.text = marker.snippet
                return infoWindow
            }
        })


        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_map))

        // Prompt the user for permission.
        getLocationPermission()

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI()

        // Get the current location of the device and set the position of the map.
        getDeviceLocation()

        selectCurrentPlace()
    }


    var locationType = "local"
    private fun selectCurrentPlace(latLng: LatLng? = null) {
        if (locationType == "local"){

            lastKnownLocation?.let { selectSearchPlace(LatLng(it.latitude, it.longitude)) }
        }

        else if (locationType == "query"){
            latLng?.let {selectSearchPlace(it)}        }

    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            map?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.")
                        Log.e(TAG, "Exception: %s", task.exception)
                        map?.animateCamera(
                            CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat())
                        )
                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun getZoomLevel(circle: Circle?): Float {
        var zoomLevel = 11f

        if(circle != null) {
            val radius = circle.radius + circle.radius / 2
            val scale = radius / 500
            zoomLevel = (16f - ln(scale) / ln(2.0)).toFloat()
        }
        return zoomLevel
    }

    private fun selectSearchPlace(location: LatLng){
        if (map == null) {
            return
        }
        map?.clear()

        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location=" +
                location.latitude +
                "," +
                location.longitude +
                "&" +
                "radius=" +
                radius.toString() +
                "&" +
                "key=" +
                getString(R.string.maps_api_key)

        Log.i("OOOOOOOOOOOOOOOOOO", url)
        val circle = drawCircle(location)


        map?.animateCamera(
            CameraUpdateFactory
                .newLatLngZoom(location, getZoomLevel(circle))
        )

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val results = response.getJSONArray("results")
                Log.i("OOOOOOOOOOOOOOOOOO", response.toString())
                for (i in 0 until results.length()) {
                    val result = results.getJSONObject(i)
                    val lat = result.getJSONObject("geometry").getJSONObject("location").getDouble(
                        "lat"
                    )
                    val lng = result.getJSONObject("geometry").getJSONObject("location").getDouble(
                        "lng"
                    )
                    Log.i("OOOOOOOOOOOOOOOOOO", result.getString("name"))

                    val type =
                        if (result.has("types")) stringFromTypes(result.getJSONArray("types")) else ""
                    var desc =
                        if (result.has("vicinity")) "${result.getString("vicinity")}\n${type}" else type


                    val distances = FloatArray(1)
                    Location.distanceBetween(location.latitude, location.longitude, lat, lng, distances)
                    desc+= "\nDistance: ${distances[0]} m"


                    map?.addMarker(
                        MarkerOptions()
                            .title(result.getString("name"))
                            .snippet(desc)
                            .position(LatLng(lat, lng))
                    )?.let { marker ->
                        iconFromURL(marker, result.getString("icon"))
                    }
                }
            },
            { error ->
                Log.e("VOLLEY ERROR", error.toString())
            }

        )

        Volley.newRequestQueue(this@MapsActivity).add(jsonObjectRequest)
    }

    private fun stringFromTypes(array: JSONArray) : String {

        var s = ""
        for (i in 0 until array.length()){
            s+= "\t- ${array.get(i)}\n"
        }
        return s
    }


    private fun iconFromURL(marker: Marker, imgURL: String){

        Log.i("AAAAAAAAAAAAAAAAAAAAAAAAAA", imgURL)
        Log.i("AAAAAAAAAAAAAAAAAAAAAAAAAA", marker.toString())
        try {
            val bmp = Ion.with(this@MapsActivity)
                    .load(imgURL)
                    .asBitmap().get()

            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bmp))

        }
         catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        catch (e: AssertionError){
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        selectCurrentPlace()
    }

    private fun drawCircle(point: LatLng): Circle? {

        // Instantiating CircleOptions to draw a circle around the marker
        val circleOptions = CircleOptions()

        // Specifying the center of the circle
        circleOptions.center(point)

        // Radius of the circle
        circleOptions.radius(radius.toDouble())

        // Fill color of the circle
        circleOptions.fillColor(0x306f0020)



        // Border width of the circle
        circleOptions.strokeWidth(0.0f)

        // Adding the circle to the GoogleMap


        return  map?.addCircle(circleOptions)
    }
    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map?.isMyLocationEnabled = true
                map?.uiSettings?.isMyLocationButtonEnabled = true
                map?.setOnMyLocationButtonClickListener {
                    locationType = "local"
                    lastKnownLocation?.let { selectCurrentPlace() }
                    false
                }

            } else {
                map?.isMyLocationEnabled = false
                map?.uiSettings?.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    companion object {
        private val TAG = MapsActivity::class.java.simpleName
        private const val DEFAULT_ZOOM = 15
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1

        // Keys for storing activity state.
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"


    }
}