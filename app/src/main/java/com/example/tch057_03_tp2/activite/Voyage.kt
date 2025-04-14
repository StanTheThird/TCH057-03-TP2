package com.example.tch057_03_tp2.activite

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication_sqllite.sqlite.DbUtil
import com.example.myapplication_sqllite.sqlite.ReservationContract
import com.example.tch057_03_tp2.R
import com.example.tch057_03_tp2.modele.EntiteVoyage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import com.google.gson.JsonSyntaxException
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class Voyage : AppCompatActivity() {

    private val client = OkHttpClient()
    private val URL = "http://10.0.2.2:3000/voyages"
    private lateinit var dbHelper: DbUtil
    private lateinit var selectedTrip: EntiteVoyage.Trip
    private var selectedPeople: Int = 1
    private lateinit var priceText: TextView
    private var basePrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voyage)

        dbHelper = DbUtil(this)

        val voyageId = intent.getIntExtra("voyageId", -1)
        Log.d("VoyageActivity", "Received voyageId: $voyageId")

        if (voyageId == -1) {
            Log.e("VoyageActivity", "Invalid voyageId, finishing activity")
            Toast.makeText(this, "Erreur: ID de voyage invalide.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchAllVoyages { voyages ->
            val voyage = voyages.find { it.id == voyageId }
            if (voyage == null || voyage.trips.isEmpty()) {
                Log.e("VoyageActivity", "Voyage data is null or has no available trips")
                Toast.makeText(this, "Erreur: Voyage introuvable ou aucune date disponible.", Toast.LENGTH_SHORT).show()
                finish()
                return@fetchAllVoyages
            }

            bindVoyageData(voyage)
        }
    }

    private fun fetchAllVoyages(callback: (List<EntiteVoyage>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(URL).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    Log.d("VoyageActivity", "Server response: $json")

                    val gson = Gson()
                    val voyages = try {
                        gson.fromJson(json, Array<EntiteVoyage>::class.java).toList()
                    } catch (e: JsonSyntaxException) {
                        try {
                            data class ApiResponse(val voyages: List<EntiteVoyage>)
                            gson.fromJson(json, ApiResponse::class.java).voyages
                        } catch (e: Exception) {
                            Log.e("VoyageActivity", "Failed to parse JSON", e)
                            emptyList()
                        }
                    }

                    withContext(Dispatchers.Main) {
                        callback(voyages)
                    }
                } else {
                    Log.e("VoyageActivity", "Server error: ${response.code}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Voyage, "Erreur serveur: ${response.code}", Toast.LENGTH_SHORT).show()
                        callback(emptyList())
                    }
                }
            } catch (e: IOException) {
                Log.e("VoyageActivity", "Network error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Voyage, "Erreur réseau: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(emptyList())
                }
            } catch (e: Exception) {
                Log.e("VoyageActivity", "Unexpected error", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Voyage, "Erreur inattendue: ${e.message}", Toast.LENGTH_SHORT).show()
                    callback(emptyList())
                }
            }
        }
    }

    private fun bindVoyageData(voyage: EntiteVoyage) {
        val subtitleView: TextView = findViewById(R.id.voyageSubtitle)
        val imageView: ImageView = findViewById(R.id.voyageImage)
        val descriptionView: TextView = findViewById(R.id.voyageDescription)
        val durationView: TextView = findViewById(R.id.tripDurationText)
        val dateContainer: LinearLayout = findViewById(R.id.dateContainer)
        val peopleSelector: Spinner = findViewById(R.id.peopleSelector)
        val remainingPlacesView: TextView = findViewById(R.id.placesRestantesText)
        val reserveButton: Button = findViewById(R.id.bookButton)
        val placeText: TextView = findViewById(R.id.voyageTitle)
        priceText = findViewById(R.id.voyagePrice)

        placeText.text = voyage.nomVoyage
        subtitleView.text = voyage.destination
        descriptionView.text = voyage.description
        durationView.text = "Durée : ${voyage.dureeJours} jours"

        // Select first available trip by default
        selectedTrip = voyage.getAvailableTrips().first()
        basePrice = voyage.prix
        updateDisplayedPrice()

        Glide.with(this)
            .load(voyage.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(imageView)

        val availableTrips = voyage.getAvailableTrips()
        if (availableTrips.isNotEmpty()) {
            dateContainer.removeAllViews()
            for (trip in availableTrips) {
                val dateButton = Button(this).apply {
                    val formattedDate = formatDateString(trip.date)
                    text = formattedDate
                    setBackgroundResource(R.drawable.white_rounded_frame_360)
                    setTextColor(resources.getColor(R.color.white_50, null))
                    setPadding(16, 8, 16, 8)
                    textSize = 14f
                    setOnClickListener {
                        selectedTrip = trip
                        remainingPlacesView.text = "Places restantes : ${trip.placesDisponibles}"

                        // Update button states
                        for (i in 0 until dateContainer.childCount) {
                            val child = dateContainer.getChildAt(i)
                            if (child is Button) {
                                child.setBackgroundResource(R.drawable.white_rounded_frame_360)
                                child.setTextColor(resources.getColor(R.color.white_50, null))
                            }
                        }
                        setBackgroundResource(R.drawable.blue_rounded_frame_360)
                        setTextColor(resources.getColor(R.color.white, null))

                        // Update people selector
                        updatePeopleSelector(peopleSelector, trip.placesDisponibles)
                    }
                }
                if (trip == selectedTrip) {
                    dateButton.setBackgroundResource(R.drawable.blue_rounded_frame_360)
                    dateButton.setTextColor(resources.getColor(R.color.white, null))
                }
                dateContainer.addView(dateButton)
            }

            remainingPlacesView.text = "Places restantes : ${selectedTrip.placesDisponibles}"
            updatePeopleSelector(peopleSelector, selectedTrip.placesDisponibles)
        }

        reserveButton.setOnClickListener {
            val db = dbHelper.writableDatabase
            val totalPrice = basePrice * selectedPeople

            val values = ContentValues().apply {
                put(ReservationContract.Colonnes.VOYAGE_ID, voyage.id)
                put(ReservationContract.Colonnes.DESTINATION, voyage.destination)
                put(ReservationContract.Colonnes.TRAVEL_DATE, selectedTrip.date)
                put(ReservationContract.Colonnes.PRICE, totalPrice)
                put(ReservationContract.Colonnes.PASSENGER_COUNT, selectedPeople)
                put(ReservationContract.Colonnes.STATUS, "Confirmée")
            }

            val newRowId = db.insert(ReservationContract.TABLE_NAME, null, values)

            if (newRowId != -1L) {
                Toast.makeText(this, "Réservation ajoutée avec succès !", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erreur lors de la réservation.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePeopleSelector(spinner: Spinner, maxPlaces: Int) {
        val peopleOptions = (1..maxPlaces).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, peopleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPeople = peopleOptions[position]
                updateDisplayedPrice()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedPeople = 1
                updateDisplayedPrice()
            }
        }
    }

    private fun updateDisplayedPrice() {
        val totalPrice = basePrice * selectedPeople
        priceText.text = "Prix : ${String.format("%,.0f", totalPrice)}$"
    }

    private fun formatDateString(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString) ?: return dateString
        return outputFormat.format(date)
    }
}