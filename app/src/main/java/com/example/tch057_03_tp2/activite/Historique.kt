package com.example.tch057_03_tp2.activite

import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.ImageView
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
import com.example.tch057_03_tp2.sqlite.Reservation

class Historique : AppCompatActivity() {

    private lateinit var dbHelper: DbUtil
    private lateinit var reservations: MutableList<Reservation>
    private lateinit var items: MutableList<MutableMap<String, Any>>
    private lateinit var adapter: GenericAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historique)

        dbHelper = DbUtil(this)
        if (!dbHelper.checkDatabase()) {
            dbHelper.writableDatabase.close()
        }

        val sharedPref = getSharedPreferences("VoyageVoyagePrefs", MODE_PRIVATE)
        val nomUtilisateur = sharedPref.getString("nom_utilisateur", "Utilisateur")
        val nomUtilisateurText = findViewById<TextView>(R.id.nom_utilisateur_text)
        nomUtilisateurText.text = nomUtilisateur

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.historiqueRecyclerView)
        loadReservations()

        adapter = GenericAdapter(
            context = this,
            layoutId = R.layout.item_historique,
            items = items,
            onItemClick = {}
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Historique)
            adapter = this@Historique.adapter
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(true)
            isNestedScrollingEnabled = true

            addItemDecoration(
                androidx.recyclerview.widget.DividerItemDecoration(
                    this@Historique,
                    LinearLayoutManager.VERTICAL
                ).apply {
                    setDrawable(ContextCompat.getDrawable(this@Historique, R.drawable.white_line)!!)
                }
            )

            addOnChildAttachStateChangeListener(object :
                RecyclerView.OnChildAttachStateChangeListener {
                override fun onChildViewAttachedToWindow(view: View) {
                    val position = getChildAdapterPosition(view)
                    if (position != RecyclerView.NO_POSITION) {
                        val item = items[position]
                        val status = item["status_part"] as String
                        val statusText = view.findViewById<TextView>(R.id.status_text)
                        val fullText = "Statut : $status"

                        val spannable = SpannableString(fullText)
                        val startIndex = fullText.indexOf(status)
                        val colorRes = if (status.lowercase() == "confirmée") R.color.success_green else R.color.error_red
                        spannable.setSpan(
                            ForegroundColorSpan(ContextCompat.getColor(this@Historique, colorRes)),
                            startIndex,
                            fullText.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        statusText.text = spannable

                        val cancelButton = view.findViewById<Button>(R.id.cancel_button)
                        val isCanceled = item["is_canceled"] as Boolean
                        cancelButton.visibility = if (isCanceled) View.GONE else View.VISIBLE

                        cancelButton.setOnClickListener {
                            val resId = reservations[position].id
                            val success = dbHelper.cancelReservation(resId)
                            if (success) {
                                item["status_part"] = "Annulée"
                                item["is_canceled"] = true
                                reservations[position] = reservations[position].copy(status = "Annulée")
                                this@Historique.adapter.notifyItemChanged(position)
                                Toast.makeText(this@Historique, "Réservation annulée", Toast.LENGTH_SHORT).show()
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
            startActivity(Intent(this@Historique, MainActivity::class.java))
            finish()
        }
    }

    private fun loadReservations() {
        try {
            reservations = dbHelper.getAllReservations().toMutableList()
            items = reservations.map { res ->
                val bookingDate = res.booking_date.split(" ").firstOrNull() ?: res.booking_date
                mutableMapOf<String, Any>(
                    "voyageVersText" to "Voyage vers",
                    "destinationText" to res.destination,
                    "voyage_date_text" to res.travel_date,
                    "reservation_date_text" to "Réservé le $bookingDate",
                    "price_text" to "Prix : ${res.price.toInt()}$",
                    "status_text" to "Statut : ${res.status}",
                    "status_part" to res.status,
                    "is_canceled" to (res.status.equals("Annulée", ignoreCase = true))
                )
            }.toMutableList()

            if (reservations.isEmpty()) {
                Toast.makeText(this, "Aucune réservation trouvée", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors du chargement des réservations", Toast.LENGTH_SHORT).show()
            items = mutableListOf()
        }
    }
}
