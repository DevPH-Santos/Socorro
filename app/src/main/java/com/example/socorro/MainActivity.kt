package com.example.socorro

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : AppCompatActivity() {
    //Declarando os atributos da classe
    private lateinit var textViewContacInfo: TextView

    //Declarando as sharedPreferences
    private lateinit var sharedPreferences: SharedPreferences

    //declarando o serviço de localização
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    //declarando o serviço de envio de SMS
    private lateinit var smsManager: SmsManager

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

        //inicializando o serviço de localização
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //ligação entre o Kotlin e o XML
        textViewContacInfo = findViewById(R.id.textViewContactInfo)

        //=============== Botões ===============\\
        findViewById<Button>(R.id.buttonSOS).setOnClickListener{

            if(checkAndRequestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)){
                //localizar
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    Toast.makeText(this, "LAT: ${location.latitude} | LONG: ${location.longitude}", Toast.LENGTH_SHORT).show()
                }
            }

        }//Botão SOS

        findViewById<Button>(R.id.imageButtonConfig).setOnClickListener {
            openConfigActivity()
        }//Botão config


    }//fim do onCreate

    override fun onStart() {
        super.onStart()
        initSetup()
    }

    private fun initSetup(){
        if (sharedPreferences.contains("contactPhone")){
            displayContactInfo()
        }else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.titleAlertDialog))
            builder.setMessage(getString(R.string.messageAlertDialog))
            builder.setCancelable(false)
            builder.setPositiveButton("Configurar Agora"){ dialog, which ->
                openConfigActivity()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayContactInfo(){
        val contactName = sharedPreferences.getString("contactName", null)
        val contactPhone = sharedPreferences.getString("contactPhone", null)
        textViewContacInfo.setText("$contactName | $contactPhone")
    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }

    private fun checkAndRequestPermission(permission: String):Boolean{
        if(ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            //naooo tenho permissão
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
            return false
        }

        return true

    }

    private fun sendSMS(latitude: String, longitude: String) {
        val msg = getString(R.string.msgSMSSocorro)

        try {
            val smsManager: SmsManager = this.getSystemService(SmsManager::class.java)

            //envia o sms
            smsManager.sendTextMessage("5554", null, msg, null, null)
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.errorMessageSMS), Toast.LENGTH_SHORT).show()
        }
    }

}//fim da class