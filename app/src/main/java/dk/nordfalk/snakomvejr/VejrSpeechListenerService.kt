package dk.nordfalk.snakomvejr

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class VejrSpeechListenerService : Service() {
    val TAG = javaClass.name

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
        }
    }

    override fun onDestroy() {
        Toast.makeText(this, "$TAG onDestroy", Toast.LENGTH_LONG).show()
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
        return START_STICKY // evt START_REDELIVER_INTENT hvis intentdata skal huskes i fald at processen bliver genstartet
    }


}