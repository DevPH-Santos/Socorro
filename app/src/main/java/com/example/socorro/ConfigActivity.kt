package com.example.socorro

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class
ConfigActivity : AppCompatActivity() {

    //Declarando as variaveis
    private lateinit var editTextContactName: EditText
    private lateinit var editTextContactPhone: EditText
    private lateinit var editTextMsg: EditText

    //Declarado os servições de preferencias compartilhadas
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

        //Iniciandoo serviço sharedPreferences
        sharedPreferences = getSharedPreferences("socorro", MODE_PRIVATE)

        //Ligação entre o Kotlin eo XML
        editTextContactName = findViewById(R.id.editTextContactName)
        editTextContactPhone = findViewById(R.id.editTextContactPhone)
        editTextMsg = findViewById(R.id.editTextMsg)

        //Carrega as preferencias
        loadPreferences()

        //Botão Salvar
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
        Toast.makeText(this,"Salvo com sucesso!", Toast.LENGTH_SHORT).show()
    }

}