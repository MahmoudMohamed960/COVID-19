package com.example.covid_19.model.remote


data class Base(val countries_stat: MutableList<Country>)
data class Country(
    val country_name: String,
    val new_cases: String,
    val active_cases: String,
    val total_recovered: String,
    val deaths: String
)