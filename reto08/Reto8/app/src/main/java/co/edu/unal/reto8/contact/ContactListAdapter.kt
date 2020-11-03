package co.edu.unal.reto8.contact

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import co.edu.unal.reto8.EditContactActivity
import co.edu.unal.reto8.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactListAdapter internal constructor(
    context: Context, callback: (contact: Contact) -> Unit
) : RecyclerView.Adapter<ContactListAdapter.ContactViewHolder>()
{

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var contacts = emptyList<Contact>()
    val modifiedContactActivityRequestCode = 2

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val nameTextView: TextView = itemView.findViewById(R.id.contact_name)
        val urlTextView: TextView = itemView.findViewById(R.id.contact_url)
        val phoneTextView: TextView = itemView.findViewById(R.id.contact_phone)
        val emailTextView: TextView = itemView.findViewById(R.id.contact_email)
        val productsTextView: TextView = itemView.findViewById(R.id.contact_products)
        val typeTextView: TextView = itemView.findViewById(R.id.contact_type)

        val editButton: FloatingActionButton = itemView.findViewById(R.id.contact_edit)
        val deleteButton: FloatingActionButton = itemView.findViewById(R.id.contact_delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return ContactViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val current = contacts[position]
        holder.nameTextView.text = current.name
        holder.urlTextView.text = current.url
        holder.phoneTextView.text = current.phone
        holder.emailTextView.text = current.email
        holder.productsTextView.text = current.products
        holder.typeTextView.text = current.type

        holder.editButton.setOnClickListener { view ->
            val intent = Intent(view.context, EditContactActivity::class.java)
            intent.putExtra(EXTRA_CURRENT, current)
            (activityContext as Activity).startActivityForResult(
                intent,
                modifiedContactActivityRequestCode
            )

        }


        holder.deleteButton.setOnClickListener {view ->

            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle("Eliminar contacto")
            builder.setMessage("¿Está seguro que desea eliminar el contacto?")

            builder.setNeutralButton("No, conservar") { dialog, _ ->

                val message = "El contacto se conservará."
                Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show()
                dialog.cancel()
            }

            // Set the alert dialog negative/no button
            builder.setPositiveButton("Sí, eliminar") { dialog, _ ->


                deleteContact(current)
                val message = "El contacto ha sido eliminado."
                Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show()

            }

            val dialog = builder.create()
            dialog.show()

        }

    }

    private fun showDialog(contact: Contact){
        lateinit var dialog:AlertDialog

        var builder = AlertDialog.Builder(activityContext)


        builder.setTitle("Eliminar contacto")
        builder.setMessage("¿Está seguro que desea eliminar el contacto?")


        val dialogClickListener = DialogInterface.OnClickListener{ _, which ->
            when(which){
                DialogInterface.BUTTON_POSITIVE -> {
                    deleteContact(contact)
                    val message = "El contacto ha sido eliminado."
                    Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show()

                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    val message = "El contacto se conservará."
                    Toast.makeText(activityContext, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
        // Set the alert dialog positive/yes button
        builder.setPositiveButton("Sí, eliminar",dialogClickListener)

        // Set the alert dialog negative/no button
        builder.setNeutralButton("No, conservar",dialogClickListener)

        // Initialize the AlertDialog using builder object
        dialog = builder.create()

        // Finally, display the alert dialog
        dialog.show()




    }


    val activityContext: Context = context
    val deleteContact = callback

    internal fun setContacts( contacts: List<Contact>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }

    override fun getItemCount() = contacts.size


    companion object {
        const val EXTRA_CURRENT = "com.example.android.wordlistsql.EXTRA_CURRENT"
    }


}
