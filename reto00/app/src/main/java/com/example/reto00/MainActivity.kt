package com.example.reto00

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "Reto 0: Hello world!"
        setContentView(R.layout.activity_main)
    }
}