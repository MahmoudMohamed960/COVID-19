package com.example.covid_19.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.covid_19.Constants.Companion.WORLD_DATA
import com.example.covid_19.R
import com.example.covid_19.model.local.CountryData
import com.example.covid_19.model.remote.Country
import com.example.covid_19.model.remote.WorldData
import com.example.covid_19.view_model.MainViewModel
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var viewModel: MainViewModel? = null
    var adapter: MainAdapter? = null
    var sharedPref: SharedPreferences? = null
    val gson = Gson()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //register view model to activity
        viewModel = ViewModelProviders.of(this)[MainViewModel::class.java]
        //shared prefrence
        sharedPref = this.getPreferences(Context.MODE_PRIVATE)

        //get world cases

        // requestWorldCases()
        //get countries cases
        requestCountriesCases()

        swip_refresh.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                var editor = sharedPref?.edit()
                editor?.putString(WORLD_DATA, "")
                viewModel?.getRemoteData()
            }

        })


        swip_refresh.setColorSchemeColors(
            ContextCompat.getColor(
                applicationContext,
                R.color.mainColor
            )
        )

    }

    fun requestCountriesCases() {
        //observe on  response
        viewModel?.getLocalData()?.observe(this, Observer { response ->
            if (response.list != null) {
                if (response.list?.size == 0) {
                    viewModel?.getRemoteData()
                } else {
                    swip_refresh.setColorSchemeColors(
                        ContextCompat.getColor(
                            applicationContext,
                            R.color.greenColor
                        )
                    )
                    var data = response.list!!.filter {
                        it.country_name == "world"
                    }
                    if(data.size !=0)
                    setData(data.get(0))

                    adapter = MainAdapter(this, response.list!!)
                    country_list.adapter = adapter
                    country_list.layoutManager = LinearLayoutManager(this)
                    country_list.hasFixedSize()
                    stopProgress()
                }
            }
            if (response.error != null) {
                showError(response.error!!)
                stopProgress()
            }

        })
    }

//    private fun requestWorldCases() {
//        var worldData = sharedPref?.getString(WORLD_DATA, "")
//        if (!worldData.equals("")) {
//            var world = gson.fromJson(worldData, WorldData::class.java)
//            setData(world)
//        }
//        if (worldData.equals("")) {
//            viewModel?.getWorldData()?.observe(this, Observer { response ->
//                if (response.error != null) {
//                    showError(response.error!!)
//                }
//                if (response.world != null) {
//                    setData(response.world!!)
//                    var data = gson.toJson(response.world)
//                    var editor = sharedPref?.edit()
//                    editor?.putString(WORLD_DATA, data)
//                    editor?.apply()
//                }
//            })
//        }
//
//    }

    private fun showError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        swip_refresh.setColorSchemeColors(ContextCompat.getColor(this, R.color.redColor))
        swip_refresh.isRefreshing = false
    }

    private fun stopProgress() {
        swip_refresh.isRefreshing = false
    }

    private fun setData(data: CountryData) {
        death_cases_World.text = data.deaths
        confirmed_cases_World.text = data.active_cases
        recoverd_cases_World.text = data.total_recovered
    }

}
