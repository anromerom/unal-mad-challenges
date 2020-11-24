package co.edu.unal.reto10

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider
import java.util.*

class ParamsActivity : AppCompatActivity() {


    private lateinit var mSearchET: EditText
    private lateinit var mAuthorityET: EditText
    private lateinit var mStationET:  EditText
    private lateinit var mDepartmentET:  EditText
    private lateinit var mTownET:  EditText
    private lateinit var mVariableET:  EditText
    private lateinit var mLimitSlider:  Slider
    private lateinit var mSubmitButton:  FloatingActionButton


    private var date: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_params)


        mSearchET = findViewById(R.id.search_edit_text)
        mAuthorityET = findViewById(R.id.authority_edit_text)
        mStationET = findViewById(R.id.station_edit_text)
        mDepartmentET = findViewById(R.id.department_edit_text)
        mTownET = findViewById(R.id.town_edit_text)
        mVariableET = findViewById(R.id.variable_edit_text)


        mLimitSlider = findViewById(R.id.limit_slider)

        mSubmitButton = findViewById(R.id.submit_button)

        mSubmitButton.setOnClickListener {

            validateForm()

        }

    }

    private fun validateForm() {


        val replyIntent = Intent()
        // 1. Búsqueda
        val search = mSearchET.text.toString()
        replyIntent.putExtra("SEARCH", search)
        // 2. Autoridad ambiental
        val authority = mAuthorityET.text.toString()
        replyIntent.putExtra("AUTHORITY", authority)
        // 3. Nombre de la estación
        val station = mStationET.text.toString()
        replyIntent.putExtra("STATION", station)
        // 4. Departamento
        val department = mDepartmentET.text.toString()
        replyIntent.putExtra("DEPARTMENT", department)
        // 5. Municipio
        val town = mTownET.text.toString()
        replyIntent.putExtra("TOWN", town)
        // 6. Variable
        val variable = mVariableET.text.toString()
        replyIntent.putExtra("VARIABLE", variable)
        // 7. Limite
        val limit = mLimitSlider.value
        replyIntent.putExtra("LIMIT", limit)

        setResult(RESULT_OK, replyIntent)
        finish()

    }






}