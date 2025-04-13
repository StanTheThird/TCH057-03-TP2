package com.example.tch057_03_tp2.modele

import android.util.Patterns

data class EntiteCompteUtilisateur(
    var id: Int = 0,
    var nom: String = "",
    var prenom: String = "",
    var email: String = "",
    var age: Int = 0,
    var telephone: String = "",
    var adresse: String = "",
    var motDePasse: String = ""
) {
    // Explicit getters for all properties
    fun getId(): Int = id
    fun getNom(): String = nom
    fun getPrenom(): String = prenom
    fun getEmail(): String = email
    fun getAge(): Int = age
    fun getTelephone(): String = telephone
    fun getAdresse(): String = adresse
    fun getMotDePasse(): String = motDePasse

    // Custom mutators with validation
    fun setEmail(value: String) {
        if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
            throw IllegalArgumentException("Courriel invalide")
        }
        email = value
    }

    fun setAge(value: Int) {
        if (value < 16) {  // Changed from 10 to 16 as per your requirement
            throw IllegalArgumentException("Âge doit être supérieur à 16")
        }
        age = value
    }

    fun setTelephone(value: String) {
        if (value.length < 8) {
            throw IllegalArgumentException("Numéro de téléphone trop court")
        }
        telephone = value
    }

    fun setMotDePasse(value: String) {
        if (value.length < 6) {
            throw IllegalArgumentException("Mot de passe trop court")
        }
        motDePasse = value
    }

    // Helper methods
    fun getNomComplet(): String = "$prenom $nom"

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "nom" to nom,
        "prenom" to prenom,
        "email" to email,
        "age" to age,
        "telephone" to telephone,
        "adresse" to adresse,
        "motDePasse" to motDePasse
    )

    companion object {
        fun fromMap(map: Map<String, Any>): EntiteCompteUtilisateur = EntiteCompteUtilisateur(
            id = map["id"] as? Int ?: 0,
            nom = map["nom"] as? String ?: "",
            prenom = map["prenom"] as? String ?: "",
            email = map["email"] as? String ?: "",
            age = map["age"] as? Int ?: 0,
            telephone = map["telephone"] as? String ?: "",
            adresse = map["adresse"] as? String ?: "",
            motDePasse = map["motDePasse"] as? String ?: ""
        )
    }
}