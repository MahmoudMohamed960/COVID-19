package com.example.covid_19.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.covid_19.Constants.Companion.SETTING_INTENT
import com.example.covid_19.Constants.Companion.SHARED_PREF
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY_POS
import com.example.covid_19.Constants.Companion.TIME_INTERVAL
import com.example.covid_19.R
import com.example.covid_19.model.local.CountryResponse
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    val timeList = (1..24).toList()
    var sharedPref: SharedPreferences? = null
    var countryName: String = ""
    var position: Int ? =null
    var time: Int = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        //initalize shared pref
        sharedPref = this.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        //set country sppinner
        setCountryList()
        //set time spinner
        setTimeList()
        done.setOnClickListener {
            var editor = sharedPref?.edit()
            editor?.putString(SUBSCRIBE_COUNTRY, countryName)
            editor?.putInt(TIME_INTERVAL, time)
            position?.let { it -> editor?.putInt(SUBSCRIBE_COUNTRY_POS, it) }
            editor?.apply()
            finish()
        }

    }

    private fun setCountryList() {
        val responseString = intent.getStringExtra(SETTING_INTENT)
        var response = GsonBuilder().create().fromJson(responseString, CountryResponse::class.java)
        //check on response
        if (response?.list != null) {
            var countryList = ArrayList<String>()
            for (item in response.list!!) {
                countryList.add(item.country_name)
            }
            val counrtyAdapter = ArrayAdapter(
                this, android.R.layout.simple_spinner_dropdown_item,
                countryList
            )
            country_spinner.adapter = counrtyAdapter

            position = sharedPref?.getInt(SUBSCRIBE_COUNTRY_POS, 0)
            position?.let {
                country_spinner.setSelection(it)
            }

            country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    countryName = countryList.get(p2)
                    position = p2
                }
            }

        }
    }

    private fun setTimeList() {
        val timeAdapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            timeList
        )
        time_spinner.adapter = timeAdapter
        time_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                time = timeList.get(p2)

            }
        }

    }
}


