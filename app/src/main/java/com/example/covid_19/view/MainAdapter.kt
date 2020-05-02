package com.example.covid_19.view

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.covid_19.Constants.Companion.CHART_INTENT
import com.example.covid_19.Constants.Companion.SHARED_PREF
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY_POS
import com.example.covid_19.R
import com.example.covid_19.model.local.CountryData
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.country_item.view.*

class MainAdapter(context: Context, list: List<CountryData>) :
    RecyclerView.Adapter<MainAdapter.ViewHolder>() {
    val context = context
    var list = list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.country_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list.get(position)
        holder.itemView.country_name.text = model.country_name
        holder.itemView.recovered_cases.text = model.total_recovered
        holder.itemView.confirmed_cases.text = model.active_cases
        holder.itemView.death_cases.text = model.deaths
        holder.itemView.new_cases.text = model.new_cases
        holder.itemView.subscribe_icon.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(context.resources.getString(R.string.alretDlaog_title))
            builder.setMessage(context.resources.getString(R.string.alretDlaog_msg))
            builder.setPositiveButton("Yes") { dialogInterface, which ->
                val sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
                var editor = sharedPref.edit()
                editor.putString(SUBSCRIBE_COUNTRY, model.country_name)
                editor.putInt(SUBSCRIBE_COUNTRY_POS, position)
                editor.apply()
                holder.itemView.subscribe_icon.setImageDrawable(context.resources.getDrawable(R.drawable.ic_pinned))
            }
            builder.setNegativeButton("No") { dialogInterface, which ->
            }
            val alertDialog: AlertDialog = builder.create()
            alertDialog.setCancelable(false)
            alertDialog.show()
        }
        //go to statistics pi chart
        holder.itemView.setOnClickListener {
            var json = GsonBuilder().create().toJson(model)
            var goChart = Intent(context, ChartActivity::class.java)
            goChart.putExtra(CHART_INTENT, json)
            context.startActivity(goChart)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}