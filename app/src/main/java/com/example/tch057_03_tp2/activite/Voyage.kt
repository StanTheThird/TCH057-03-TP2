package com.example.tch057_03_tp2.activite

import android.content.ContentValues
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication_sqllite.sqlite.DbUtil
import com.example.myapplication_sqllite.sqlite.ReservationContract
import com.example.tch057_03_tp2.R

class Voyage : AppCompatActivity() {

    private lateinit var selectedDate: String
    private var selectedPeople: Int = 1
    private lateinit var priceText: TextView
    private var basePrice : Double = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voyage)
        val dbHelper = DbUtil(this)
        // Get the voyage ID from the Intent
        val voyageId = intent.getIntExtra("voyageId", -1)

        // Fetch voyage details using the repository
        val voyageRepository = VoyageRepository()
        val voyage = voyageRepository.getVoyageById(voyageId)

        // If voyage is null, finish the activity
        if (voyage == null) {
            finish() // Close the activity if the ID is invalid
            return
        }

        // Bind voyage data to view
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
        countryText.text = "Pays : ${voyage.country}"
        placeText.text = voyage.place


        descriptionView.text = voyage.description
        durationView.text = "Durée : ${voyage.duree}"
        remainingPlacesView.text = "Places restantes : ${voyage.placesRestantes}"
        basePrice = voyage.price
        updateDisplayedPrice() // Initialize the price display

        Glide.with(this)
            .load(voyage.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(imageView)

        // Populate dates dynamically
        selectedDate = voyageRepository.convertLongToDateString(voyage.possibleDates.first()) // Default to the first date
        dateContainer.removeAllViews()
        for (date in voyageRepository.convertLongListToDateStringList(voyage.possibleDates)) {
            val dateButton = Button(this).apply {
                text = date
                setBackgroundResource(R.drawable.white_rounded_frame_360)
                setTextColor(resources.getColor(R.color.white_50, null))
                setPadding(16, 8, 16, 8)
                textSize = 14f
                setOnClickListener {
                    // Update selected date
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
            // Highlight the first date by default
            if (date == selectedDate) {
                dateButton.setBackgroundResource(R.drawable.blue_rounded_frame_360)
                dateButton.setTextColor(resources.getColor(R.color.white, null))
            }
            dateContainer.addView(dateButton)
        }

        // Populate the Spinner for number of people
        val peopleOptions = (1..voyage.placesRestantes).toList()
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
                // BOOKING_DATE is auto-filled with datetime('now')

            }

            val newRowId = db.insert(ReservationContract.TABLE_NAME, null, values)

            if (newRowId != -1L) {
                Toast.makeText(
                    this,
                    "Réservation ajoutée avec succès !",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Erreur lors de la réservation.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateDisplayedPrice() {
        val totalPrice = basePrice * selectedPeople
        priceText.text = "Prix : ${String.format("%,.0f", totalPrice)}$"
    }
}