package co.edu.unal.reto8

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import co.edu.unal.reto8.contact.Contact
import co.edu.unal.reto8.contact.ContactListAdapter.Companion.EXTRA_CURRENT

class EditContactActivity : AppCompatActivity() {

    private lateinit var editNameView : EditText
    private lateinit var editURLView : EditText
    private lateinit var editPhoneView : EditText
    private lateinit var editEmailView : EditText
    private lateinit var editProductsView : EditText
    private lateinit var editTypeView : EditText

    private var id : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_contact)


        val contact: Contact? = intent.getSerializableExtra(EXTRA_CURRENT) as? Contact


        id = contact?.id ?: 0
        editNameView = findViewById(R.id.edit_name)
        editNameView.setText(contact?.name)

        editURLView = findViewById(R.id.edit_url)
        editURLView.setText(contact?.url)

        editPhoneView = findViewById(R.id.edit_phone)
        editPhoneView.setText(contact?.phone)

        editEmailView = findViewById(R.id.edit_email)
        editEmailView.setText(contact?.email)

        editProductsView = findViewById(R.id.edit_products)
        editProductsView.setText(contact?.products)

        editTypeView = findViewById(R.id.edit_type)
        editTypeView.setText(contact?.type)


        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            validateContact()
        }
    }


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
        else if (TextUtils.isEmpty(editTypeView.text)) {
            setResult(Activity.RESULT_CANCELED, replyIntent)
        }
        else {
            val name = editNameView.text.toString()
            val url = editURLView.text.toString()
            val phone = editPhoneView.text.toString()
            val email = editEmailView.text.toString()
            val products = editProductsView.text.toString()
            val type = editTypeView.text.toString()


            val newContact = Contact(
                id = id,
                name = name,
                url = url,
                phone = phone,
                email = email,
                products = products,
                type = type
            )
            replyIntent.putExtra(EditContactActivity.EXTRA_EDIT_REPLY, newContact)
            setResult(Activity.RESULT_OK, replyIntent)
            Log.v("TAAAAAAAAAAAAAAAAAG", "Devolviendo, al parecer")

        }
        finish()
    }

    companion object {
        const val EXTRA_EDIT_REPLY = "com.example.android.wordlistsql.EDIT_REPLY"
    }
}