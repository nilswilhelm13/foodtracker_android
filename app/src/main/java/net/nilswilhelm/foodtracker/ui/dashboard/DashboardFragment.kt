package net.nilswilhelm.foodtracker.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.progress_bars.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.adapters.TransactionRecyclerViewAdapter
import net.nilswilhelm.foodtracker.data.Intake
import net.nilswilhelm.foodtracker.utils.RecyclerItemCLickListener
import net.nilswilhelm.foodtracker.utils.Utils
import net.nilswilhelm.foodtracker.utils.Utils.Companion.delete
import okhttp3.internal.toHexString


class DashboardFragment : Fragment(), RecyclerItemCLickListener.OnRecyclerClickListener,
    Utils.Companion.OnDeleteListener {

    private val foodRecyclerViewAdapterTransaction = TransactionRecyclerViewAdapter(ArrayList())
    private val TAG = "DashboardFragment"
    private lateinit var viewModel: DashboardViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        return root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        viewModel.data().observe(requireActivity(), Observer {
            if (it != null) {

                val intake = it.intake
                val transactions = it.transactions

                if (intake != null){
                    buildPieChart(it.intake)
                }
                if (transactions != null){
                    foodRecyclerViewAdapterTransaction.loadNewData(transactions.toList())
                }
            }
        })
        viewModel.progressData().observe(requireActivity(), Observer {
            progress_protein_bar.progress = it.proteinProgress
            progress_protein_text.text = it.proteinProgressString
            progress_carbohydrate_bar.progress = it.carbohydrateProgress
            progress_carbohydrate_text.text = it.carbohydrateProgressString
            progress_fat_bar.progress = it.fatProgress
            progress_fat_text.text = it.fatProgressString
        })
        viewModel.errorMessage().observe(requireActivity(), Observer {
            activity?.runOnUiThread {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewModel()
        viewModel.fetchData(getString(R.string.BASE_URL) + "dashboard", requireContext())
        transaction_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        transaction_recycler_view.addOnItemTouchListener(
            RecyclerItemCLickListener(
                requireContext(),
                transaction_recycler_view,
                this
            )
        )
        transaction_recycler_view.adapter = foodRecyclerViewAdapterTransaction
        transaction_recycler_view.isNestedScrollingEnabled = false
    }

    private fun buildPieChart(intake: Intake) {

        val pieChart = view?.findViewById<PieChart>(R.id.pie_chart)

        pieChart?.setUsePercentValues(false)
        pieChart?.description?.isEnabled = false
        pieChart?.setExtraOffsets(5F, 10F, 5F, 5F)

        pieChart?.dragDecelerationFrictionCoef = 0.95f

        pieChart?.isDrawHoleEnabled = true
        pieChart?.setHoleColor(Color.parseColor(getColorString(R.color.colorEnergy)))

        pieChart?.setTransparentCircleColor(Color.WHITE)
        pieChart?.setTransparentCircleAlpha(300)

        pieChart?.holeRadius = 54f
        pieChart?.transparentCircleRadius = 56f

        // center text
        pieChart?.setDrawCenterText(true)
        pieChart?.centerText = " ${intake?.nutrition?.energy} kcal"
        pieChart?.setCenterTextSize(30F)
        pieChart?.setCenterTextColor(Color.WHITE)

        // rotation
        pieChart?.rotationAngle = 0F
        // enable rotation of the pieChart? by touch
        pieChart?.isRotationEnabled = false
        pieChart?.isHighlightPerTapEnabled = true

        // animation
        pieChart?.animateY(1400, Easing.EaseInOutQuad)

        val list = ArrayList<PieEntry>()
        list.add(PieEntry(intake?.nutrition?.protein!!.toFloat(), "Protein"))
        list.add(PieEntry(intake?.nutrition?.fat!!.toFloat(), "Fat"))
        list.add(PieEntry(intake?.nutrition?.carbohydrate!!.toFloat(), "Carbs"))


        val pieDataSet = PieDataSet(list, "Data Set 1")


        // colors
        pieDataSet.setColors(
            Color.parseColor(getColorString(R.color.colorProtein)),
            Color.parseColor(getColorString(R.color.colorFat)),
            Color.parseColor(getColorString(R.color.colorCarbohydrate))
        )
        pieDataSet.sliceSpace = 3f
        pieDataSet.iconsOffset = MPPointF(0F, 40F)
        pieDataSet.selectionShift = 5f
        val pieData = PieData(pieDataSet)

        // value styling
        pieData.setValueFormatter(PercentFormatter())
        pieData.setValueTextSize(20f)
        pieData.setValueTextColor(Color.WHITE)

        // entry label styling
        pieChart?.setEntryLabelColor(Color.WHITE)
        pieChart?.setEntryLabelTextSize(24f)

        pieChart?.data = pieData
        pieChart?.invalidate()
    }

    override fun onItemClick(view: View, position: Int) {
        Toast.makeText(requireContext(), "Hold to delete", Toast.LENGTH_SHORT).show()
    }

    override fun onItemLongClick(view: View, position: Int) {
        val transaction = foodRecyclerViewAdapterTransaction.getFood(position)
        if (transaction != null) {
            delete(
                requireContext(),
                "https://backend.nilswilhelm.net/intake/" + transaction.id,
                this
            )
        }
    }

    override fun onDeleted(responseMessage: String) {
        viewModel.fetchData(getString(R.string.BASE_URL) + "dashboard", requireContext())

    }

    override fun onDeleteFailed(errorMessage: String) {
        activity?.runOnUiThread {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    fun getColorString(id: Int): String{
        return "#" + Integer.toHexString(ContextCompat.getColor(requireContext(), id))
    }
}