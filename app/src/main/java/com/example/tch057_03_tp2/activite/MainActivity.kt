package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tch057_03_tp2.R
import com.example.tch057_03_tp2.modele.EntiteVoyage
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    private val URL = "http://10.0.2.2:3000/voyages" // Use 10.0.2.2 for Android emulator

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GenericAdapter
    private var voyages: List<EntiteVoyage> = listOf()
    private var filteredVoyages: List<EntiteVoyage> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vérification de la connexion
        val sharedPref = getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE)
        val utilisateurConnecte = sharedPref.getBoolean("connecte", false)

        if (!utilisateurConnecte) {
            // Redirection vers l'écran de connexion
            val intent = Intent(this, Connexion::class.java)
            startActivity(intent)
            finish() // Terminer l'activité actuelle pour éviter le retour en arrière
        } else {
            setContentView(R.layout.activity_main)

            // Récupérer le nom de l'utilisateur
            val nomUtilisateur = sharedPref.getString("nom_utilisateur", "Utilisateur")

            // Mettre à jour le TextView
            val nomUtilisateurText = findViewById<TextView>(R.id.nom_utilisateur_text)
            nomUtilisateurText.text = nomUtilisateur

            // Set up RecyclerView
            recyclerView = findViewById(R.id.listVoyage)
            recyclerView.layoutManager = LinearLayoutManager(this)

            // Fetch voyages from the server
            fetchVoyages()

            // Set up filter button
            val filterButton: LinearLayout = findViewById(R.id.filterButtonContainer)
            filterButton.setOnClickListener { showFilterOverlay() }

            // Set up reservation button
            val reservationBtn: LinearLayout = findViewById(R.id.reservation_btn)
            reservationBtn.setOnClickListener {
                val intent = Intent(this, Historique::class.java)
                startActivity(intent)
            }

            val deconnexionBtn: Button = findViewById(R.id.deconnexion_btn)
            deconnexionBtn.setOnClickListener {
                val editor = sharedPref.edit()
                editor.clear() // ou remove("connecte") + remove("nom_utilisateur")
                editor.apply()

                val intent = Intent(this, Connexion::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
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
                    val voyageType = object : TypeToken<List<EntiteVoyage>>() {}.type
                    voyages = Gson().fromJson(json, voyageType)
                    filteredVoyages = voyages

                    // Update UI on the main thread
                    withContext(Dispatchers.Main) {
                        adapter = GenericAdapter(
                            context = this@MainActivity,
                            layoutId = R.layout.item_voyage,
                            items = mapVoyagesToAdapterData(filteredVoyages),
                            onItemClick = { position -> navigateToVoyageDetails(filteredVoyages[position]) }
                        )
                        recyclerView.adapter = adapter
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Erreur lors du chargement des voyages.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Erreur de connexion au serveur.", Toast.LENGTH_SHORT).show()
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

        val destinations = listOf("All") + voyages.map { it.pays }.distinct()
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
            val matchesDestination = destination == "All" || voyage.pays.contains(destination, ignoreCase = true)
            val priceValue = voyage.prix
            val matchesPrice = (priceMin == null || priceValue >= priceMin) &&
                    (priceMax == null || priceValue <= priceMax)
            val matchesType = type == "All" || voyage.type.contains(type, ignoreCase = true)

            val matchesDate = voyage.getAvailableDates().any { date ->
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

    private fun navigateToVoyageDetails(voyage: EntiteVoyage) {
        val intent = Intent(this, Voyage::class.java).apply {
            putExtra("voyageId", voyage.id)
        }
        startActivity(intent)
    }

    private fun mapVoyagesToAdapterData(voyages: List<EntiteVoyage>): List<Map<String, Any>> {
        return voyages.map { voyage ->
            mapOf(
                "countryText" to voyage.pays,
                "placeText" to voyage.destination,
                "voyageImage" to voyage.imageUrl,
                "possibleDates" to voyage.getDatesFormatted(),
                "prixText" to "%.0f$".format(voyage.prix),
                "description" to voyage.description
            )
        }
    }
}