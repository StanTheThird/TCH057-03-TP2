package com.example.myapplication_sqllite.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Classe utilitaire pour gérer la base de données SQLite.
 */
class DbUtil(context: Context) : SQLiteOpenHelper(
    context,
    ReservationContract.DB_NAME,
    null,
    ReservationContract.DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE ${ReservationContract.TABLE_NAME} (
                ${ReservationContract.Colonnes.ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${ReservationContract.Colonnes.VOYAGE_ID} INTEGER,
                ${ReservationContract.Colonnes.DESTINATION} TEXT,
                ${ReservationContract.Colonnes.TRAVEL_DATE} TEXT,
                ${ReservationContract.Colonnes.BOOKING_DATE} TEXT DEFAULT (datetime('now')),
                ${ReservationContract.Colonnes.PRICE} REAL,
                ${ReservationContract.Colonnes.PASSENGER_COUNT} INTEGER,
                ${ReservationContract.Colonnes.STATUS} TEXT
            )
        """.trimIndent()

        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Exemple simple : on supprime et on recrée la table (à adapter pour de vraies migrations)
        db.execSQL("DROP TABLE IF EXISTS ${ReservationContract.TABLE_NAME}")
        onCreate(db)
    }
}
