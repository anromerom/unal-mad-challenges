package co.edu.unal.reto8

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import co.edu.unal.reto8.contact.Contact

class NewContactActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var editNameView :EditText
    private lateinit var editURLView :EditText
    private lateinit var editPhoneView :EditText
    private lateinit var editEmailView :EditText
    private lateinit var editProductsView :EditText
    private lateinit var editTypeView :Spinner



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_contact)

        editNameView = findViewById(R.id.edit_name)
        editURLView = findViewById(R.id.edit_url)
        editPhoneView = findViewById(R.id.edit_phone)
        editEmailView = findViewById(R.id.edit_email)
        editProductsView = findViewById(R.id.edit_products)
        editTypeView = findViewById(R.id.edit_type)

        val adapter =  ArrayAdapter.createFromResource(
            this,
             R.array.types,
            android.R.layout.simple_spinner_dropdown_item)
         adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        editTypeView.adapter = adapter

        editTypeView.setOnItemSelectedListener(this);


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            validateContact()
        }
    }

    /*
        editURLView
        editPhoneView
        editEmailView
        editProductsView
        editTypeView

        */
    var spinnerType : String = ""

    fun validateContact(){
        val replyIntent = Intent()
        if (TextUtils.isEmpty(editNameView.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        }
        else if (TextUtils.isEmpty(editURLView.text)) {
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }
        else if (TextUtils.isEmpty(editPhoneView.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        }
        else if (TextUtils.isEmpty(editEmailView.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        }
        else if (TextUtils.isEmpty(editProductsView.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        }
        else {
            val name = editNameView.text.toString()
            val url = editURLView.text.toString()
            val phone = editPhoneView.text.toString()
            val email = editEmailView.text.toString()
            val products = editProductsView.text.toString()
            val type = spinnerType


            val contact: Contact = Contact(
                name = name,
                url = url,
                phone = phone,
                email = email,
                products = products,
                type = type
                )
            replyIntent.putExtra(EXTRA_REPLY, contact)
            setResult(Activity.RESULT_OK, replyIntent)
        }
        finish()
    }



    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        Log.i("TAAAAAAAAAAG, ", parent.getItemAtPosition(pos).toString())
        spinnerType = parent.getItemAtPosition(pos).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        spinnerType = parent.getItemAtPosition(0).toString()
    }


}
