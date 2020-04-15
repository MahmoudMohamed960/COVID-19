package com.example.covid_19.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setCountry(country: CountryData)

    @Query("SELECT * from world_table ORDER BY id ASC")
    fun getAllCountries() : MutableList<CountryData>

    @Query("DELETE FROM world_table")
    fun deleteAll()
}