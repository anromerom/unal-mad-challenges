package co.edu.unal.reto8.contact

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(entities = arrayOf(Contact::class), version = 1, exportSchema = false)
public abstract class ContactRoomDatabase : RoomDatabase() {

    abstract fun contactDao() : ContactDao

    private class ContactDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDatabase(database.contactDao())
                }
            }
        }

        suspend fun populateDatabase(contactDao: ContactDao) {
            // Delete all content here.
            contactDao.deleteAll()

            var contact: Contact = Contact(name = "Google",
                url = "https://about.google/intl/es/products/",
                phone = "555 0233",
                email = "contact@google.com",
                type = "Consultoría")

            contactDao.insertContact(contact)

            contact = Contact(name = "Microsoft",
                url = "https://www.microsoft.com/en-us/about",
                phone = "555 0234",
                email = "contact@microsoft.com",
                type = "Fábrica de software")

            contactDao.insertContact(contact)

            contact = Contact(name = "Apple",
                url = "https://www.apple.com/en-us/about",
                phone = "555 0235",
                email = "contact@apple.com",
                type = "Desarrollo a la medida")

            contactDao.insertContact(contact)

            contact = Contact(name = "Amazon",
                url = "https://www.amazon.com/en-us/about",
                phone = "555 0236",
                email = "contact@amazon.com",
                type = "Desarrollo a la medida")

            contactDao.insertContact(contact)

            contact = Contact(name = "Oracle",
                url = "https://www.amazon.com/en-us/about",
                phone = "555 0236",
                email = "contact@amazon.com",
                type = "Consultoría")

            contactDao.insertContact(contact)

        }
    }


    companion object {

        @Volatile
        private var INSTANCE : ContactRoomDatabase? = null

        fun getDatabase (context: Context,
                         scope: CoroutineScope): ContactRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ContactRoomDatabase::class.java,
                    "contact_database"
                ).addCallback(ContactDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }





}