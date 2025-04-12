package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.tch057_03_tp2.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var voyageRepository: VoyageRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GenericAdapter
    private lateinit var voyages: List<VoyageRepository.Voyage>
    private var filteredVoyages: List<VoyageRepository.Voyage> = listOf()

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

        // Set up reservation button
        val reservationBtn: LinearLayout = findViewById(R.id.reservation_btn)
        reservationBtn.setOnClickListener {
            val intent = Intent(this, Historique::class.java)
            startActivity(intent)
        }
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
            val minPrice = priceMinField.text.toString().toDoubleOrNull()
            val maxPrice = priceMaxField.text.toString().toDoubleOrNull()
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
        priceMin: Double?,
        priceMax: Double?,
        type: String,
        dateStart: String,
        dateEnd: String
    ) {
        val dateFormatter = SimpleDateFormat("dd MMMM", Locale.getDefault())

        filteredVoyages = voyages.filter { voyage ->
            val matchesDestination = destination == "All" || voyage.title.contains(destination, ignoreCase = true)
            val matchesPrice = (priceMin == null || voyage.price >= priceMin) &&
                    (priceMax == null || voyage.price <= priceMax)
            val matchesType = type == "All" || voyage.description.contains(type, ignoreCase = true)
            val startDate = try {
                if (dateStart.isNotEmpty()) dateFormatter.parse(dateStart) else null
            } catch (e: ParseException) {
                null
            }
            val endDate = try {
                if (dateEnd.isNotEmpty()) dateFormatter.parse(dateEnd) else null
            } catch (e: ParseException) {
                null
            }

            val matchesDate = voyage.possibleDates.any { possibleDate ->
                val parsedDate = try {
                    dateFormatter.parse(possibleDate)
                } catch (e: ParseException) {
                    null
                }
                parsedDate != null &&
                        (startDate == null || parsedDate >= startDate) &&
                        (endDate == null || parsedDate <= endDate)
            }

            matchesDestination && matchesPrice && matchesType && matchesDate
        }

        adapter.updateItems(mapVoyagesToAdapterData(filteredVoyages))
    }

    private fun navigateToVoyageDetails(voyage: VoyageRepository.Voyage) {
        val intent = Intent(this, Voyage::class.java).apply {
            putExtra("voyageId", voyage.id)
        }
        startActivity(intent)
    }

    private fun mapVoyagesToAdapterData(voyages: List<VoyageRepository.Voyage>): List<Map<String, Any>> {
        return voyages.map { voyage ->
            mapOf(
                "voyageNameText" to voyage.title,
                "voyageImage" to voyage.imageUrl,
                "prixText" to "%.0f$".format(voyage.price),
                "description" to voyage.description
            )
        }
    }
}