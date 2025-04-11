package com.example.tch057_03_tp2

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class Historique : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historique)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Set up RecyclerView with GenericAdapter
        val recyclerView = findViewById<RecyclerView>(R.id.historiqueRecyclerView)

        // Create dummy data for 4 history items (mix of confirmed and canceled)
        val dummyItems = List(7) { index ->
            val isCanceled = index % 2 == 0 // Every other item is canceled for demo
            val voyageDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, index * 7) // Spread dates by weeks
            }

            val cancelDate = if (isCanceled) {
                Calendar.getInstance().apply {
                    time = voyageDate.time
                    add(Calendar.DAY_OF_YEAR, -3) // Canceled 3 days before voyage
                }
            } else null

            mutableMapOf<String, Any>().apply {
                put("voyageVersText", "Voyage vers")
                put("destinationText", "Destination ${index + 1}")
                put("voyage_date_text", formatDateForDisplay(voyageDate.time))
                put("voyage_date_raw", formatDateForStorage(voyageDate.time))
                put("reservation_date_text", "Réservé le ${formatDateForStorage(voyageDate.time)}")
                put("price_text", "Prix : ${(index + 1) * 700}\$")
                put("status_text", if (isCanceled) "Statut : Annulée" else "Statut : Confirmée")
                put("status_part", if (isCanceled) "Annulée" else "Confirmée")
                put("is_canceled", isCanceled)
                put("cancel_date", cancelDate?.let { formatDateForStorage(it.time) } ?: "")
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Historique)
            overScrollMode = View.OVER_SCROLL_NEVER
            setHasFixedSize(true)
            isNestedScrollingEnabled = true

            adapter = GenericAdapter(
                context = this@Historique,
                layoutId = R.layout.item_historique,
                items = dummyItems,
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
                        val item = dummyItems[position]

                        // 1. Handle status text coloring
                        val statusText = view.findViewById<TextView>(R.id.status_text)
                        val fullText = statusText.text.toString()
                        val coloredPart = item["status_part"] as? String

                        coloredPart?.let {
                            val spannable = SpannableString(fullText)
                            val startIndex = fullText.indexOf(it)
                            if (startIndex >= 0) {
                                val colorRes = if (it == "Confirmée") R.color.success_green else R.color.error_red
                                spannable.setSpan(
                                    ForegroundColorSpan(
                                        ContextCompat.getColor(
                                            this@Historique,
                                            colorRes
                                        )
                                    ),
                                    startIndex,
                                    startIndex + it.length,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                                statusText.text = spannable
                            }
                        }

                        // 2. Handle cancel button/status
                        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
                        val isCanceled = item["is_canceled"] as? Boolean ?: false

                        if (isCanceled) {
                            cancelButton.visibility = View.GONE  // Completely hides the button
                        } else {
                            cancelButton.text = "Annuler"
                            cancelButton.isEnabled = true
                            cancelButton.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onChildViewDetachedFromWindow(view: View) {}
            })
        }
    }

    private fun formatDateForDisplay(date: Date): String {
        return SimpleDateFormat("d MMMM yyyy", Locale.FRENCH).format(date)
    }

    private fun formatDateForStorage(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}