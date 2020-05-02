package com.example.covid_19.model.repo


import android.content.Context
import com.example.covid_19.model.local.CountryData
import com.example.covid_19.model.local.CountryResponse
import com.example.covid_19.model.local.DAO
import com.example.covid_19.model.local.DBHandeller
import com.example.covid_19.model.remote.*
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Repositiory(application: Context) {
    private var countryDAO: DAO?
    private var countryResponse = CountryResponse()
    private val factory = RetrofitFactory.instance
    private var countryData: Country? = null

    init {
        val db = DBHandeller.getDatabase(application)
        countryDAO = db?.countryDao()
    }

    //Room operations
    //add country to DB
    fun setCountry(country: Country) = runBlocking<Unit> {
        launch { setCountryBG(country.toDBData()) }
    }
    private suspend fun setCountryBG(country: CountryData) {
        withContext(Dispatchers.IO)
        {
            countryDAO?.setCountry(country)
        }
    }
    //get all countries from DB
    suspend fun getCountriesBG(): CountryResponse {
        withContext(Dispatchers.IO)
        {
            countryResponse.list = countryDAO?.getAllCountries()
            countryResponse.error = null
        }
        return countryResponse
    }
    //clear data from DB
    suspend fun deleteAll() {
        withContext(Dispatchers.IO)
        {
            countryDAO?.deleteAll()
        }
    }

    //get data about specific country local
    suspend fun getCountryLocal(name: String): Country? {
        withContext(Dispatchers.IO)
        {
            countryData = countryDAO?.getCountry(name)
        }
        return countryData
    }

    //Retrofit operations
    //get world data from API
    fun getWorldState(): Observable<WorldData> = factory.api.getWorldData()
    //get countries data from API
    fun getCountriesState(): Observable<Base> = factory.api.getAllCountries()
    //get data about specific country from API
    fun getSpecificCountry(cName: String): Observable<BaseSubscribe> = factory.api.getSpecificCountry(cName)


    //convert country model fetched from API to CountryData model to save on DB
    fun Country.toDBData() = CountryData(
        country_name = country_name,
        new_cases = new_cases,
        deaths = deaths,
        total_recovered = total_recovered,
        active_cases = active_cases
    )
}