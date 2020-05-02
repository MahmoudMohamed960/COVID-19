package com.example.covid_19.model.remote

data class BaseSubscribe(val country: String, val latest_stat_by_country: List<Subscribe>)
data class Subscribe(
    val country_name: String,
    val new_cases: String,
    val active_cases: String,
    val total_recovered: String,
    val new_deaths: String
)