package com.example.covid_19.model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer

import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.covid_19.R
import com.example.covid_19.model.remote.BaseSubscripe
import com.example.covid_19.model.remote.RetrofitFactory
import com.example.covid_19.model.remote.Subscripe
import com.example.covid_19.model.repo.Repositiory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

public class NotificationWorker(context: Context, pram: WorkerParameters) : Worker(context, pram) {
    private var context = context
    private var specificCountryDispose = CompositeDisposable()
    private var factory = RetrofitFactory.instance
    private var channelID = "ID"
    private var channelName = "name"
    override fun doWork(): Result {
         getSpecificCountryState("Egypt")

        return Result.success()
    }

    //get data about specific country
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

    fun getCountryRequet(data: BaseSubscripe) {
        var subscripe = Subscripe("Egypt", "100", "30", "1000", "1")
        var model = data.latest_stat_by_country.get(0)
        if (model != subscripe)
            showNotification(model)
        specificCountryDispose.clear()
    }

    private fun showNotification(data: Subscripe) {
        var notification =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel =
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            notification.createNotificationChannel(channel)
        }
        var builder = NotificationCompat.Builder(context, channelID)
            .setContentText("new data about ${data.country_name} new cases ${data.new_cases}")
            .setContentTitle("Covid-19")
            .setSmallIcon(R.drawable.ic_death)
        notification.notify(1, builder.build())
    }
}