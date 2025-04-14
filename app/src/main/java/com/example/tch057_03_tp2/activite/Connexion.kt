package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tch057_03_tp2.R
import com.example.tch057_03_tp2.modele.EntiteCompteUtilisateur
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
    private val URL = "http://10.0.2.2:3000/"

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

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = Request.Builder()
                        .url("${URL}comptes?email=$email")
                        .build()

                    val response = client.newCall(request).execute()

                    if (response.isSuccessful) {
                        val json = response.body?.string()
                        val comptes = Gson().fromJson(json, Array<EntiteCompteUtilisateur>::class.java)

                        if (comptes.isNotEmpty()) {
                            val compte = comptes[0]
                            if (compte.motDePasse == password) {
                                withContext(Dispatchers.Main) {
                                    val sharedPref = getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE)
                                    with(sharedPref.edit()) {
                                        putBoolean("connecte", true)
                                        putString("email", email)
                                        putString("nom_utilisateur", compte.getNomComplet())
                                        apply()
                                    }
                                    startActivity(Intent(this@Connexion, MainActivity::class.java))
                                    finish()
                                }
                            } else {
                                withContext(Dispatchers.Main) {
                                    Toast.makeText(this@Connexion, "Mot de passe incorrect.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@Connexion, "Aucun compte avec cet email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@Connexion, "Erreur de connexion au serveur.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: IOException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Connexion, "Erreur: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        inscrireBtn.setOnClickListener {
            startActivity(Intent(this, Inscription::class.java))
        }
    }
}