package com.example.covid_19.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY
import com.example.covid_19.R
import com.example.covid_19.model.remote.Subscribe
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_country.*

class CountryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country)
        //get data about country from Notification
        var json = intent.getStringExtra(SUBSCRIBE_COUNTRY)
        var subscribe = GsonBuilder().create().fromJson(json, Subscribe::class.java)
        //set data into textviews
        recovered_cases.text = subscribe.total_recovered
        new_cases.text = subscribe.new_cases
        death_cases.text = subscribe.new_deaths
        country_name.text = subscribe.country_name
    }
}
