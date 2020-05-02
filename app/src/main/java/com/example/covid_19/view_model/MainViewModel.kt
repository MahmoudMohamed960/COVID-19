package com.example.covid_19.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.covid_19.Constants.Companion.NETWORK_ERROR_MSG
import com.example.covid_19.model.local.CountryResponse
import com.example.covid_19.model.remote.Base
import com.example.covid_19.model.remote.Country
import com.example.covid_19.model.remote.WorldData
import com.example.covid_19.model.repo.Repositiory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var countryResponse = MutableLiveData<CountryResponse>()
    var response = CountryResponse()
    var worldData = MutableLiveData<WorldData>()
    var repo = Repositiory(application)
    private var allCountryDispose = CompositeDisposable()
    private var worldDispose = CompositeDisposable()
    private var list: MutableList<Country>? = null

    //countries data
    fun getLocalData(): MutableLiveData<CountryResponse>? {
        runBlocking<Unit> {
            launch {
                response = repo.getCountriesBG()
                countryResponse.postValue(response)
            }
        }
        return countryResponse
    }
    //update data on Room
    fun getRemoteCountriesData(): CountryResponse {
        updateCountriesData()
        return response
    }
    //first delete all data on room
    fun deleteAllCountries() = runBlocking<Unit> {
        launch {
            repo.deleteAll()
        }
    }
    //add new data
    private fun updateCountriesData() {
        allCountryDispose.add(
            repo.getCountriesState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse)
                {
                    response.error = NETWORK_ERROR_MSG
                    response.list = null
                }
        )
    }

    fun handleResponse(data: Base) {
        list = data.countries_stat
        deleteAllCountries()
        list?.let {
            for (item in it) {
                if (!item.country_name.equals(""))
                    repo.setCountry(item)
            }
        }
        allCountryDispose.clear()
    }

    //world data
    fun getRemoteWordData(): MutableLiveData<WorldData>? {
        updateWorldData()
        return worldData
    }
    fun updateWorldData() {
        worldDispose.add(
            repo.getWorldState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleWorldResponse)
                {

                }
        )
    }

    fun handleWorldResponse(data: WorldData) {
        worldData.postValue(data)
        worldDispose.clear()
    }

}