package com.example.tch057_03_tp2.activite

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication_sqllite.sqlite.DbUtil
import com.example.tch057_03_tp2.R
import java.text.SimpleDateFormat
import android.content.Intent
import android.widget.ImageView
import java.util.*

class Historique : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historique)

        val sharedPref = getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE)
        val nomUtilisateur = sharedPref.getString("nom_utilisateur", "Utilisateur")

        // Mettre à jour le TextView
        val nomUtilisateurText = findViewById<TextView>(R.id.nom_utilisateur_text)
        nomUtilisateurText.text = nomUtilisateur


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView with GenericAdapter
        val recyclerView = findViewById<RecyclerView>(R.id.historiqueRecyclerView)

        val dbHelper = DbUtil(this)
        if (!dbHelper.checkDatabase()) {
            // Créer la base de données si elle n'existe pas
            dbHelper.writableDatabase.close()
        }
        val reservations = dbHelper.getAllReservations()

        val items = reservations.map { res ->
            // Formater la date de réservation (enlève l'heure si elle existe)
            val bookingDate = if (res.booking_date.contains(" ")) {
                res.booking_date.split(" ")[0] // Prend seulement la partie date
            } else {
                res.booking_date
            }

            mutableMapOf<String, Any>(
                "voyageVersText" to "Voyage vers",
                "destinationText" to res.destination,
                "voyage_date_text" to res.travel_date,
                "reservation_date_text" to "Réservé le $bookingDate",
                "price_text" to "Prix : ${res.price.toInt()}$",
                "status_text" to "Statut : ${res.status}",
                "status_part" to res.status,
                "is_canceled" to (res.status.lowercase() == "annulée")
            )
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Historique)
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(true)
            isNestedScrollingEnabled = true

            adapter = GenericAdapter(
                context = this@Historique,
                layoutId = R.layout.item_historique,
                items = items,
                onItemClick = { position ->
                    // Handle item click if needed
                }
            )

            // Add divider between items
            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    this@Historique,
                    LinearLayoutManager.VERTICAL
                ).apply {
                    setDrawable(
                        ContextCompat.getDrawable(
                            this@Historique,
                            R.drawable.white_line
                        )!!
                    )
                }
            )

            // Handle view customizations after views are created
            addOnChildAttachStateChangeListener(object :
                RecyclerView.OnChildAttachStateChangeListener {

                override fun onChildViewAttachedToWindow(view: View) {
                    val position = getChildAdapterPosition(view)
                    if (position != RecyclerView.NO_POSITION) {
                        val item = items[position]

                        // 1. Met à jour le texte du statut
                        val status = item["status_part"] as? String ?: ""
                        val statusText = view.findViewById<TextView>(R.id.status_text)
                        val fullText = "Statut : $status"
                        statusText.text = fullText

                        // 2. Applique la couleur
                        val spannable = SpannableString(fullText)
                        val startIndex = fullText.indexOf(status)
                        if (startIndex >= 0) {
                            val colorRes = if (status.lowercase() == "confirmée") R.color.success_green else R.color.error_red
                            spannable.setSpan(
                                ForegroundColorSpan(ContextCompat.getColor(this@Historique, colorRes)),
                                startIndex,
                                startIndex + status.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                            statusText.text = spannable
                        }

                        // 2. Handle cancel button/status
                        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
                        val isCanceled = item["is_canceled"] as? Boolean ?: false

                        cancelButton.visibility = if (isCanceled) View.GONE else View.VISIBLE

                        cancelButton.setOnClickListener {
                            val dbHelper = DbUtil(this@Historique)
                            val reservation = reservations[position]  // Récupère la vraie réservation

                            val success = dbHelper.cancelReservation(reservation.id)
                            if (success) {
                                item["status_part"] = "Annulée"
                                item["is_canceled"] = true

                                // Met à jour les vues
                                val newFullText = "Statut : Annulée"
                                val newSpannable = SpannableString(newFullText)
                                val newColor = ContextCompat.getColor(this@Historique, R.color.error_red)
                                newSpannable.setSpan(
                                    ForegroundColorSpan(newColor),
                                    "Statut : ".length,
                                    newFullText.length,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                statusText.text = newSpannable
                                cancelButton.visibility = View.GONE
                            } else {
                                Toast.makeText(this@Historique, "Erreur lors de l'annulation", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {}
            })
        }
        val homeBtn = findViewById<ImageView>(R.id.homeBtn)
        homeBtn.setOnClickListener {
            val intent = Intent(this@Historique, MainActivity::class.java)
            startActivity(intent)
            finish() // Facultatif : ferme l'activité actuelle si tu ne veux pas revenir avec le bouton "back"
        }

    }

    private fun formatDateForDisplay(date: Date): String {
        return SimpleDateFormat("d MMMM yyyy", Locale.FRENCH).format(date)
    }

    private fun formatDateForStorage(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}