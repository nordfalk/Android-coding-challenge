package dk.nordfalk.snakomvejr.ui.s1_start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dk.nordfalk.snakomvejr.App
import dk.nordfalk.snakomvejr.R
import kotlinx.android.synthetic.main.s1_start_fragment.*

class HomeFragment : Fragment() {


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.s1_start_fragment, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        App.instance.modelLiveData.observe(viewLifecycleOwner, {
            if (App.instance.model.serviceShouldBeStarted) {
                activeImageView.setImageResource(R.drawable.ic_baseline_cloud_queue_48)
                activeTextView.text = "Tjenesten er aktiv"
            } else {
                activeImageView.setImageResource(R.drawable.ic_baseline_cloud_off_48)
                activeTextView.text = "Tjenesten er slukket"
            }
        })


        activeImageView.setOnClickListener {
            App.instance.model.serviceShouldBeStarted = !App.instance.model.serviceShouldBeStarted
            App.instance.modelLiveData.value = "";
        }

    }
}