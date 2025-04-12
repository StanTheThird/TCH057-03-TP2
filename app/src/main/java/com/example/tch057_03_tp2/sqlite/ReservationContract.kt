package com.example.myapplication_sqllite.sqlite

import android.provider.BaseColumns

/**
 * Contrat définissant la structure de la base de données SQLite pour les réservations.
 */
object ReservationContract {

    const val DB_NAME = "RESERVATIONS.DB"
    const val DB_VERSION = 1
    const val TABLE_NAME = "reservations"

    /**
     * Structure des colonnes de la table de réservations.
     */
    object Colonnes : BaseColumns {
        const val ID = "reservation_id"
        const val VOYAGE_ID = "voyage_id"
        const val DESTINATION = "destination"
        const val TRAVEL_DATE = "travel_date"
        const val BOOKING_DATE = "booking_date"
        const val PRICE = "price"
        const val PASSENGER_COUNT = "passenger_count"
        const val STATUS = "status"
    }
}
