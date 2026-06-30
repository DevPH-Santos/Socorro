package com.example.socorro

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
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
    // ============= DECLARAÇÃO DE VARIÁVEIS =============
    private lateinit var textViewConfigInfo: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialização dos serviços
        initializeServices()

        // Botão SOS
        findViewById<Button>(R.id.buttonSOS).setOnClickListener {
            handleSmsProcess()
        }

        // Botão de Configuração
        findViewById<ImageButton>(R.id.imageButtonConfig).setOnClickListener {
            openConfigActivity()
        }
    }

    override fun onStart() {
        super.onStart()
        initSetup()
    }

    //inicializacao
    private fun initializeServices() {
        sharedPreferences = getSharedPreferences("socorro", MODE_PRIVATE)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        textViewConfigInfo = findViewById(R.id.textViewConfigInfo)
    }

    private fun initSetup() {
        if (sharedPreferences.contains("contactPhone")) {
            displayContactInfo()
        } else {
            showConfigurationDialog()
        }
    }

    //exibir informações
    private fun displayContactInfo() {
        val contactName = sharedPreferences.getString("contactName", null)
        val contactPhone = sharedPreferences.getString("contactPhone", null)
        textViewConfigInfo.text = "$contactName | $contactPhone"
    }

    private fun showConfigurationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.greetingMsg))
        builder.setMessage(getString(R.string.waringUsableMsg))
        builder.setCancelable(false)
        builder.setPositiveButton(getString(R.string.buttonConfigNow)) { dialog, which ->
            openConfigActivity()
        }
        builder.show()
    }

    private fun openConfigActivity() {
        val intent = android.content.Intent(this, ConfigActivity::class.java)
        startActivity(intent)
    }

    //verifica se tem permissão
    private fun checkAndRequestPermission(permission: String): Boolean {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE_PERMISSION)
            return false
        }
        return true
    }

    // função CORAÇÃO para enviar o sms
    private fun handleSmsProcess() {
        //verifica permissão
        if (!checkAndRequestPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            Toast.makeText(
                this,
                "Permissão de localização necessária para enviar SOS",
                Toast.LENGTH_SHORT
            ).show()

            return

        }

        //pegar localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                // ve se location é nulo ou nn
                if (location != null) {
                    val latitude = location.latitude.toString()
                    val longitude = location.longitude.toString()
                    sendSMS(latitude, longitude)
                } else {
                    showLocationErrorDialog()
                }

            }

            .addOnFailureListener { exception ->

                Toast.makeText(
                    this,
                    "Erro ao buscar localização: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }

        }

    }

    // envia o sms
    private fun sendSMS(latitude: String, longitude: String) {
        val contactPhone = sharedPreferences.getString("contactPhone", null)
        val contactName = sharedPreferences.getString("contactName", null)

        if (contactPhone.isNullOrEmpty()) {
            Toast.makeText(
                this,
                "Número de telefone não configurado. Por favor, acesse as configurações.",
                Toast.LENGTH_LONG
            ).show()
            openConfigActivity()
            return
        }

        val messageBody = buildSosMessage(contactName, latitude, longitude)

        try {
            //uso da ia para ser acessivel para todas versoes
            val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Para Android 12 (API 31) ou superior
                this.getSystemService(SmsManager::class.java)
            } else {
                // Para versões anteriores
                @Suppress("DEPRECATION")
                android.telephony.SmsManager.getDefault()
            }

            // Enviar o SMS
            smsManager.sendTextMessage(
                contactPhone,
                null,
                messageBody,
                null,
                null
            )

            // Feedback de sucesso
            Toast.makeText(
                this,
                "SOS enviado com sucesso!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Erro: Permissão de SMS não concedida",
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Toast.makeText(
                this,
                getString(R.string.failureSendSMS),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    //função para estruturar a msg
    private fun buildSosMessage(contactName: String?, latitude: String, longitude: String): String {
        return """
            🚨 SOCORRO! 🚨
            
            Preciso de ajuda urgente!
            ${if (!contactName.isNullOrEmpty()) "Contato: $contactName\n" else ""}
            Minha localização atual:
            Latitude: $latitude
            Longitude: $longitude
            
            Abrir no Google Maps:
            https://maps.google.com/?q=$latitude,$longitude
        """.trimIndent()
    }

    //tela de erro ao capturar localização
    private fun showLocationErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("Localização Indisponível")
            .setMessage(
                "Não foi possível obter sua localização.\n\n" +
                        "Verifique se:\n" +
                        "• O GPS está ativado\n" +
                        "• Você tem permissão de localização concedida\n" +
                        "• Você não está em um local sem sinal (subsolo, túnel, etc)"
            )
            .setPositiveButton("Tentar Novamente") { dialog, which ->
                handleSmsProcess()
            }
            .setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSION = 100
    }
}