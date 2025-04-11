package com.example.tch057_03_tp2

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GenericAdapter(
    private val context: Context,
    private val layoutId: Int, // Layout resource for list item
    private val items: List<Map<String, Any>>, // List of data maps for each list item
    private val onItemClick: (position: Int) -> Unit // Lambda for item click handling
) : RecyclerView.Adapter<GenericAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataMap = items[position]

        // Dynamically bind data to views
        for ((key, value) in dataMap) {
            val view = holder.itemView.findViewById<View>(
                context.resources.getIdentifier(key, "id", context.packageName)
            )

            when (view) {
                is TextView -> view.text = value.toString() // Set text for TextView
                is ImageView -> {
                    // Load image into ImageView using Glide
                    Glide.with(context)
                        .load(value.toString())
                        .placeholder(R.drawable.placeholder_image) // Optional placeholder
                        .error(R.drawable.error_image) // Optional error image
                        .into(view)
                }
            }
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}