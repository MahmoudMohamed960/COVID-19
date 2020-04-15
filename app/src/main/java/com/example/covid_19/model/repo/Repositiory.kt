package com.example.covid_19.model.repo


import android.app.Application
import com.example.covid_19.Constants.Companion.NETWORK_ERROR_MSG
import com.example.covid_19.model.local.CountryResponse
import com.example.covid_19.model.local.DAO
import com.example.covid_19.model.local.DBHandeller
import com.example.covid_19.model.local.CountryData
import com.example.covid_19.model.remote.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

class Repositiory(application: Application) {
    private var countryDAO: DAO?
    private var list: List<Country>? = null
    private var countryResponse = CountryResponse()
    private val factory = RetrofitFactory.instance
    private var firstDispose = CompositeDisposable()
    private var secondDispose = CompositeDisposable()


    init {
        val db = DBHandeller.getDatabase(application)
        countryDAO = db?.countryDao()
    }

    //Room operations
    fun getCountriesLocal(): CountryResponse{
        runBlocking<Unit> {
            launch { getCountriesBG() }
        }
        return countryResponse
    }

    fun setCountry(country: CountryData) = runBlocking<Unit> {
        launch { setCountryBG(country) }
    }

    fun deleteAllCountries() = runBlocking<Unit> {
        launch { deleteAll() }
    }

    private suspend fun getCountriesBG() {
        withContext(Dispatchers.IO)
        {
            countryResponse.list = countryDAO?.getAllCountries()
            countryResponse.error = null
        }
    }

    private suspend fun setCountryBG(country: CountryData) {
        withContext(Dispatchers.IO)
        {
            countryDAO?.setCountry(country)
        }
    }

    private suspend fun deleteAll() {
        withContext(Dispatchers.IO)
        {
            countryDAO?.deleteAll()
        }
    }

    //Retrofit operations
    //world data
    fun getWorldState():CountryResponse {
        firstDispose.add(
            factory.api.getWorldData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleWorldResponse)
                {
                    countryResponse.error = NETWORK_ERROR_MSG
                }
        )
        return countryResponse
    }

    fun handleWorldResponse(data: WorldData) {
       var country = Country("world","",data.total_cases,data.total_recovered,data.total_deaths)
       setCountry(country.toDBData())
       countryResponse.list?.add(country.toDBData())
        firstDispose.clear()
    }

    //countries data
    fun getCountriesState(): List<Country>? {
        secondDispose.add(
            factory.api.getAllCountries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse)
                {
                    countryResponse.error = NETWORK_ERROR_MSG
                    countryResponse.list = null
                }


        )
        return list
    }

    fun handleResponse(data: Base) {
        list = data.countries_stat
        list?.let {
            deleteAllCountries()
            for (item in it) {
                if(!item.country_name.equals(""))
                setCountry(item.toDBData())
            }
        }
        secondDispose.clear()
    }

    //exchange
    fun Country.toDBData() = CountryData(
        country_name = country_name,
        new_cases = new_cases,
        deaths = deaths,
        total_recovered = total_recovered,
        active_cases = active_cases
    )


}