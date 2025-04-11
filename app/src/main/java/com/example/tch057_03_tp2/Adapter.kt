package com.example.tch057_03_tp2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class Adapter(context: Context, private val mData: List<String>) :
    RecyclerView.Adapter<Adapter.ViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.voyage_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val txt = mData[position]
        holder.myTextView.text = txt
        Log.d("Adapter", "Binding: $txt")
    }

    override fun getItemCount(): Int = mData.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val myTextView: TextView = itemView.findViewById(R.id.voyageNameText)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            mClickListener?.onItemClick(view, adapterPosition)
        }
    }

    fun getItem(id: Int): String = mData[id]

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener) {
        this.mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}