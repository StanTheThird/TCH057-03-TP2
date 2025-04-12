package com.example.tch057_03_tp2

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var voyageRepository: VoyageRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GenericAdapter
    private lateinit var voyages: List<VoyageRepository.Voyage>
    private var filteredVoyages: List<VoyageRepository.Voyage> = listOf()

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        voyageRepository = VoyageRepository()
        voyages = voyageRepository.fetchVoyages()
        filteredVoyages = voyages

        // Set up RecyclerView
        recyclerView = findViewById(R.id.listVoyage)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = GenericAdapter(
            context = this,
            layoutId = R.layout.item_voyage,
            items = mapVoyagesToAdapterData(filteredVoyages),
            onItemClick = { position -> navigateToVoyageDetails(filteredVoyages[position]) }
        )
        recyclerView.adapter = adapter

        // Set up filter button
        val filterButton: LinearLayout = findViewById(R.id.filterButtonContainer)
        filterButton.setOnClickListener { showFilterOverlay() }
    }

    private fun showFilterOverlay() {
        val filterDialog = BottomSheetDialog(this)
        val filterView = layoutInflater.inflate(R.layout.filter_overlay, null)
        filterDialog.setContentView(filterView)

        val destinationField: Spinner = filterView.findViewById(R.id.spinnerDestination)
        val priceMinField: EditText = filterView.findViewById(R.id.editTextPriceMin)
        val priceMaxField: EditText = filterView.findViewById(R.id.editTextPriceMax)
        val typeField: Spinner = filterView.findViewById(R.id.spinnerType)
        val dateStartField: EditText = filterView.findViewById(R.id.editTextDateStart)
        val dateEndField: EditText = filterView.findViewById(R.id.editTextDateEnd)
        val applyButton: Button = filterView.findViewById(R.id.buttonApplyFilters)

        // Populate destination spinner
        val destinations = listOf("All") + voyages.map { it.title }.distinct()
        val destinationAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations)
        destinationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        destinationField.adapter = destinationAdapter

        // Populate type spinner
        val types = listOf("All", "Adventure", "Relaxation", "Cultural", "Historical")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeField.adapter = typeAdapter

        applyButton.setOnClickListener {
            val selectedDestination = destinationField.selectedItem.toString()
            val minPrice = priceMinField.text.toString().toIntOrNull()
            val maxPrice = priceMaxField.text.toString().toIntOrNull()
            val selectedType = typeField.selectedItem.toString()
            val startDate = dateStartField.text.toString()
            val endDate = dateEndField.text.toString()

            applyFilters(selectedDestination, minPrice, maxPrice, selectedType, startDate, endDate)
            filterDialog.dismiss()
        }

        filterDialog.show()
    }

    private fun applyFilters(
        destination: String,
        priceMin: Int?,
        priceMax: Int?,
        type: String,
        dateStart: String,
        dateEnd: String
    ) {
        val dateFormatter = SimpleDateFormat("dd MMMM", Locale.getDefault())

        filteredVoyages = voyages.filter { voyage ->
            // Match destination
            val matchesDestination = destination == "All" || voyage.title.contains(destination, ignoreCase = true)

            // Match price
            val priceValue = voyage.price.replace("$", "").replace(",", "").toIntOrNull() ?: 0
            val matchesPrice = (priceMin == null || priceValue >= priceMin) &&
                    (priceMax == null || priceValue <= priceMax)

            // Match type
            val matchesType = type == "All" || voyage.description.contains(type, ignoreCase = true)

            // Match date range
            val startDate = if (dateStart.isNotEmpty()) dateFormatter.parse(dateStart) else null
            val endDate = if (dateEnd.isNotEmpty()) dateFormatter.parse(dateEnd) else null
            val matchesDate = voyage.possibleDates.any { possibleDate ->
                val parsedDate = dateFormatter.parse(possibleDate)
                (startDate == null || parsedDate >= startDate) &&
                        (endDate == null || parsedDate <= endDate)
            }

            matchesDestination && matchesPrice && matchesType && matchesDate
        }

        adapter.updateItems(mapVoyagesToAdapterData(filteredVoyages))
    }

    private fun navigateToVoyageDetails(voyage: VoyageRepository.Voyage) {
        // Code to navigate to VoyageActivity with voyage details
    }

    private fun mapVoyagesToAdapterData(voyages: List<VoyageRepository.Voyage>): List<Map<String, Any>> {
        return voyages.map { voyage ->
            mapOf(
                "voyageNameText" to voyage.title,
                "voyageImage" to voyage.imageUrl,
                "prixText" to voyage.price,
                "description" to voyage.description
            )
        }
    }
}