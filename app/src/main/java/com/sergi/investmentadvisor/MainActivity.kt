package com.sergi.investmentadvisor

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var mChart: PieChart? = null

    protected lateinit var mTfRegular: Typeface
    protected lateinit var mTfLight: Typeface

    lateinit var db: DatabaseMoney

    lateinit var typesArray: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "My money"
        setSupportActionBar(toolbar)

        db = DatabaseMoney(this)
        typesArray = getResources().getStringArray(R.array.money_type)

        configureChart()
    }

    private fun configureChart () {
        mTfRegular = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        mTfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")

        mChart = findViewById(R.id.chart)
        mChart!!.setBackgroundColor(Color.WHITE)

        mChart!!.setUsePercentValues(true)
        mChart!!.getDescription().isEnabled = false

        mChart!!.setCenterTextTypeface(mTfLight)
        mChart!!.setCenterText(generateCenterSpannableText())

        mChart!!.setDrawHoleEnabled(true)
        mChart!!.setHoleColor(Color.WHITE)

        mChart!!.setTransparentCircleColor(Color.WHITE)
        mChart!!.setTransparentCircleAlpha(0)

        mChart!!.setHoleRadius(45f)
        //mChart!!.setTransparentCircleRadius(61f)

        mChart!!.setDrawCenterText(true)

        mChart!!.setRotationEnabled(false)
        mChart!!.setHighlightPerTapEnabled(true)

        mChart!!.setRotationAngle(0f)
        mChart!!.setCenterTextOffset(0f, 0f)

        setData(4, 100f)

        mChart!!.animateY(1400, Easing.EasingOption.EaseInOutQuad)

        val l = mChart!!.getLegend()
        l.isEnabled = false
        l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        l.orientation = Legend.LegendOrientation.HORIZONTAL
        l.setDrawInside(true)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 10f

        // entry label styling
        mChart!!.setEntryLabelColor(Color.WHITE)
        mChart!!.setEntryLabelTypeface(mTfRegular)
        mChart!!.setEntryLabelTextSize(12f)
    }

    private fun setData(count: Int, range: Float) {

        val moneyArray = db.moneyData
        val map = HashMap<String, Float>()

        for (e in 0 until moneyArray.size) {

            val type = moneyArray[e].type
            val amount = moneyArray[e].amount

            if (map.containsKey(type)){
                map.put(type, map.getValue(type) + amount)
            } else {
                map.put(type, amount)
            }
        }

        val values = ArrayList<PieEntry>()

        for ((key, value) in map) {
            values.add(PieEntry(value, key))
        }

        /*for (i in 0 until count) {
            values.add(PieEntry(5.toFloat(), typesArray[i]))
        }*/

        val dataSet = PieDataSet(values, "Election Results")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        dataSet.setColors(*ColorTemplate.MATERIAL_COLORS)
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(mTfLight)
        mChart!!.setData(data)

        mChart!!.invalidate()
    }

    private fun generateCenterSpannableText(): SpannableString {

        val s = SpannableString("MPAndroidChart\ndeveloped by Sergi")
        s.setSpan(RelativeSizeSpan(1f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 5, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 5, s.length, 0)
        return s
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.add -> {
                showDialog()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    fun showDialog(){

        val builder = AlertDialog.Builder(this)

        val inflater = this.getLayoutInflater()
        val view = inflater.inflate(R.layout.dialog_main, null)

        builder.setView(view)
        val dialog = builder.create()

        val descriptionET = view.findViewById<EditText>(R.id.editTextDescription)
        val amountET = view.findViewById<EditText>(R.id.editTextAmount)
        val typeSpinner = view.findViewById<Spinner>(R.id.spinnerType)

        view.findViewById<Button>(R.id.addBtn).setOnClickListener({

            val money = Money()
            money.name = descriptionET.text.toString()
            money.amount = amountET.text.toString().toFloat()

            val position = typeSpinner.selectedItemPosition
            money.type = typesArray[position]

            db.addMoney(money)

            Log.e("FULL DB", db.moneyData.toString())

            dialog.dismiss()

        })

        dialog.show()
    }

}
