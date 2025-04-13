package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tch057_03_tp2.R
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;


class Connexion : AppCompatActivity() {

    // Liste fictive d'utilisateurs (email -> mot de passe)
    private val comptesValidés = mapOf(
        "test@exemple.com" to Pair("1234", "Test User"),
        "admin@voyage.com" to Pair("admin", "Admin Voyage"),
        "alice@tch057.com" to Pair("alicepass", "Alice"),
        "bob@tch057.com" to Pair("bobpass", "Bob")
    )

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

            val motDePasseAttendu = comptesValidés[email]?.first
            val nomUtilisateur = comptesValidés[email]?.second

            if (motDePasseAttendu != null && motDePasseAttendu == password) {
                val sharedPref = getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("connecte", true)
                    putString("email", email)
                    putString("nom_utilisateur", nomUtilisateur) // Ajout du nom d'utilisateur
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Identifiants incorrects.", Toast.LENGTH_SHORT).show()
            }
        }

        inscrireBtn.setOnClickListener {
            startActivity(Intent(this, Inscription::class.java))
        }
    }
}
