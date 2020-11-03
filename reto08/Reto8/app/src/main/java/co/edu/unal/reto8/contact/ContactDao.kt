package co.edu.unal.reto8.contact

import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface ContactDao {


    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact : Contact)

    // READ
    @Query("SELECT * FROM contact_table ORDER BY name ASC")
    fun getAllContactsAlphabetic(): LiveData<List<Contact>>

    @Query("SELECT * FROM contact_table WHERE type = :type ORDER BY name ASC ")
    fun getAllContactsOfType(type : String): LiveData<List<Contact>>

    // UPDATE
    @Update
    suspend fun updateContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)

    @Query("DELETE FROM contact_table")
    suspend fun deleteAll()


}