package co.edu.unal.reto10

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var mParamsButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        mParamsButton = findViewById(R.id.param_button)

        mParamsButton.setOnClickListener {

            val intent = Intent(this@MapsActivity, ParamsActivity::class.java)
            startActivityForResult(intent, PARAMS_ACTIVITY_REQUEST_CODE)

        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        val marker = MarkerOptions().position(sydney).title("Marker in Sydney")
        mMap.addMarker(marker)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))


        mMap.setOnMarkerClickListener {         // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
            showMyDialog(it.title, it.snippet)
            false
        }


    }


    private fun showMyDialog(title: String?, desc: String?){
        val builder: AlertDialog.Builder? = this?.let {
            AlertDialog.Builder(it)
        }

// 2. Chain together various setter methods to set the dialog characteristics
        builder?.setMessage(desc ?: "No description")
                ?.setTitle(title ?: "No title")
                ?.setCancelable(true)

// 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        val dialog: AlertDialog? = builder?.create()
        dialog?.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PARAMS_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {


            val date = data?.getSerializableExtra("DATE") as Date? ?: Calendar.getInstance().time

            val base_url = "https://www.datos.gov.co/resource/ysq6-ri4e.json"
            var uri = Uri.parse(base_url).buildUpon()
            val paramsMap = HashMap<String, String>()
            // 1. Fecha
            val search = data?.getStringExtra("SEARCH")
            if (search != null && search.isNotEmpty()) {
                paramsMap.put("\$q", search)
            }

            // 2. Autoridad ambiental
            val authority = data?.getStringExtra("AUTHORITY")
            if (authority != null && authority.isNotEmpty()) {
                paramsMap.put("autoridad_ambiental", authority)
            }
            // 3. Nombre de la estación
            val station = data?.getStringExtra("STATION")
            if (station != null && station.isNotEmpty()) {
                paramsMap.put("nombre_de_la_estaci_n", station)
            }

            // 4. Departamento
            val department = data?.getStringExtra("DEPARTMENT")
            if (department != null && department.isNotEmpty()) {
                paramsMap.put("departamento", department)
            }


            // 5. Municipio
            val town = data?.getStringExtra("TOWN")
            if (town != null && town.isNotEmpty()) {
                paramsMap.put("nombre_del_municipio", town)
            }

            // 6. Variable
            val variable = data?.getStringExtra("VARIABLE")
            if (variable != null && variable.isNotEmpty()) {
                paramsMap.put("variable", variable)
            }


            // 7. Limite
            val limit = data?.getFloatExtra("LIMIT", 5f)




            var query = "\$limit=${limit?.toInt()?:5}"
            query
            for (key in paramsMap.keys){
                query += "&${key}=${paramsMap.get(key)}"
            }

            Log.i("PITO", query)
            val url = uri.encodedQuery(query).build().toString()

            Log.i("PITO", url)

            var position = LatLng(4.624335, -74.063644)
            val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
                    { response ->
                        //textView.text = "Response: %s".format(response.toString())
                        mMap?.clear()

                        Toast.makeText(applicationContext, "${response.length()} resultados encontrados.", Toast.LENGTH_SHORT).show()


                        for (i in 0 until response.length()) {
                            val item = response.getJSONObject(i)
                            var desc: String = ""

                            for (key in item.keys()) {
                                desc += "${key}: ${item.get(key)}\n"
                            }

                            val lat = item.getString("latitud")
                            val lng = item.getString("longitud")
                            position = LatLng(lat.toDouble(), lng.toDouble())

                            val marker = MarkerOptions()
                                    .title(item.getString("nombre_de_la_estaci_n"))
                                    .snippet(desc)
                                    .position(position)
                            mMap?.addMarker(
                                    marker
                            )

                        }

                        mMap?.animateCamera(
                                CameraUpdateFactory
                                        .newLatLngZoom(position, 9f)
                        )

                    },
                    { error ->
                        Log.e("NOOOOOOOOOO", error.toString())
                    }
            )
            Volley.newRequestQueue(this).add(jsonObjectRequest);


        }

    }




    companion object {

        const val PARAMS_ACTIVITY_REQUEST_CODE = 1

    }


}