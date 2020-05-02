package com.example.covid_19.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.example.covid_19.Constants
import com.example.covid_19.Constants.Companion.SETTING_INTENT
import com.example.covid_19.Constants.Companion.WORLD_DATA
import com.example.covid_19.R
import com.example.covid_19.model.NotificationWorker
import com.example.covid_19.model.local.CountryResponse
import com.example.covid_19.model.remote.WorldData
import com.example.covid_19.view_model.MainViewModel
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    var viewModel: MainViewModel? = null
    var adapter: MainAdapter? = null
    var sharedPref: SharedPreferences? = null
    var worldData: String? = null
    var responseObj: CountryResponse? = null
    var time: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //register view model to activity
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        //shared prefrence
        sharedPref = this.getSharedPreferences(Constants.SHARED_PREF, Context.MODE_PRIVATE)
        //get interval
        time = sharedPref?.getInt(Constants.TIME_INTERVAL, 1)
        //setup work manager
        setupWorkManaager()
        //intilize progressbar
        progressBar.visibility = View.VISIBLE
        // make requests
        getLocalWorldData()
        getLocalData()
        //refresh data
        swip_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                var editor = sharedPref?.edit()
                editor?.putString(WORLD_DATA, "")
                editor?.apply()
                getRemoteWorldData()
                viewModel?.getRemoteCountriesData()
                getLocalData()

            }

        })
        //change swipRefresh color
        swip_refresh.setColorSchemeColors(
            ContextCompat.getColor(
                applicationContext,
                R.color.mainColor
            )
        )
        //go to setting activity
        setting_icon.setOnClickListener { goToSetting() }
    }
   //move to setting activity
    private fun goToSetting() {
        var go = Intent(this, SettingActivity::class.java)
        var jsonString = GsonBuilder().create().toJson(responseObj)
        go.putExtra(SETTING_INTENT, jsonString)
        startActivity(go)
    }

    //update data by work manager
    private fun setupWorkManaager() {
        val work = createWorkRequest(Data.EMPTY)
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("Smart work", ExistingPeriodicWorkPolicy.KEEP, work)

    }

    //make constraints on work manager
    fun createConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .setRequiresBatteryNotLow(true)
        .setRequiresStorageNotLow(true)
        .build()
    //make Work request
    fun createWorkRequest(data: Data) =
        PeriodicWorkRequestBuilder<NotificationWorker>(time!!.toLong(), TimeUnit.MINUTES)
            // set input data for the work
            .setInputData(data)
            .setConstraints(createConstraints())
            // setting a backoff on case the work needs to retry
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                PeriodicWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

    //show data on UI
    //countries data
    fun getLocalData() {
        //observe on  response
        viewModel?.getLocalData()?.observe(this, Observer { response ->
            if (response.list != null) {
                if (response.list!!.size == 0) {
                    viewModel?.getRemoteCountriesData()
                    viewModel?.getLocalData()
                }
                swip_refresh.setColorSchemeColors(
                    ContextCompat.getColor(
                        applicationContext,
                        R.color.green
                    )
                )
                if (response.list!!.size > 0)
                    progressBar.visibility = View.INVISIBLE
                setAdapter(response)
                stopProgress()
                responseObj = response

            }
            if (response.error != null) {
                showError(response.error!!)
                stopProgress()
            }

        })
    }
   //World data
    private fun getLocalWorldData() {
        worldData = sharedPref?.getString(WORLD_DATA, "")
        if (!worldData.equals("")) {
            var data = worldData?.split("/")
            var world = WorldData(data!!.get(0), data!!.get(1), data!!.get(2))
            setData(world)
        } else {
            getRemoteWorldData()
        }

    }

    private fun getRemoteWorldData() {
        viewModel?.getRemoteWordData()?.observe(this, Observer { response ->
            if (!response.total_cases.equals("0")) {
                setData(response)
                var editor = sharedPref?.edit()
                var data =
                    response.total_cases + "/" + response.total_recovered + "/" + response.total_deaths
                editor?.putString(WORLD_DATA, data)
                editor?.apply()
            }

        })
    }
  //show error messege
    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        swip_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.redColor))
        swip_refresh.isRefreshing = false
        progressBar.visibility = View.INVISIBLE
    }
   //disapear progress bar
    private fun stopProgress() {
        swip_refresh.isRefreshing = false
    }
    //set data on recycler view
    private fun setAdapter(response: CountryResponse) {
        adapter = MainAdapter(this, response.list!!)
        country_list.adapter = adapter
        country_list.layoutManager = LinearLayoutManager(this)
        country_list.hasFixedSize()
    }
    //set data on world data screen
    private fun setData(data: WorldData) {
        death_cases_World.text = data.total_deaths
        confirmed_cases_World.text = data.total_cases
        recovered_cases_World.text = data.total_recovered
    }
}
