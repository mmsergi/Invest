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
import android.widget.ListView
import android.widget.Spinner
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.ColorTemplate.rgb
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private var mChart: PieChart? = null

    protected lateinit var mTfRegular: Typeface
    protected lateinit var mTfLight: Typeface

    lateinit var typesArray: Array<String>

    lateinit var listView: ListView

    lateinit var moneyArray: ArrayList<Money>

    lateinit var adapter: ListViewAdapter

    companion object {
        lateinit var instance : MainActivity
        lateinit var db: DatabaseMoney
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        instance = this

        db = DatabaseMoney(this)
        moneyArray = db.moneyData

        typesArray = getResources().getStringArray(R.array.money_type)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = "My money"
        setSupportActionBar(toolbar)

        mChart = findViewById(R.id.chart)
        listView = findViewById(R.id.listView)

        adapter = ListViewAdapter(this, moneyArray)
        listView.setAdapter(adapter)

        configureChart()
        updateData()
    }

    private fun configureChart () {
        mTfRegular = Typeface.createFromAsset(assets, "OpenSans-Regular.ttf")
        mTfLight = Typeface.createFromAsset(assets, "OpenSans-Light.ttf")

        mChart!!.setBackgroundColor(Color.TRANSPARENT)

        mChart!!.setUsePercentValues(true)
        mChart!!.getDescription().isEnabled = false

        //mChart!!.setCenterTextTypeface(mTfLight)
        //mChart!!.setCenterText(generateCenterSpannableText())

        mChart!!.setDrawHoleEnabled(true)
        mChart!!.setHoleColor(Color.TRANSPARENT)

        mChart!!.setTransparentCircleColor(Color.TRANSPARENT)
        mChart!!.setTransparentCircleAlpha(0)

        mChart!!.setHoleRadius(45f)
        //mChart!!.setTransparentCircleRadius(61f)

        mChart!!.setDrawCenterText(true)

        mChart!!.setRotationEnabled(true)
        mChart!!.setHighlightPerTapEnabled(true)

        mChart!!.setRotationAngle(0f)
        mChart!!.setCenterTextOffset(0f, 0f)

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
        mChart!!.setEntryLabelTextSize(18f)
    }

    fun updateData(){
        moneyArray = db.moneyData
        setData()
        adapter = ListViewAdapter(this, moneyArray)
        listView.setAdapter(adapter)
    }

    private fun setData() {

        val map = HashMap<String, Float>()
        val arrayColors = ArrayList<Int>()

        for (e in 0 until moneyArray.size) {

            val type = moneyArray[e].type
            val amount = moneyArray[e].amount

            if (map.containsKey(type)){
                map.put(type, map.getValue(type) + amount)
            } else {
                map.put(type, amount)
            }
        }

        Collections.reverse(arrayColors)

        val values = ArrayList<PieEntry>()

        for ((key, value) in map) {
            values.add(PieEntry(value, key))

            if (key.equals(typesArray[0])) {
                arrayColors.add(rgb("#2ecc71")) //green
            } else if (key.equals(typesArray[1])) {
                arrayColors.add(rgb("#3498db")) //blue
            } else if (key.equals(typesArray[2])) {
                arrayColors.add(rgb("#f1c40f")) //yellow
            } else if (key.equals(typesArray[3])) {
                arrayColors.add(rgb("#e74c3c")) //red
            }
        }

        val dataSet = PieDataSet(values, "Smart Invest")
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        dataSet.setColors(arrayColors)
        //dataSet.setSelectionShift(0f);

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.WHITE)
        data.setValueTypeface(mTfLight)
        mChart!!.setData(data)

        mChart!!.invalidate()
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

            updateData()

            dialog.dismiss()

        })

        dialog.show()
    }

    private fun generateCenterSpannableText(): SpannableString {

        val s = SpannableString("Smart Invest\ndeveloped by Sergi")
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
}