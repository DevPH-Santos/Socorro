package com.example.socorro

import android.Manifest
import android.app.Activity
import android.content.ContentProviderClient
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Button
import android.widget.ImageButton
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
    //Declarando as variaveis
    private lateinit var textViewConfigInfo: TextView

    //Declarado os servições de preferencias compartilhadas
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

        //Iniciandoo serviço sharedPreferences
        sharedPreferences = getSharedPreferences("socorro", MODE_PRIVATE)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //Ligação entre o Kotlin eo XML
        textViewConfigInfo = findViewById(R.id.textViewConfigInfo)

        //Botão
        findViewById<Button>(R.id.buttonSOS).setOnClickListener {
            if(checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                       // Toast.makeText(this, "LAT: ${location.latitude} | LONG: ${location.longitude}", Toast.LENGTH_SHORT).show()
                        sendSMS(location.latitude.toString(), location.longitude.toString())
                    }
            }
        }
        findViewById<ImageButton>(R.id.imageButtonConfig).setOnClickListener {
            openConfigActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        initSetup()
    }

    private fun initSetup(){
        if (sharedPreferences.contains("contactPhone")){
            displayContactInfo()
        } else{
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Bem vindo ao App Socorro!")
            builder.setMessage("Antes de atualizar o app, será necessario configurar")
            builder.setCancelable(false)
            builder.setPositiveButton("Configurar agora"){ dialog, which ->
                openConfigActivity()
            }
        }
    }

    private fun displayContactInfo(){
        val contactName = sharedPreferences.getString("contactName",null)
        val contactPhone = sharedPreferences.getString("contactPhone",null)
        textViewConfigInfo.setText("$contactName | $contactPhone")
    }

    private fun openConfigActivity(){
        val intent = Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }

    private fun checkAndRequestPermission(permission:String): Boolean{
        if (ActivityCompat.checkSelfPermission(this, permission)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(permission), 0)
            return false
        }
        return true
    }

    private fun sendSMS(latitude: String, longitude: String){
        val msg = "Socorro me ajuda http://www.google.com/maps/?q=${latitude}.${longitude}"
        try{
            val smsManager: SmsManager = this.getSystemService(SmsManager::class.java)
            //Enviar
            smsManager.sendTextMessage("5554", null, msg, null, null)
        } catch (e: Exception){
            Toast.makeText(this,"Folha ao enviae o SMS.", Toast.LENGTH_SHORT).show()
        }

    }

}