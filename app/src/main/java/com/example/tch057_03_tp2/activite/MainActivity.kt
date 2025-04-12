package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.myapplication_sqllite.sqlite.DbUtil
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.example.tch057_03_tp2.R
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

        //val nbReservationText: TextView = findViewById(R.id.nb_reservation_text)
        //val dbHelper = DbUtil(this)
        //val reservationCount = dbHelper.getReservationCount()
        //nbReservationText.text = reservationCount.toString()


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

        val destinationField: Spinner = filterView.findViewById(R.id.spinnerCountry)
        val priceMinField: EditText = filterView.findViewById(R.id.editTextPriceMin)
        val priceMaxField: EditText = filterView.findViewById(R.id.editTextPriceMax)
        val typeField: Spinner = filterView.findViewById(R.id.spinnerType)
        val dayStartField: Spinner = filterView.findViewById(R.id.spinnerDayStart)
        val monthStartField: Spinner = filterView.findViewById(R.id.spinnerMonthStart)
        val dayEndField: Spinner = filterView.findViewById(R.id.spinnerDayEnd)
        val monthEndField: Spinner = filterView.findViewById(R.id.spinnerMonthEnd)
        val applyButton: Button = filterView.findViewById(R.id.buttonApplyFilters)

        // Populate spinners
        val days = listOf("Any") + (1..31).map { it.toString() }
        val months = listOf("Any") + listOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre")

        ArrayAdapter(this, android.R.layout.simple_spinner_item, days).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            dayStartField.adapter = adapter
            dayEndField.adapter = adapter
        }

        ArrayAdapter(this, android.R.layout.simple_spinner_item, months).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            monthStartField.adapter = adapter
            monthEndField.adapter = adapter
        }

        val destinations = listOf("All") + voyages.map { it.country }.distinct()
        ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            destinationField.adapter = adapter
        }

        val types = listOf("All") + voyages.map { it.type }.distinct()
        ArrayAdapter(this, android.R.layout.simple_spinner_item, types).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            typeField.adapter = adapter
        }

        applyButton.setOnClickListener {
            val selectedDestination = destinationField.selectedItem.toString()
            val minPrice = priceMinField.text.toString().toIntOrNull()
            val maxPrice = priceMaxField.text.toString().toIntOrNull()
            val selectedType = typeField.selectedItem.toString()

            val startDay = if (dayStartField.selectedItem.toString() == "Any") null else dayStartField.selectedItem.toString().toIntOrNull()
            val startMonth = if (monthStartField.selectedItem.toString() == "Any") null else monthStartField.selectedItemPosition
            val endDay = if (dayEndField.selectedItem.toString() == "Any") null else dayEndField.selectedItem.toString().toIntOrNull()
            val endMonth = if (monthEndField.selectedItem.toString() == "Any") null else monthEndField.selectedItemPosition

            val startDateMillis = convertDayMonthToMilliseconds(startDay, startMonth)
            val endDateMillis = convertDayMonthToMilliseconds(endDay, endMonth)

            applyFilters(selectedDestination, minPrice, maxPrice, selectedType, startDateMillis, endDateMillis)
            filterDialog.dismiss()
        }

        filterDialog.show()
    }

    private fun applyFilters(
        destination: String,
        priceMin: Int?,
        priceMax: Int?,
        type: String,
        startDateMillis: Long?,
        endDateMillis: Long?
    ) {
        filteredVoyages = voyages.filter { voyage ->
            val matchesDestination = destination == "All" || voyage.country.contains(destination, ignoreCase = true)
            val priceValue = voyage.price;
            val matchesPrice = (priceMin == null || priceValue >= priceMin) &&
                    (priceMax == null || priceValue <= priceMax)
            val matchesType = type == "All" || voyage.type.contains(type, ignoreCase = true)

            val matchesDate = voyage.possibleDates.any { date ->
                val returnDateMillis = date + convertDurationToMilliseconds(voyage.duree)
                (startDateMillis == null || date >= startDateMillis) &&
                        (endDateMillis == null || returnDateMillis <= endDateMillis)
            }

            matchesDestination && matchesPrice && matchesType && matchesDate
        }

        adapter.updateItems(mapVoyagesToAdapterData(filteredVoyages))
    }

    private fun convertDayMonthToMilliseconds(day: Int?, month: Int?): Long? {
        if (day == null || month == null) return null
        val calendar = Calendar.getInstance()
        calendar.set(2025, month - 1, day, 0, 0, 0) // Set year to 2025 and time to 12:00 AM
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    private fun convertDurationToMilliseconds(duration: String): Long {
        val days = duration.split(" ")[0].toIntOrNull() ?: 0
        return days * 24 * 60 * 60 * 1000L // Convert days to milliseconds
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
                "countryText" to voyage.country,
                "placeText" to voyage.place,
                "voyageImage" to voyage.imageUrl,
                "possibleDates" to voyageRepository.convertLongListToDateStringList(voyage.possibleDates),
                "prixText" to "%.0f$".format(voyage.price),
                "description" to voyage.description
            )
        }
    }
}