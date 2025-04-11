package com.example.tch057_03_tp2

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fetch voyages from the repository
        val voyageRepository = VoyageRepository()
        val voyages = voyageRepository.fetchVoyages()

        // Prepare data for the GenericAdapter
        val voyageData = voyages.map { voyage ->
            mapOf(
                "voyageNameText" to voyage.title,
                "voyageImage" to voyage.imageUrl,
                "prixText" to voyage.price,
                "description" to voyage.description
            )
        }

        // Find the LinearLayout you want to make clickable
        val reservationBtn = findViewById<LinearLayout>(R.id.reservation_btn)

        // Set click listener
        reservationBtn.setOnClickListener {
            // Create an Intent to start the HistoriqueActivity
            val intent = Intent(this, Historique::class.java)
            startActivity(intent)
        }

        // Set up RecyclerView with GenericAdapter
        val recyclerView: RecyclerView = findViewById(R.id.listVoyage)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GenericAdapter(
            context = this,
            layoutId = R.layout.item_voyage, // Specify the layout for list items
            items = voyageData,
            onItemClick = { position ->
                // Pass only the voyage ID to VoyageActivity
                val selectedVoyage = voyages[position]
                val intent = Intent(this, Voyage::class.java).apply {
                    putExtra("voyageId", selectedVoyage.id) // Pass the ID
                }
                startActivity(intent)
            }
        )
    }
}