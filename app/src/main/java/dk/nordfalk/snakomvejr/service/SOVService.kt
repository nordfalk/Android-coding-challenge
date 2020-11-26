package dk.nordfalk.snakomvejr.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import dk.nordfalk.snakomvejr.MainActivity
import dk.nordfalk.snakomvejr.R

class SOVService : Service() {
    private var latest: Long = 0
    private val TAG = javaClass.name
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var tts: SOVTextToSpeech
    private lateinit var sovSpeechListener: SOVSpeechListener

    object state {
        var isRunning = false

        val liveData = MutableLiveData<String>()
        var isListening = false
    }

    /**
     * Service-mekanik. Ligegyldig da vi kører i samme proces og ikke ønsker at binde til denne service
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        Toast.makeText(this, "$TAG onCreate", Toast.LENGTH_LONG).show()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "$TAG onCreate mangler tilladelser - stopper", Toast.LENGTH_LONG).show()
            stopSelf();
        } else {
            state.isRunning = true
        }
        setup()
    }

    override fun onDestroy() {
        Toast.makeText(this, "$TAG onDestroy", Toast.LENGTH_LONG).show()
        state.isRunning = false
        sovSpeechListener.stop()
        sovSpeechListener.destroy()
        tts.destroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "$TAG onStartCommand($flags $startId", Toast.LENGTH_LONG).show()
        val i = Intent(this, MainActivity::class.java)
        val KANALID = "kanal_id"
        // Fra API 26 skal man bruge en NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val kanal = NotificationChannel(KANALID, "kanalnavn", NotificationManager.IMPORTANCE_DEFAULT)
            kanal.description = "En notifikationskanal for AndroidElementer (setDescription)"
            getSystemService(NotificationManager::class.java).createNotificationChannel(kanal)
        }
        val builder = NotificationCompat.Builder(this, KANALID)
            .setContentIntent(PendingIntent.getActivity(this, 0, i, 0))
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("SnakOmVejr lytter")
            .setContentText("Tryk for at stoppe SnakOmVejr")
        startForeground(42, builder.build())

        handler.postDelayed(startSpeechListener, 1000)

        return START_STICKY // evt START_REDELIVER_INTENT hvis intentdata skal huskes i fald at processen bliver genstartet
    }


    private fun setup() {
        tts = SOVTextToSpeech(this)
        sovSpeechListener = SOVSpeechListener(this);

        sovSpeechListener.speechDataCallback = SOVSpeechListener.Callback {
            println("Service speechDataCallback $it")
            if (it?.toLowerCase()!!.contains("vejr")) {
                sovSpeechListener.stop()
                startVejr()
            }
        }
        sovSpeechListener.recognitionEndCallback = SOVSpeechListener.Callback {
            println("vejrSpeechListener.recognitionEndCallback")
            state.isListening = false
            state.liveData.value = ""
            //handler.removeCallbacks(startSpeechListener)
            handler.postDelayed( startSpeechListener, 15000)
        }
    }

    private val startSpeechListener: () -> Unit = {
        if (state.isRunning) {
            println("vejrSpeechListener.start()")
            state.isListening = true
            state.liveData.value = ""
            sovSpeechListener.start()
        }
    }

    private fun startVejr() {
        val now = System.currentTimeMillis();
        if (now - latest<2000) return  //undgå flere kald kort tid efter hinanden
        latest = now
        tts.speak("Øjeblik, henter vejret")

        val w = WeatherDataFetcher()

        w.getWeatherData {
            println("w.getWeatherData giver"+  it)
            tts.speak(getString(R.string.weather_now_is) +" ${it.description}")
        }
    }

}