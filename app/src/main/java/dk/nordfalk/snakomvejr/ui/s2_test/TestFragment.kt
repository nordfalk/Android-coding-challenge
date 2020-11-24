package dk.nordfalk.snakomvejr.ui.s2_test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import dk.nordfalk.snakomvejr.R
import dk.nordfalk.snakomvejr.VejrSpeechListener
import dk.nordfalk.snakomvejr.model.WeatherData
import kotlinx.android.synthetic.main.s2_test_fragment.*

class TestFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.s2_test_fragment, container, false)

        val textView2 : TextView = root.findViewById(R.id.textView2)
        val micButton : ImageView = root.findViewById(R.id.button)


        val vejrSpeechListener = VejrSpeechListener(requireActivity());

        vejrSpeechListener.speechDataCallback = VejrSpeechListener.Callback {
            textView2.setText(it)
            textView2.setHint(if (it==null || it.length==0) "Listening..." else "")
            println("speechDataCallback $it")
            if (it?.toLowerCase()!!.contains("vejr")) {
                startVejr()
            }
        }
        vejrSpeechListener.recognitionEndCallback = VejrSpeechListener.Callback {
            micButton.setImageResource(R.drawable.ic_baseline_mic_48)
        }

        micButton.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(view: View?, motionEvent: MotionEvent): Boolean {
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    vejrSpeechListener.stop();
                    micButton.animate().scaleX(1f).scaleY(1f).setDuration(100)
                }
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    micButton.setImageResource(R.drawable.ic_baseline_hearing_48)
                    micButton.animate().scaleX(1.5f).scaleY(1.5f).setInterpolator(OvershootInterpolator())
                        .withEndAction {
                            micButton.animate().scaleX(1f).scaleY(1f).setInterpolator(OvershootInterpolator())
                        }
                    vejrSpeechListener.start();
                }
                return false
            }
        })


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        weatherAskedImageView.visibility = View.INVISIBLE
        weatherAskedTextView.visibility = View.INVISIBLE
        weatherResultTextView.visibility = View.INVISIBLE
    }

    private fun startVejr() {
        val w = WeatherData()
        weatherAskedImageView.visibility = View.VISIBLE
        weatherAskedTextView.visibility = View.VISIBLE
        weatherResultTextView.visibility = View.VISIBLE
        weatherResultTextView.text = "Henter....."

        w.getDescription({
            println(it)
            weatherResultTextView.post { weatherResultTextView.text = it }
        })

    }
}