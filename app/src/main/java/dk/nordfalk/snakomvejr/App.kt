package dk.nordfalk.snakomvejr

import android.app.Application
import androidx.lifecycle.MutableLiveData
import dk.nordfalk.snakomvejr.model.SnakOmVejrModel

class App : Application() {

    val model = SnakOmVejrModel()
    val modelLiveData = MutableLiveData<String>()

    companion object {
        lateinit var instance: App
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}