package com.example.tch057_03_tp2

import android.os.Bundle
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

        // Set up RecyclerView with GenericAdapter
        val recyclerView: RecyclerView = findViewById(R.id.listVoyage)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = GenericAdapter(
            context = this,
            layoutId = R.layout.voyage_item, // Specify the layout for list items
            items = voyageData // Provide the data
        )
    }

}