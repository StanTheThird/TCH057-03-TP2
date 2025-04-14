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
import okhttp3.Request
import java.io.IOException

class Voyage : AppCompatActivity() {

    private val client = OkHttpClient()
    private val URL = "http://192.168.2.128:3000/voyages" // Use the same route as MainActivity
    private lateinit var dbHelper: DbUtil // Declare dbHelper at the class level

    private lateinit var selectedDate: String
    private var selectedPeople: Int = 1
    private lateinit var priceText: TextView
    private var basePrice: Double = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voyage)

        dbHelper = DbUtil(this) // Initialize dbHelper here

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
            if (voyage == null) {
                Log.e("VoyageActivity", "Voyage data is null or not found for ID: $voyageId")
                Toast.makeText(this, "Erreur: Voyage introuvable.", Toast.LENGTH_SHORT).show()
                finish()
                return@fetchAllVoyages
            }

            // Bind voyage data to views
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
                    val voyageType = object : TypeToken<List<EntiteVoyage>>() {}.type
                    val voyages: List<EntiteVoyage> = Gson().fromJson(json, voyageType)

                    withContext(Dispatchers.Main) {
                        callback(voyages)
                    }
                } else {
                    Log.e("VoyageActivity", "Server error: ${response.code}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@Voyage, "Erreur lors du chargement des voyages.", Toast.LENGTH_SHORT).show()
                        callback(emptyList())
                    }
                }
            } catch (e: IOException) {
                Log.e("VoyageActivity", "Network error: ${e.localizedMessage}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@Voyage, "Erreur de connexion au serveur.", Toast.LENGTH_SHORT).show()
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
        val countryText: TextView = findViewById(R.id.countryText)
        val placeText: TextView = findViewById(R.id.voyageTitle)
        priceText = findViewById(R.id.voyagePrice)

        countryText.text = "Pays : ${voyage.pays}"
        placeText.text = voyage.destination
        descriptionView.text = voyage.description
        durationView.text = "Durée : ${voyage.duree}"
        remainingPlacesView.text = "Places restantes : ${voyage.getPlacesForDate(voyage.getAvailableDates().first())}"
        basePrice = voyage.prix
        updateDisplayedPrice()

        Glide.with(this)
            .load(voyage.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(imageView)

        // Populate available dates dynamically
        val availableDates = voyage.getAvailableDates()
        if (availableDates.isNotEmpty()) {
            selectedDate = voyage.getDatesFormatted().first() // Default to the first available date
            dateContainer.removeAllViews()
            for (date in voyage.getDatesFormatted()) {
                val dateButton = Button(this).apply {
                    text = date
                    setBackgroundResource(R.drawable.white_rounded_frame_360)
                    setTextColor(resources.getColor(R.color.white_50, null))
                    setPadding(16, 8, 16, 8)
                    textSize = 14f
                    setOnClickListener {
                        selectedDate = date

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
                    }
                }
                if (date == selectedDate) {
                    dateButton.setBackgroundResource(R.drawable.blue_rounded_frame_360)
                    dateButton.setTextColor(resources.getColor(R.color.white, null))
                }
                dateContainer.addView(dateButton)
            }
        }

        // Populate the Spinner for the number of people
        val maxPlaces = voyage.getPlacesForDate(availableDates.first()) // Use the first available date
        val peopleOptions = (1..maxPlaces).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, peopleOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        peopleSelector.adapter = adapter

        peopleSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPeople = peopleOptions[position]
                updateDisplayedPrice()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedPeople = 1
                updateDisplayedPrice()
            }
        }

        // Set up the reserve button
        reserveButton.setOnClickListener {
            val db = dbHelper.writableDatabase

            // Extract price from the displayed text (removing all non-numeric characters)
            val priceValue = priceText.text.toString()
                .replace("Prix : ", "")
                .replace("$", "")
                .replace(",", "")
                .toDouble()

            val values = ContentValues().apply {
                put(ReservationContract.Colonnes.VOYAGE_ID, voyage.id)
                put(ReservationContract.Colonnes.TRAVEL_DATE, selectedDate)
                put(ReservationContract.Colonnes.PASSENGER_COUNT, selectedPeople)
                put(ReservationContract.Colonnes.PRICE, priceValue)
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

    private fun updateDisplayedPrice() {
        val totalPrice = basePrice * selectedPeople
        priceText.text = "Prix : ${String.format("%,.0f", totalPrice)}$"
    }
}