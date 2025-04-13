package com.example.tch057_03_tp2.modele

import java.util.Date

data class EntiteVoyage(
    var id: Int = 0,
    var pays: String = "",
    var destination: String = "",
    var description: String = "",
    var type: String = "",
    var duree: String = "",
    var prix: Double = 0.0,
    var imageUrl: String = "",
    var datesDisponibles: Map<Long, Int> = emptyMap() // Utilise une map pour savoir le nombre de place selon la date. Nécéssiteras probablement des changements dans MainActivity
) {
    // Explicit getters for all properties
    fun getId(): Int = id
    fun getPays(): String = pays
    fun getDestination(): String = destination
    fun getDescription(): String = description
    fun getType(): String = type
    fun getDuree(): String = duree
    fun getPrix(): Double = prix
    fun getImageUrl(): String = imageUrl
    fun getDatesDisponibles(): Map<Long, Int> = datesDisponibles

    // Custom mutators with validation
    fun setPrix(value: Double) {
        if (value < 0) {
            throw IllegalArgumentException("Le prix ne peut pas être négatif")
        }
        prix = value
    }

    fun setPlacesForDate(date: Long, places: Int) {
        if (places < 0) {
            throw IllegalArgumentException("Le nombre de places ne peut pas être négatif")
        }
        datesDisponibles = datesDisponibles.toMutableMap().apply { put(date, places) }
    }

    // Helper methods
    fun getPrixFormatted(): String = "%.2f$".format(prix)

    fun getDatesFormatted(): List<String> {
        return datesDisponibles.keys.map { timestamp ->
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.FRENCH).format(Date(timestamp))
        }
    }

    fun getPlacesForDate(date: Long): Int {
        return datesDisponibles[date] ?: 0
    }

    fun getAvailableDates(): List<Long> {
        return datesDisponibles.filter { it.value > 0 }.keys.toList()
    }

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "pays" to pays,
        "destination" to destination,
        "description" to description,
        "type" to type,
        "duree" to duree,
        "prix" to prix,
        "imageUrl" to imageUrl,
        "datesDisponibles" to datesDisponibles
    )

    companion object {
        fun fromMap(map: Map<String, Any>): EntiteVoyage = EntiteVoyage(
            id = map["id"] as? Int ?: 0,
            pays = map["pays"] as? String ?: "",
            destination = map["destination"] as? String ?: "",
            description = map["description"] as? String ?: "",
            type = map["type"] as? String ?: "",
            duree = map["duree"] as? String ?: "",
            prix = map["prix"] as? Double ?: 0.0,
            imageUrl = map["imageUrl"] as? String ?: "",
            datesDisponibles = (map["datesDisponibles"] as? Map<*, *> ?: emptyMap<Any, Any>())
                .mapKeys { (key, _) -> (key as? Number)?.toLong() ?: 0L }
                .mapValues { (_, value) -> (value as? Number)?.toInt() ?: 0 }
        )
    }
}