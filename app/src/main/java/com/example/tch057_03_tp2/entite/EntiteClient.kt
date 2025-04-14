package com.example.tch057_03_tp2.modele

import com.google.gson.Gson

data class EntiteClient(
    var id: Int = 0,
    var nom: String = "",
    var prenom: String = "",
    var email: String = "",
    var mdp: String = "",  // Changé de motDePasse à mdp
    var age: Int = 0,
    var telephone: String = "",
    var adresse: String = ""
) {
    // Helper methods
    fun getNomComplet(): String = "$prenom $nom"

    fun validatePassword(inputPassword: String): Boolean {
        return mdp == inputPassword
    }

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "nom" to nom,
        "prenom" to prenom,
        "email" to email,
        "mdp" to mdp,  // Champ mis à jour
        "age" to age,
        "telephone" to telephone,
        "adresse" to adresse
    )

    companion object {
        fun fromMap(map: Map<String, Any>): EntiteClient {
            return EntiteClient(
                id = (map["id"] as? Number)?.toInt() ?: 0,  // Gestion plus robuste des nombres
                nom = map["nom"] as? String ?: "",
                prenom = map["prenom"] as? String ?: "",
                email = map["email"] as? String ?: "",
                mdp = map["mdp"] as? String ?: "",  // Champ mis à jour
                age = (map["age"] as? Number)?.toInt() ?: 0,
                telephone = map["telephone"] as? String ?: "",
                adresse = map["adresse"] as? String ?: ""
            )
        }

        // Nouvelle méthode pour créer depuis un JSON brut
        fun fromJson(json: String): EntiteClient? {
            return try {
                val map = Gson().fromJson(json, Map::class.java) as Map<String, Any>
                fromMap(map)
            } catch (e: Exception) {
                null
            }
        }
    }
}