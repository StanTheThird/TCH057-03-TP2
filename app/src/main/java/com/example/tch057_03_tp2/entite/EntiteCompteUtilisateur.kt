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