package com.example.covid_19.model.remote

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers

interface API {
    @Headers("x-rapidapi-key:0e56c0a4c3msh6268a2ad230fa45p11a6d1jsnde4c75363434")
    @GET("cases_by_country.php")
    fun getAllCountries() : Observable<Base>
    @Headers("x-rapidapi-key:0e56c0a4c3msh6268a2ad230fa45p11a6d1jsnde4c75363434")
    @GET("worldstat.php")
    fun getWorldData() : Observable<WorldData>

}