package com.example.covid_19.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.covid_19.model.local.CountryResponse
import com.example.covid_19.model.repo.Repositiory

class MainViewModel(application: Application) : AndroidViewModel(application) {
    var countryResponse = MutableLiveData<CountryResponse>()
    var repo = Repositiory(application)

    fun getRemoteData() {
        repo.getCountriesState()
        getLocalData()
    }

    fun getLocalData(): MutableLiveData<CountryResponse> {
        countryResponse.postValue(repo.getCountriesLocal())
        var data=repo.getWorldState()
        countryResponse.postValue(data)
        return countryResponse
    }

}