package net.nilswilhelm.foodtracker.ui.home

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_home.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.adapters.BarChartAdapter
import net.nilswilhelm.foodtracker.adapters.BarChartClickListener
import net.nilswilhelm.foodtracker.data.Intake


class HistoryFragment : Fragment() {

    private lateinit var viewModel: HistoryViewModel
    private var adapter: BarChartAdapter? = null
    private val TAG = "HistoryFragment"
    private var height = 0

    val list = ArrayList<Intake>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = BarChartAdapter(ArrayList(), object : BarChartClickListener{
            override fun onItemClick(intake: Intake) {
                showIntakeDetails(intake)
            }

        }, requireActivity().windowManager?.defaultDisplay?.height!!)
        super.onViewCreated(view, savedInstanceState)
        barchart.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, true)
        barchart.adapter = adapter

        initViewModel()
        viewModel.fetchData(requireContext())


    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.data().observe(requireActivity(), Observer {
            if (it != null) {
                adapter?.loadNewData(it)
            }
        })
        viewModel.errorMessage().observe(requireActivity(), Observer {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showIntakeDetails(intake: Intake) {
        requireActivity().runOnUiThread {
            Toast.makeText(requireContext(), intake.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}