package dk.nordfalk.snakomvejr

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.content.pm.PackageManager.*
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val RecordAudioRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_s1, R.id.navigation_s2, R.id.navigation_s3
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO)!= PERMISSION_GRANTED) {
            checkPermission()
        }

        val editText : EditText = findViewById(R.id.text)
        val micButton : ImageView = findViewById(R.id.button)
        val vejrSpeechListener = VejrSpeechListener(this);

        vejrSpeechListener.speechDataCallback = VejrSpeechListener.Callback {
            editText.setText(it)
            editText.setHint(if (it==null || it.length==0) "Listening..." else "")
            println("speechDataCallback $it")
        }
        vejrSpeechListener.recognitionEndCallback = VejrSpeechListener.Callback {
            micButton.setImageResource(R.drawable.ic_home_black_24dp)
        }

        micButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    vejrSpeechListener.stop();
                }
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.ic_notifications_black_24dp)
                    vejrSpeechListener.start();
                }
                return false
            }
        })
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RecordAudioRequestCode
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RecordAudioRequestCode && grantResults.size > 0) {
            if (grantResults[0] == PERMISSION_GRANTED) Toast.makeText(
                this,
                "Permission Granted",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}