package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tch057_03_tp2.R
import com.example.tch057_03_tp2.modele.EntiteClient
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class Connexion : AppCompatActivity() {

    private val client = OkHttpClient()
    private val URL = "http://10.0.2.2:3000/clients" //PEut varier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_connexion)

        val emailInput = findViewById<EditText>(R.id.email_imput)
        val passwordInput = findViewById<EditText>(R.id.password_imput)
        val continuerBtn = findViewById<Button>(R.id.continuerBtn)
        val inscrireBtn = findViewById<Button>(R.id.inscrireBtn)

        continuerBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            authenticateUser(email, password)
        }

        inscrireBtn.setOnClickListener {
            startActivity(Intent(this, Inscription::class.java))
        }
    }

    private fun authenticateUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder()
                    .url("$URL?email=$email")
                    .build()

                val response = client.newCall(request).execute()

                when {
                    !response.isSuccessful -> {
                        showToast("Erreur de connexion au serveur: ${response.code}")
                    }
                    response.body?.contentLength() == 0L -> {
                        showToast("Aucun compte avec cet email")
                    }
                    else -> {
                        val json = response.body?.string()
                        val clients = Gson().fromJson(json, Array<EntiteClient>::class.java)

                        if (clients.isNotEmpty()) {
                            val client = clients[0]
                            if (client.mdp == password) {
                                saveSessionAndRedirect(
                                    email = email,
                                    fullName = client.getNomComplet()
                                )
                            } else {
                                showToast("Mot de passe incorrect")
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                showToast("Erreur réseau: ${e.localizedMessage}")
            }
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(this@Connexion, message, Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun saveSessionAndRedirect(email: String, fullName: String) {
        withContext(Dispatchers.Main) {
            getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE).edit().apply {
                putBoolean("connecte", true)
                putString("email", email)
                putString("nom_utilisateur", fullName)
                apply()
            }
            startActivity(Intent(this@Connexion, MainActivity::class.java))
            finish()
        }
    }
}