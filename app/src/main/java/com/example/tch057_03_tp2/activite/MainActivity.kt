package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tch057_03_tp2.R
import com.example.tch057_03_tp2.modele.EntiteVoyage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val URL = "http://10.0.2.2:3000/voyages"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GenericAdapter
    private var voyages: List<EntiteVoyage> = listOf()
    private var filteredVoyages: List<EntiteVoyage> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE)
        val utilisateurConnecte = sharedPref.getBoolean("connecte", false)

        if (!utilisateurConnecte) {
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish()
        } else {
            setContentView(R.layout.activity_main)

            val nomUtilisateur = sharedPref.getString("nom_utilisateur", "Utilisateur")
            val nomUtilisateurText = findViewById<TextView>(R.id.nom_utilisateur_text)
            nomUtilisateurText.text = nomUtilisateur

            recyclerView = findViewById(R.id.listVoyage)
            recyclerView.layoutManager = LinearLayoutManager(this)

            fetchVoyages()

            val filterButton: LinearLayout = findViewById(R.id.filterButtonContainer)
            filterButton.setOnClickListener { showFilterOverlay() }

            val reservationBtn: LinearLayout = findViewById(R.id.reservation_btn)
            reservationBtn.setOnClickListener {
                startActivity(Intent(this, Historique::class.java))
            }

            val deconnexionBtn: Button = findViewById(R.id.deconnexion_btn)
            deconnexionBtn.setOnClickListener {
                sharedPref.edit().clear().apply()
                startActivity(Intent(this, Connexion::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
                finish()
            }
        }
    }

    private fun fetchVoyages() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = Request.Builder().url(URL).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val json = response.body?.string()
                    Log.d("API_RESPONSE", "Raw JSON: $json") // Debug log

                    val gson = Gson()
                    voyages = try {
                        // First try parsing as direct array of voyages
                        gson.fromJson(json, Array<EntiteVoyage>::class.java).toList()
                    } catch (e: JsonSyntaxException) {
                        // If that fails, try parsing as object with voyages array
                        data class ApiResponse(val voyages: List<EntiteVoyage>)
                        gson.fromJson(json, ApiResponse::class.java).voyages
                    }

                    filteredVoyages = voyages

                    withContext(Dispatchers.Main) {
                        if (voyages.isEmpty()) {
                            Toast.makeText(
                                this@MainActivity,
                                "Aucun voyage disponible",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        adapter = GenericAdapter(
                            context = this@MainActivity,
                            layoutId = R.layout.item_voyage,
                            items = mapVoyagesToAdapterData(filteredVoyages),
                            onItemClick = { position ->
                                navigateToVoyageDetails(filteredVoyages[position])
                            }
                        )
                        recyclerView.adapter = adapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Erreur serveur: ${response.code}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Erreur réseau: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("NETWORK_ERROR", "IO Error", e)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Erreur inattendue: ${e.javaClass.simpleName}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("APP_ERROR", "Parsing error", e)
                }
            }
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
        val months = listOf("Any") + listOf(
            "Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"
        )

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

        val destinations = listOf("All") + voyages.map { it.destination }.distinct()
        ArrayAdapter(this, android.R.layout.simple_spinner_item, destinations).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            destinationField.adapter = adapter
        }

        val types = listOf("All") + voyages.map { it.typeVoyage }.distinct()
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

            val startDate = convertDayMonthToDate(startDay, startMonth)
            val endDate = convertDayMonthToDate(endDay, endMonth)

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
        startDate: String?,
        endDate: String?
    ) {
        filteredVoyages = voyages.filter { voyage ->
            val matchesDestination = destination == "All" ||
                    voyage.destination.contains(destination, ignoreCase = true)

            val priceValue = voyage.prix
            val matchesPrice = (priceMin == null || priceValue >= priceMin) &&
                    (priceMax == null || priceValue <= priceMax)

            val matchesType = type == "All" ||
                    voyage.typeVoyage.contains(type, ignoreCase = true)

            val matchesDate = voyage.trips.any { trip ->
                val tripDate = trip.date
                (startDate == null || tripDate >= startDate) &&
                        (endDate == null || tripDate <= endDate)
            }

            matchesDestination && matchesPrice && matchesType && matchesDate
        }

        adapter.updateItems(mapVoyagesToAdapterData(filteredVoyages))
    }

    private fun convertDayMonthToDate(day: Int?, month: Int?): String? {
        if (day == null || month == null) return null
        return String.format("2025-%02d-%02d", month + 1, day) // YYYY-MM-DD format
    }

    private fun navigateToVoyageDetails(voyage: EntiteVoyage) {
        startActivity(Intent(this, Voyage::class.java).apply {
            putExtra("voyageId", voyage.id)
        })
    }

    private fun mapVoyagesToAdapterData(voyages: List<EntiteVoyage>): List<Map<String, Any>> {
        return voyages.map { voyage ->
            mapOf(
                "countryText" to (voyage.destination.split(", ").lastOrNull() ?: ""),
                "voyageTitle" to voyage.nomVoyage,
                "voyageImage" to voyage.imageUrl,
                "prixText" to "%.0f$".format(voyage.prix),
                "description" to voyage.description,
                "versVoyageTexts" to "Vers ${voyage.destination.split(", ").firstOrNull() ?: ""}"
            )
        }
    }
}