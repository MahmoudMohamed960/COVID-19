package com.example.covid_19.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.R
import com.example.covid_19.model.local.CountryData
import kotlinx.android.synthetic.main.countery_item.view.*

class MainAdapter(context: Context, list: List<CountryData>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    val context = context
    var list = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.countery_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size-1
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list.get(position)
        holder.itemView.country_name.text = model.country_name
        holder.itemView.recovered_cases.text = model.total_recovered
        holder.itemView.confirmed_cases.text = model.active_cases
        holder.itemView.death_cases.text = model.deaths
        holder.itemView.new_cases.text = model.new_cases
}

class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}