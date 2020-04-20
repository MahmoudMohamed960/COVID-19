package com.example.covid_19.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.covid_19.R
import lecho.lib.hellocharts.model.SliceValue
import java.util.ArrayList

class ChartActivity : AppCompatActivity() {
    private var pieData = ArrayList<SliceValue>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)
    }
}
