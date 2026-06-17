package com.example.socorro

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    //Declarando os atributos da classe
    private lateinit var textViewContacInfo: TextView

    //Declarando as sharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //Inicializando o serviço de SharedPreferences
        sharedPreferences = getSharedPreferences("socorro", MODE_PRIVATE)

        //ligação entre o Kotlin e o XML
        textViewContacInfo = findViewById(R.id.textViewContactInfo)

        //=============== Botões ===============\\
        findViewById<Button>(R.id.buttonSOS).setOnClickListener{
            //todo
        }//Botão SOS

        findViewById<Button>(R.id.imageButtonConfig).setOnClickListener {
            openConfigActivity()
        }//Botão config

        initSetup()

    }//fim do onCreate

    private fun initSetup(){
        if (sharedPreferences.contains("contactPhone")){
            displayContactInfo()
        }else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.titleAlertDialog))
            builder.setMessage(getString(R.string.messageAlertDialog))
            builder.setPositiveButton("Configurar Agora"){ dialog, which ->
                openConfigActivity()
            }
        }
    }

    private fun displayContactInfo(){
        val contactName = sharedPreferences.getString("contactName", null)
        val contactPhone = sharedPreferences.getString("contactPhone", null)
        textViewContacInfo.setText("$contactName | $contactPhone")
    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }

}//fim da class