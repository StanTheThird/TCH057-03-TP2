package com.example.tch057_03_tp2.activite

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tch057_03_tp2.R

class Voyage : AppCompatActivity() {

    private lateinit var selectedDate: String
    private var selectedPeople: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voyage)

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

        // Bind voyage data to views
        val titleView: TextView = findViewById(R.id.voyageTitle)
        val subtitleView: TextView = findViewById(R.id.voyageSubtitle)
        val imageView: ImageView = findViewById(R.id.voyageImage)
        val descriptionView: TextView = findViewById(R.id.voyageDescription)
        val durationView: TextView = findViewById(R.id.tripDurationText)
        val dateContainer: LinearLayout = findViewById(R.id.dateContainer)
        val peopleSelector: Spinner = findViewById(R.id.peopleSelector)
        val remainingPlacesView: TextView = findViewById(R.id.placesRestantesText)
        val reserveButton: Button = findViewById(R.id.bookButton)

        titleView.text = voyage.title
        subtitleView.text = "Voyage vers ${voyage.title}"
        descriptionView.text = voyage.description
        durationView.text = "Durée : ${voyage.duree}"
        remainingPlacesView.text = "Places restantes : ${voyage.placesRestantes}"

        Glide.with(this)
            .load(voyage.imageUrl)
            .placeholder(R.drawable.placeholder_image)
            .error(R.drawable.error_image)
            .into(imageView)

        // Populate dates dynamically
        selectedDate = voyage.possibleDates.first() // Default to the first date
        dateContainer.removeAllViews()
        for (date in voyage.possibleDates) {
            val dateButton = Button(this).apply {
                text = date
                setBackgroundResource(R.drawable.rounded_button_unselected)
                setTextColor(resources.getColor(R.color.light_bluish_gray, null))
                setPadding(16, 8, 16, 8)
                textSize = 14f
                setOnClickListener {
                    // Update selected date
                    selectedDate = date

                    // Update button states
                    for (i in 0 until dateContainer.childCount) {
                        val child = dateContainer.getChildAt(i)
                        if (child is Button) {
                            child.setBackgroundResource(R.drawable.rounded_button_unselected)
                            child.setTextColor(resources.getColor(R.color.light_bluish_gray, null))
                        }
                    }
                    setBackgroundResource(R.drawable.rounded_button_selected)
                    setTextColor(resources.getColor(R.color.white, null))
                }
            }
            // Highlight the first date by default
            if (date == selectedDate) {
                dateButton.setBackgroundResource(R.drawable.rounded_button_selected)
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedPeople = 1 // Default to 1 if nothing is selected
            }
        }

        // Set up the reserve button
        reserveButton.setOnClickListener {
            Toast.makeText(
                this,
                "Réservation confirmée pour $selectedPeople personne(s) le $selectedDate",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}