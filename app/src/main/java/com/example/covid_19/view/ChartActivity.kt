package com.example.covid_19.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.covid_19.Constants
import com.example.covid_19.R
import com.example.covid_19.model.local.CountryData
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_chart.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue
import java.util.*

class ChartActivity : AppCompatActivity() {
    private var pieData = ArrayList<SliceValue>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
        //get data about country from Main Activity
        var jsonString = intent.getStringExtra(Constants.CHART_INTENT)
        var model = GsonBuilder().create().fromJson(jsonString, CountryData::class.java)
        //set country name text
        country_name_txt.text = model.country_name
        //pichart
        setChartData(model)
    }

    private fun setChartData(model: CountryData?) {
        //recoverd cases
        if (model?.total_recovered?.equals("N/A") == false) {
            model?.total_recovered?.replace(",", "")?.toFloat()?.let {
                SliceValue(
                    it, ContextCompat.getColor(
                        applicationContext,
                        R.color.green
                    )
                ).setLabel("Recoverd ${model?.total_recovered}")
            }?.let { pieData.add(it) }
        }
        //death cases
        model?.deaths?.replace(",", "")?.toFloat()?.let {
            SliceValue(
                it, ContextCompat.getColor(
                    applicationContext,
                    R.color.red
                )
            ).setLabel("Death ${model?.deaths}")
        }?.let { pieData.add(it) }
        //new cases
        model?.new_cases?.replace(",", "")?.toFloat()?.let {
            SliceValue(
                it, ContextCompat.getColor(
                    applicationContext,
                    R.color.dark_blue
                )
            ).setLabel("New Cases ${model?.new_cases}")
        }?.let { pieData.add(it) }

        //confirmed
        model?.active_cases?.replace(",", "")?.toFloat()?.let {
            SliceValue(
                it, ContextCompat.getColor(
                    applicationContext,
                    R.color.yellow
                )
            ).setLabel("Confirmed ${model?.active_cases}")
        }?.let { pieData.add(it) }
        //show data on pichart view
        var data = PieChartData(pieData)
        data.setHasLabels(true)
        data.valueLabelTextSize = 14
        chart.pieChartData = data
    }
}

