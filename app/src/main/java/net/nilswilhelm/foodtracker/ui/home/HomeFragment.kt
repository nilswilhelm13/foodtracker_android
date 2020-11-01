package net.nilswilhelm.foodtracker.ui.home

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.adapters.BarChartAdapter
import net.nilswilhelm.foodtracker.data.Intake


class HomeFragment : Fragment() {

    val list = ArrayList<Intake>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
//        val adapter = BarChartAdapter(list)
//        barchart.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        barchart.adapter = adapter
        return root

    }
}