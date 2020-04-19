package com.example.covid_19.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.covid_19.Constants
import com.example.covid_19.Constants.Companion.SHARED_PREF
import com.example.covid_19.Constants.Companion.SUBSCRIBE_COUNTRY
import com.example.covid_19.R
import com.example.covid_19.model.local.DBHandeller
import com.example.covid_19.model.remote.*
import com.example.covid_19.view.CountryActivity
import com.google.gson.GsonBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import kotlin.random.Random

public class NotificationWorker(context: Context, pram: WorkerParameters) : Worker(context, pram) {
    private var context = context
    private var specificCountryDispose = CompositeDisposable()
    private var firstDispose = CompositeDisposable()
    private var factory = RetrofitFactory.instance
    private var channelID = "ID"
    private var channelName = "name"
    private var countryData: Country? = null
    var sharedPref: SharedPreferences? = null
    override fun doWork(): Result {
        sharedPref = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE)
        var name = sharedPref?.getString(SUBSCRIBE_COUNTRY, "Egypt")
        name?.let {
            getCountryData(it)
            getSpecificCountryState(it)
        }

        return Result.success()
    }

    private fun getCountryData(name: String) = runBlocking<Unit> {
        launch {
            getCountryLocal(name)
        }
    }

    //get data about specific country local
    private suspend fun getCountryLocal(name: String) {
        withContext(Dispatchers.IO)
        {
            var handler = DBHandeller.getDatabase(context)
            delay(2000)
            countryData = handler?.countryDao()?.getCountry(name)
        }

    }

    //get data about specific country remote
    fun getSpecificCountryState(cName: String) {
        specificCountryDispose.add(
            factory.api.getSpecificCountry(cName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(this::getCountryRequet)
                {
                    print("no internet connection")
                }
        )

    }

    fun getCountryRequet(data: BaseSubscribe) {
        var model = data.latest_stat_by_country.get(0)
      //  if (model != countryData?.toSubscribe() && !model.new_cases.equals(""))
            getWorldState()
            showNotification(model)
        specificCountryDispose.clear()
    }
    // get word data
    //world data
    fun getWorldState() {
        firstDispose.add(
            factory.api.getWorldData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleWorldResponse)
                {
                    print(Constants.NETWORK_ERROR_MSG)
                }
        )
    }

    fun handleWorldResponse(data: WorldData) {
        var editor = sharedPref?.edit()
        var data = data.total_cases + "/" + data.total_recovered + "/" + data.total_deaths
        editor?.putString(Constants.WORLD_DATA, data)
        editor?.apply()
        firstDispose.clear()
    }

    private fun showNotification(data: Subscribe) {
        var notification =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notification.createNotificationChannel(channel)
        }
        var openResult = Intent(context, CountryActivity::class.java)
        var json = GsonBuilder().create().toJson(data)
        openResult.putExtra(SUBSCRIBE_COUNTRY,json)
        openResult.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        var intent = PendingIntent.getActivity(context,Random.nextInt(0, 100),openResult,0)
        var ring = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(context, channelID)
            .setContentText("new data about ${data.country_name}: new cases ${data.new_cases} ,recoverd ${data.total_recovered} ,death ${data.total_deaths} ")
            .setContentTitle("Covid-19")
            .setSmallIcon(R.drawable.corona)
            .setSound(ring)
            .setContentIntent(intent)
        notification.notify(1, builder.build())


    }

    //exchange
    fun Country.toSubscribe() = Subscribe(
        country_name = country_name,
        new_cases = new_cases,
        total_deaths = deaths,
        total_recovered = total_recovered,
        active_cases = active_cases
    )
}