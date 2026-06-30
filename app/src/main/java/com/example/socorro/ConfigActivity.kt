package com.example.socorro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ConfigActivity : AppCompatActivity() {

    private lateinit var editTextContactName: EditText
    private lateinit var editTextContactPhone: EditText
    private lateinit var editTextMsg: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_config)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sharedPreferences = getSharedPreferences("socorro", MODE_PRIVATE)

        editTextContactName = findViewById(R.id.editTextContactName)
        editTextContactPhone = findViewById(R.id.editTextContactPhone)
        editTextMsg = findViewById(R.id.editTextMsg)

        loadPreferences()

        // ✅ CORRETO: ImageButton, não Button!
        findViewById<ImageButton>(R.id.backtoMain).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.buttonSave).setOnClickListener {
            handlePreferences()
        }
    }

    private fun savePreferences(){
        sharedPreferences.edit()
            .putString("contactName", editTextContactName.text.toString())
            .putString("contactPhone", editTextContactPhone.text.toString())
            .putString("msg", editTextMsg.text.toString())
            .apply()
    }

    private fun loadPreferences(){
        editTextContactName.setText(sharedPreferences.getString("contactName",null))
        editTextContactPhone.setText(sharedPreferences.getString("contactPhone",null))
        editTextMsg.setText(sharedPreferences.getString("msg",null))
    }

    private fun handlePreferences(){
        savePreferences()
        Toast.makeText(this,getString(R.string.success), Toast.LENGTH_SHORT).show()
    }
}