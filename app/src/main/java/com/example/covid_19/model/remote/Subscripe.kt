package com.example.covid_19.model.remote
data class BaseSubscripe(val country:String,val latest_stat_by_country:List<Subscripe>)
data class Subscripe(
    val country_name: String,
    val new_cases: String,
    val active_cases: String,
    val total_recovered: String,
    val total_deaths: String
)