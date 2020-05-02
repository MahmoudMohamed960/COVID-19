package com.example.covid_19.model.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CountryData::class], version = 1, exportSchema = false)
abstract class DBHandeller : RoomDatabase() {
    abstract fun countryDao(): DAO

    companion object {
        @Volatile
        private var INSTANCE: DBHandeller? = null

        fun getDatabase(context: Context): DBHandeller? {
            if (INSTANCE == null) {
                synchronized(DBHandeller::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            DBHandeller::class.java, "Corona_DB"
                        )
                            .build()
                    }
                }
            }
            return INSTANCE
        }
    }
}