package net.nilswilhelm.foodtracker.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_dashboard.*
import net.nilswilhelm.foodtracker.R
import net.nilswilhelm.foodtracker.activities.AuthenticatorActivity
import net.nilswilhelm.foodtracker.adapters.TransactionRecyclerViewAdapter
import net.nilswilhelm.foodtracker.auth.AuthHandler
import net.nilswilhelm.foodtracker.data.AuthData
import net.nilswilhelm.foodtracker.data.Intake
import net.nilswilhelm.foodtracker.data.Transaction
import net.nilswilhelm.foodtracker.utils.Utils.Companion.getData
import okhttp3.*
import java.io.IOException


class DashboardFragment : Fragment(), Callback {


    private lateinit var dashboardViewModel: DashboardViewModel
    var transactions = ArrayList<Transaction>()
    private val foodRecyclerViewAdapterTransaction = TransactionRecyclerViewAdapter(ArrayList())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        fetchData(getString(R.string.BASE_URL) + "intake")
        getData(requireContext(), getString(R.string.BASE_URL) + "transactions/{bla}", this)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        transaction_recycler_view.layoutManager = LinearLayoutManager(requireContext())
        transaction_recycler_view.adapter = foodRecyclerViewAdapterTransaction
        transaction_recycler_view.isNestedScrollingEnabled = false;
    }

    private fun buildPieChart(intake: Intake) {

        val pieChart = view?.findViewById<PieChart>(R.id.pie_chart)

        pieChart?.setUsePercentValues(false);
        pieChart?.description?.isEnabled = false;
        pieChart?.setExtraOffsets(5F, 10F, 5F, 5F);

        pieChart?.dragDecelerationFrictionCoef = 0.95f;

        pieChart?.isDrawHoleEnabled = true;
        pieChart?.setHoleColor(Color.parseColor("#8cf188"));

        pieChart?.setTransparentCircleColor(Color.WHITE);
        pieChart?.setTransparentCircleAlpha(300);

        pieChart?.holeRadius = 54f;
        pieChart?.transparentCircleRadius = 56f;

        // center text
        pieChart?.setDrawCenterText(true);
        pieChart?.centerText = " ${intake.nutrition.energy} kcal"
        pieChart?.setCenterTextSize(30F)
        pieChart?.setCenterTextColor(Color.WHITE)

        // rotation
        pieChart?.rotationAngle = 0F;
        // enable rotation of the pieChart? by touch
        pieChart?.isRotationEnabled = false;
        pieChart?.isHighlightPerTapEnabled = true;

        // animation
        pieChart?.animateY(1400, Easing.EaseInOutQuad);


        val list = ArrayList<PieEntry>()
        list.add(PieEntry(intake.nutrition.protein.toFloat(), "Protein"))
        list.add(PieEntry(intake.nutrition.fat.toFloat(), "Fat"))
        list.add(PieEntry(intake.nutrition.carbohydrate.toFloat(), "Carbs"))


        val pieDataSet = PieDataSet(list, "Data Set 1")


        // colors
        pieDataSet.setColors(
            Color.parseColor("#f09263"),
            Color.parseColor("#59bdc6"),
            Color.parseColor("#e9e795")
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
        pieChart?.setEntryLabelColor(Color.WHITE);
        pieChart?.setEntryLabelTextSize(24f);

        pieChart?.data = pieData
        pieChart?.invalidate()
    }

    private fun fetchData(url: String) {

        val authData: AuthData = AuthHandler.getAuthData(requireContext())

        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .addHeader(
                "Authorization",
                authData.token
            )
            .addHeader("userId", authData.userId)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {

                    if (!response.isSuccessful) {
                        if (response.code == 401) {
                            startActivity(Intent(activity, AuthenticatorActivity::class.java))
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(activity, "Could not fetch data", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    } else {
                        val resString = response.body!!.string()

                        val gson = Gson()
                        val intake = gson.fromJson<Intake>(resString, Intake::class.java)
                        Log.d("Response", intake.toString())
                        activity?.runOnUiThread {
                            buildPieChart(intake)
                        }
                    }


                }
            }
        })
    }

    override fun onFailure(call: Call, e: IOException) {
        e.printStackTrace()
    }

    // Transactions received
    override fun onResponse(call: Call, response: Response) {
        response.use {

            if (!response.isSuccessful) {
                if (response.code == 401) {
                    startActivity(Intent(requireContext(), AuthenticatorActivity::class.java))
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Could not fetch data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                val resString = response.body!!.string()

                val gson = Gson()
                val itemType = object : TypeToken<ArrayList<Transaction>>() {}.type
                transactions = gson.fromJson<ArrayList<Transaction>>(resString, itemType)

                Log.d("Cool", transactions.toString())
                activity?.runOnUiThread {
                    foodRecyclerViewAdapterTransaction.loadNewData(transactions)
                }

            }


        }
    }


}