package com.example.covid_19.view_model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.covid_19.model.local.CountryResponse
import com.example.covid_19.model.remote.WorldData
import com.example.covid_19.model.repo.Repositiory

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var countryResponse = MutableLiveData<CountryResponse>()
    var worldData = MutableLiveData<WorldData>()
    var repo = Repositiory(application)

    fun getRemoteData(): MutableLiveData<WorldData> {
        repo.getCountriesState()
        worldData.postValue(repo.getWorldState())
        return worldData
    }

    fun getLocalData(): MutableLiveData<CountryResponse> {
        countryResponse.postValue(repo.getCountriesLocal())
        return countryResponse
    }

}