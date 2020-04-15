package com.example.covid_19.model.local


class CountryResponse {
     var list: MutableList<CountryData>?=null
        get() {
            return field
        }
        set(value) {
            field=value
        }
     var error:String ?=null
        get() {
            return field
        }
        set(value) {
            field=value
        }
}