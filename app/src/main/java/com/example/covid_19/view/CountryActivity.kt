package com.example.covid_19.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY
import com.example.covid_19.R
import com.example.covid_19.model.remote.Subscribe
import com.example.covid_19.view_model.MainViewModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_country.*

class CountryActivity : AppCompatActivity() {
    private var viewModel: MainViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country)
        var json = intent.getStringExtra(SUBSCRIBE_COUNTRY)
        var subscribe = GsonBuilder().create().fromJson(json, Subscribe::class.java)
        recovered_cases.text = subscribe.total_recovered
        new_cases.text = subscribe.new_cases
        death_cases.text = subscribe.new_deaths
        country_name.text = subscribe.country_name
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        updateData()
    }

    private fun updateData() {
        //update data
        viewModel?.getRemoteData()
    }
}
