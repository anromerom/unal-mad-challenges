package co.edu.unal.reto8.contact

import android.app.Application
import android.text.TextUtils
import android.view.animation.Transformation
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactViewModel(application: Application) : AndroidViewModel(application) {


    private val repository: ContactRepository

    init {
        val contactDao = ContactRoomDatabase.getDatabase(application, viewModelScope).contactDao()
        repository = ContactRepository(contactDao)

    }

    private val searchStringLiveData  = MutableLiveData<String>("")


    var allContacts: LiveData<List<Contact>> = Transformations.switchMap(searchStringLiveData)
    {
        string ->
        if (TextUtils.isEmpty(string)){
            repository.allContacts()
        } else {

            repository.allContactsByType(string)
        }

    }


    fun insert(contact: Contact) = viewModelScope.launch(Dispatchers.IO){
        repository.insert(contact)
    }

    fun update(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(contact)

    }


    fun contactsByType(type : String ) = viewModelScope.launch(Dispatchers.IO) {
        repository.allContactsByType(type)

    }

    fun searchByType(type: String) {
        searchStringLiveData.value = if(type == "Todos") "" else type

    }

}