package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.tch057_03_tp2.R

class Inscription : AppCompatActivity() {

    // Liste fictive d’emails existants
    private val fakeUserList = mutableListOf("alice@example.com", "bob@example.com")

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
            val age = ageInput.text.toString().trim()
            val phone = phoneInput.text.toString().trim()
            val address = addressInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()

            // Vérifications
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || age.isEmpty() ||
                phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email invalide.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (fakeUserList.contains(email)) {
                Toast.makeText(this, "Cet email est déjà utilisé.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (age.toIntOrNull() == null || age.toInt() < 10) {
                Toast.makeText(this, "Âge invalide.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (phone.length < 8) {
                Toast.makeText(this, "Numéro de téléphone invalide.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // On considère le compte créé
            fakeUserList.add(email)
            Toast.makeText(this, "Compte créé avec succès !", Toast.LENGTH_SHORT).show()

            // Redirection vers Connexion
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        }
    }
}
