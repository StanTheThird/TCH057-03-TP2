package com.example.myapplication_sqllite.sqlite

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tch057_03_tp2.sqlite.Reservation

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
                ${ReservationContract.Colonnes.BOOKING_DATE} TEXT DEFAULT (date('now')),
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

    fun getAllReservations(): List<Reservation> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${ReservationContract.TABLE_NAME}", null)
        val reservations = mutableListOf<Reservation>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ReservationContract.Colonnes.ID))
                val destination = cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.Colonnes.DESTINATION))
                val travelDate = cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.Colonnes.TRAVEL_DATE))
                val bookingDate = cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.Colonnes.BOOKING_DATE))
                val price = cursor.getDouble(cursor.getColumnIndexOrThrow(ReservationContract.Colonnes.PRICE))
                val status = cursor.getString(cursor.getColumnIndexOrThrow(ReservationContract.Colonnes.STATUS))

                reservations.add(Reservation(id, destination, travelDate, bookingDate, price, status))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return reservations
    }

    fun checkDatabase(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name='${ReservationContract.TABLE_NAME}'",
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        return exists
    }

    fun cancelReservation(reservationId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ReservationContract.Colonnes.STATUS, "Annulée")
        }

        val rowsAffected = db.update(
            ReservationContract.TABLE_NAME,
            values,
            "${ReservationContract.Colonnes.ID} = ?",
            arrayOf(reservationId.toString())
        )
        return rowsAffected > 0
    }

    fun getReservationCount(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM reservations", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

}