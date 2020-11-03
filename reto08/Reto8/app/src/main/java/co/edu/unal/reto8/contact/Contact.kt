package co.edu.unal.reto8.contact

import androidx.room.*
import java.io.Serializable


@Entity(tableName = "contact_table")
data class Contact  (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Int = 0,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "url") var url: String,
    @ColumnInfo(name = "phone") var phone: String,
    @ColumnInfo(name = "email") var email: String,
    @ColumnInfo(name = "products") var products: String = "Sin descripción de los productos",
    @ColumnInfo(name = "type") var type: String = "Sin clasificar"


): Serializable
