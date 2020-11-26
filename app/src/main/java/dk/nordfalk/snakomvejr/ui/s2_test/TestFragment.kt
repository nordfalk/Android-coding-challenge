package dk.nordfalk.snakomvejr.ui.s2_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import dk.nordfalk.snakomvejr.R
import dk.nordfalk.snakomvejr.service.SOVSpeechListener
import dk.nordfalk.snakomvejr.service.WeatherDataFetcher
import dk.nordfalk.snakomvejr.service.SOVTextToSpeech
import kotlinx.android.synthetic.main.s2_test_fragment.*


/**
 * Beregnet til at prøve tjenesten i forgrunden
 */
class TestFragment : Fragment() {


    private var latest: Long = 0
    private lateinit var tts: SOVTextToSpeech

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.s2_test_fragment, container, false)

        val textView2 : TextView = root.findViewById(R.id.textView2)
        val micButton : ImageView = root.findViewById(R.id.button)


        val vejrSpeechListener = SOVSpeechListener(requireActivity());

        vejrSpeechListener.speechDataCallback = SOVSpeechListener.Callback {
            textView2.setText(it)
            textView2.setHint(if (it==null || it.length==0) "Listening..." else "")
            println("speechDataCallback $it")
            if (it?.toLowerCase()!!.contains("vejr")) {
                vejrSpeechListener.stop()
                startVejr()
            }
        }
        vejrSpeechListener.recognitionEndCallback = SOVSpeechListener.Callback {
            micButton.setImageResource(R.drawable.ic_baseline_mic_48)
        }

        micButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.ic_baseline_hearing_48)
                    micButton.animate().scaleX(1.5f).scaleY(1.5f).setInterpolator(OvershootInterpolator())
                        .withEndAction {
                            micButton.animate().scaleX(1f).scaleY(1f).setInterpolator(OvershootInterpolator())
                        }
                    vejrSpeechListener.start();
                }
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    vejrSpeechListener.stop();
                    micButton.animate().scaleX(1f).scaleY(1f).setDuration(100)
                }
                return false
            }
        })

        tts = SOVTextToSpeech(requireContext());


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherAskedImageView.visibility = View.INVISIBLE
        weatherAskedTextView.visibility = View.INVISIBLE
        weatherResultTextView.visibility = View.INVISIBLE
    }

    private fun startVejr() {
        val now = System.currentTimeMillis();
        if (now - latest<2000) return  //undgå flere kald kort tid efter hinanden
        latest = now

        val w = WeatherDataFetcher()
        weatherAskedImageView.visibility = View.VISIBLE
        weatherAskedImageView.alpha = 0f;
        weatherAskedImageView.animate().alpha(1f)
        weatherAskedTextView.visibility = View.VISIBLE
        weatherResultTextView.visibility = View.VISIBLE
        weatherResultTextView.text = getString(R.string.getting)

        w.getWeatherData {
            println(it)
            weatherResultTextView.post {
                if (it.description != null) {
                    weatherResultTextView.text = it.description
                    tts.speak(getString(R.string.weather_now_is) +" ${it.description}")
                } else {
                    weatherAskedTextView.text = getString(R.string.error_network)
                }
            }
        }
    }
}