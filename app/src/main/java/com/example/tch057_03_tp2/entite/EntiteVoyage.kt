package com.example.tch057_03_tp2.modele

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class EntiteVoyage(
    @SerializedName("id")
    var id: Int = 0,

    @SerializedName("nom_voyage")
    var nomVoyage: String = "",

    @SerializedName("destination")
    var destination: String = "",

    @SerializedName("description")
    var description: String = "",

    @SerializedName("type_de_voyage")
    var typeVoyage: String = "",

    @SerializedName("duree_jours")
    var dureeJours: Int = 0,

    @SerializedName("prix")
    var prix: Double = 0.0,

    @SerializedName("image_url")
    var imageUrl: String = "",

    @SerializedName("activites_incluses")
    var activitesIncluses: String = "",

    @SerializedName("trips")
    var trips: List<Trip> = emptyList()
) {
    data class Trip(
        @SerializedName("date")
        val date: String,

        @SerializedName("nb_places_disponibles")
        val placesDisponibles: Int
    )

    fun getPrixFormatted(): String = "%.2f$".format(prix)

    fun getDatesFormatted(): List<String> {
        return trips.map { trip ->
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.FRENCH)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
            val date = inputFormat.parse(trip.date) ?: Date()
            outputFormat.format(date)
        }
    }

    fun getPlacesForDate(dateString: String): Int {
        return trips.firstOrNull { it.date == dateString }?.placesDisponibles ?: 0
    }

    fun getAvailableTrips(): List<Trip> {
        return trips.filter { it.placesDisponibles > 0 }
    }

    fun toMap(): Map<String, Any> = mapOf(
        "id" to id,
        "nom_voyage" to nomVoyage,
        "destination" to destination,
        "description" to description,
        "type_de_voyage" to typeVoyage,
        "duree_jours" to dureeJours,
        "prix" to prix,
        "image_url" to imageUrl,
        "activites_incluses" to activitesIncluses,
        "trips" to trips.map { trip ->
            mapOf(
                "date" to trip.date,
                "nb_places_disponibles" to trip.placesDisponibles
            )
        }
    )

    companion object {
        fun fromMap(map: Map<String, Any>): EntiteVoyage {
            val tripsList = (map["trips"] as? List<Map<String, Any>> ?: emptyList()).map { tripMap ->
                Trip(
                    date = tripMap["date"] as? String ?: "",
                    placesDisponibles = (tripMap["nb_places_disponibles"] as? Number)?.toInt() ?: 0
                )
            }

            return EntiteVoyage(
                id = (map["id"] as? Number)?.toInt() ?: 0,
                nomVoyage = map["nom_voyage"] as? String ?: "",
                destination = map["destination"] as? String ?: "",
                description = map["description"] as? String ?: "",
                typeVoyage = map["type_de_voyage"] as? String ?: "",
                dureeJours = (map["duree_jours"] as? Number)?.toInt() ?: 0,
                prix = (map["prix"] as? Number)?.toDouble() ?: 0.0,
                imageUrl = map["image_url"] as? String ?: "",
                activitesIncluses = map["activites_incluses"] as? String ?: "",
                trips = tripsList
            )
        }
    }
}