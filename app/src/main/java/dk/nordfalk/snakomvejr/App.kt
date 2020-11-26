package dk.nordfalk.snakomvejr

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import dk.nordfalk.snakomvejr.model.SOVModel
import dk.nordfalk.snakomvejr.service.SOVService

class App : Application() {

    var model = SOVModel()
    val modelLiveData = MutableLiveData<String>()

    companion object {
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this


        // Hent tidligere data persisteret som JSON i Prefs
        val gson = Gson()
        val json = PreferenceManager.getDefaultSharedPreferences(this).getString("model", null)
        if (json!=null) {
            model = gson.fromJson(json, SOVModel::class.java)
        }

        model.audioPermissionOk =
            ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED


        modelLiveData.observeForever {
            println("model = ${model.audioPermissionOk} ${model.serviceShouldBeStarted} ${SOVService.state.isRunning}")
            //Exception().printStackTrace()
            if (model.serviceShouldBeStarted && model.audioPermissionOk && !SOVService.state.isRunning) {
                println("App startService")
                startService(Intent(this, SOVService::class.java))
            }

            if (SOVService.state.isRunning && !model.serviceShouldBeStarted) {
                println("App stopService")
                stopService(Intent(this, SOVService::class.java))
            }
        }

        modelLiveData.value = "" // gennemtving at alle observatører får en ændring ved start


    }

    fun onStopCalled() {
        val gson = Gson()
        val json = gson.toJson(model)
        println("JSON streng = $json")
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("model", json).apply()
    }

}