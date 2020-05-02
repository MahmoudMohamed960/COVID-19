package com.example.covid_19.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "world_table")
data class CountryData(
    @ColumnInfo(name = "country_name")
    val country_name: String,
    @ColumnInfo(name = "new_cases")
    val new_cases: String,
    @ColumnInfo(name = "active_cases")
    val active_cases: String,
    @ColumnInfo(name = "total_recovered")
    val total_recovered: String,
    @ColumnInfo(name = "deaths")
    val deaths: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0
}
