package com.example.covid_19.model.local

import androidx.room.*
import com.example.covid_19.model.remote.Country

@Dao
interface DAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun setCountry(country: CountryData)
    @Transaction
    @Query("SELECT * from world_table ORDER BY id ASC")
    fun getAllCountries() : MutableList<CountryData>
    @Transaction
    @Query("DELETE FROM world_table")
    fun deleteAll()
    @Transaction
    @Query("SELECT * FROM world_table WHERE country_name= :name")
    fun getCountry(name: String):Country
}