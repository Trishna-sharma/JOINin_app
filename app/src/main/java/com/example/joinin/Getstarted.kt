package com.example.joinin

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Getstarted : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getstarted)

        val editTextName = findViewById<EditText>(R.id.name)
        val editTextAge = findViewById<EditText>(R.id.age)
        val start = findViewById<Button>(R.id.GetStarted)


        start.setOnClickListener {
            val name = editTextName.text.toString().trim()
            val age = editTextAge.text.toString().trim()

            if (name.isEmpty() || age.isEmpty()) {
                Toast.makeText(this, "Please enter both name and age!!", Toast.LENGTH_SHORT).show()
            } else {
                // Storing some basic data's using shared preferences
                val sharedPreferences = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("name", name)
                editor.putString("age", age)
                editor.apply()

                //Starts the main activity after the required informations are taken from the user
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
