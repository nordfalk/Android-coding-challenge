package dk.nordfalk.snakomvejr.ui.s1_start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dk.nordfalk.snakomvejr.App
import dk.nordfalk.snakomvejr.R
import kotlinx.android.synthetic.main.s1_start_fragment.*

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.s1_start_fragment, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })

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
                activeTextView.text = "Tjenesten er slukket aktiv"
            }
        })


        activeImageView.setOnClickListener {
            App.instance.model.serviceShouldBeStarted = !App.instance.model.serviceShouldBeStarted
            App.instance.modelLiveData.value = "";
        }

    }
}