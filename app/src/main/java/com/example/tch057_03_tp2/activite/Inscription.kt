package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class Inscription : AppCompatActivity() {

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()
    private val URL = "http://10.0.2.2:3000/clients" // Updated endpoint

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inscription)

        val nomInput = findViewById<EditText>(R.id.nom_imput)
        val prenomInput = findViewById<EditText>(R.id.prenom_imput)
        val emailInput = findViewById<EditText>(R.id.email_imput)
        val ageInput = findViewById<EditText>(R.id.age_imput)
        val phoneInput = findViewById<EditText>(R.id.phone_imput)
        val addressInput = findViewById<EditText>(R.id.adress_imput)
        val passwordInput = findViewById<EditText>(R.id.password_imput)
        val btnContinuer = findViewById<Button>(R.id.continuerBtn)

        btnContinuer.setOnClickListener {
            val nom = nomInput.text.toString().trim()
            val prenom = prenomInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val age = ageInput.text.toString().trim().toIntOrNull() ?: 0
            val phone = phoneInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Validation
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || ageInput.text.toString().isEmpty() ||
                phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email invalide.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (age < 16) {
                Toast.makeText(this, "Âge doit être supérieur à 16.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length < 8) {
                Toast.makeText(this, "Numéro de téléphone invalide.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerClient(nom, prenom, email, age, phone, address, password)
        }
    }

    private fun registerClient(
        nom: String,
        prenom: String,
        email: String,
        age: Int,
        phone: String,
        address: String,
        password: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Check if email exists
                val checkRequest = Request.Builder()
                    .url("$URL?email=$email")
                    .build()

                val checkResponse = client.newCall(checkRequest).execute()
                if (checkResponse.isSuccessful && checkResponse.body?.string()?.isNotEmpty() == true) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Inscription, "Cet email est déjà utilisé.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Create new client
                val newClient = EntiteClient(
                    id = 0, // Server will assign ID
                    nom = nom,
                    prenom = prenom,
                    email = email,
                    mdp = password,
                    age = age,
                    telephone = phone,
                    adresse = address
                )

                val json = Gson().toJson(newClient)
                val body = json.toRequestBody(JSON)

                val createRequest = Request.Builder()
                    .url(URL)
                    .post(body)
                    .build()

                val createResponse = client.newCall(createRequest).execute()

                if (createResponse.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Inscription, "Compte créé avec succès!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Inscription, Connexion::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@Inscription,
                            "Erreur lors de la création: ${createResponse.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@Inscription,
                        "Erreur de connexion: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}