package co.edu.unal.reto8.contact

import androidx.lifecycle.LiveData

class ContactRepository (private val contactDao: ContactDao) {

        suspend fun insert(contact: Contact){
        contactDao.insertContact(contact)
    }

    suspend fun update(contact: Contact) {
        contactDao.updateContact(contact)
    }

    suspend fun delete(contact: Contact) {
        contactDao.deleteContact(contact)
    }

    fun allContacts() : LiveData<List<Contact>>{
        return contactDao.getAllContactsAlphabetic()
    }


    fun allContactsByType(type: String) : LiveData<List<Contact>>{
        return contactDao.getAllContactsOfType(type)
    }

}