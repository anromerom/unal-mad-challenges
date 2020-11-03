package co.edu.unal.reto8

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import co.edu.unal.reto8.contact.Contact
import co.edu.unal.reto8.contact.ContactListAdapter
import co.edu.unal.reto8.contact.ContactViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var  contactViewModel: ContactViewModel

    val newContactActivityRequestCode = 1
    val modifiedContactActivityRequestCode = 2

    private lateinit var spinner : Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)

        val deleteContact = {
            contact: Contact ->
            contactViewModel.delete(contact)
            Unit

        }

        val adapter = ContactListAdapter(this, deleteContact)


        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        contactViewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

        contactViewModel.allContacts.observe(this, Observer { contacts ->
            contacts?.let { adapter.setContacts(it) }
        })

        //Spinner view

        spinner = findViewById(R.id.filter_type)

        val spinnerAdapter =  ArrayAdapter.createFromResource(
            this,
            R.array.all_types,
            android.R.layout.simple_spinner_dropdown_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.setOnItemSelectedListener(this);




        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewContactActivity::class.java)
            startActivityForResult(intent, newContactActivityRequestCode)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newContactActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(NewContactActivity.EXTRA_REPLY)?.let {
                val contact: Contact? = it as Contact
                if(contact != null) contactViewModel.insert(contact)
            }
        }
        else if (requestCode == modifiedContactActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getSerializableExtra(EditContactActivity.EXTRA_EDIT_REPLY)?.let {
                val contact: Contact? = it as Contact
                if(contact != null) contactViewModel.update(contact)
            }
        }

        else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }







    // Extension function to show toast message
    fun Context.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        contactViewModel.searchByType(parent.getItemAtPosition(pos).toString())
    }

    override fun onNothingSelected(p0: AdapterView<*>) {
        TODO("Not yet implemented")
    }


}