package com.saurav.covid_19india.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.saurav.covid_19graph.util.ConnectionManager
import com.saurav.covid_19india.R


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    lateinit var barChartActive: BarChart
    lateinit var barChartRecovered: BarChart
    lateinit var barChartDeceased: BarChart
    lateinit var lineChart: LineChart
    lateinit var lineChartRecovery: LineChart
    lateinit var lineChartDeceased: LineChart
    lateinit var txtTitle: TextView
    lateinit var spinner: Spinner

    lateinit var txtState: TextView
    lateinit var etConfirmed: TextView
    lateinit var etRecovered: TextView
    lateinit var etDeceased: TextView
    lateinit var txtInd: TextView
    lateinit var etConfirmedInd: TextView
    lateinit var etRecoveredInd: TextView
    lateinit var etDeceasedInd: TextView

    lateinit var pieChart: PieChart
    lateinit var progressLayout: RelativeLayout
    lateinit var txtTotal: TextView

    var stateName: String="West Bengal"
    var stateList: ArrayList<String> = ArrayList()

    var confirmedY= mutableListOf<Entry>()
    var activeY: ArrayList<BarEntry> = ArrayList()
    var recoveredY: ArrayList<BarEntry> = ArrayList()
    var deceasedY: ArrayList<BarEntry> = ArrayList()
    var districtX: ArrayList<String> = ArrayList()

    var universalY: ArrayList<Entry> = ArrayList()
    var universalX: ArrayList<String> = ArrayList()

    var universalRY: ArrayList<Entry> = ArrayList()
    var universalRX: ArrayList<String> = ArrayList()

    var universalDY: ArrayList<Entry> = ArrayList()
    var universalDX: ArrayList<String> = ArrayList()

    var stateCodeList= arrayListOf<String>("an", "ap", "ar", "as", "br", "ch", "ct", "dd", "dl", "dn", "ga", "gj", "hp", "hr", "jh",
        "jk","ka", "kl", "ld", "mh", "ml", "mn", "mp", "mz", "nl", "or", "pb", "py", "rj", "sk", "tg", "tn", "tr", "un",
        "up", "ut", "wb")


    var uniConfirmed=0
    var uniRecovered=0
    var uniDeceased=0

    var stateConfirmed=0
    var stateRecovered=0
    var stateDeceased=0

    var flag=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtTitle=findViewById(R.id.txtTitle)
        pieChart=findViewById(R.id.piechartConfirmed)
        barChartActive=findViewById(R.id.barchartActive)
        barChartRecovered=findViewById(R.id.barchartRecovered)
        barChartDeceased=findViewById(R.id.barchartDeceased)
        lineChart=findViewById(R.id.linechart)
        lineChartRecovery=findViewById(R.id.linechartRecovered)
        lineChartDeceased=findViewById(R.id.linechartDeceased)
        spinner=findViewById(R.id.spinner)
        txtState=findViewById(R.id.txtState)
        etConfirmed=findViewById(R.id.etConfirmed)
        etRecovered=findViewById(R.id.etRecovered)
        etDeceased=findViewById(R.id.etDeceased)
        txtInd=findViewById(R.id.txtInd)
        etConfirmedInd=findViewById(R.id.etConfirmedInd)
        etRecoveredInd=findViewById(R.id.etRecoveredInd)
        etDeceasedInd=findViewById(R.id.etDeceasedInd)
//        pieChart=findViewById(R.id.piechart)
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility=View.VISIBLE

        spinner.onItemSelectedListener = this
        loadSpinner(0)
        loadConfirmed()
    }

    private fun loadSpinner(index: Int){
        var width:Int
        var height:Int
        activeY.clear()
        recoveredY.clear()
        deceasedY.clear()
        confirmedY.clear()
        districtX.clear()

        pieChart.clear()
        barChartActive.clear()
        barChartRecovered.clear()
        barChartDeceased.clear()
        val queue= Volley.newRequestQueue(applicationContext)

        val dataUrl="https://api.covid19india.org/v2/state_district_wise.json"

        if (ConnectionManager().checkConnectivity(applicationContext)){

            val jsonArrayRequest= object: JsonArrayRequest(Request.Method.GET,dataUrl,null, Response.Listener {

                try {
                    progressLayout.visibility= View.GONE
                    println("Object:$it")
                    for(i in 0 until it.length()) {
                        val obj = it.getJSONObject(i)
                        val state = obj.getString("state")
                        println("State:$state")

                        if(flag==0){
                            stateList.add(state)
                        }

                        if(i==index && flag==1){
                            val stateCode=obj.getString("statecode")
                            val districtData=obj.getJSONArray("districtData")

                            for(j in 0 until districtData.length()){
                                val index=districtData.getJSONObject(j)
                                val district=index.getString("district")
                                val confirmed=index.getInt("confirmed")
                                val active=index.getInt("active")
                                val recovered=index.getInt("recovered")
                                val deceased=index.getInt("deceased")

                                confirmedY.add(BarEntry(confirmed.toFloat(),j))
                                activeY.add(BarEntry(active.toFloat(),j))
                                recoveredY.add(BarEntry(recovered.toFloat(),j))
                                deceasedY.add(BarEntry(deceased.toFloat(),j))
                                districtX.add(district)

                                stateConfirmed+=confirmed
                                stateRecovered+=recovered
                                stateDeceased+=deceased
                            }
                            break
                        }
                    }

                    if(flag==0){
                        val adapter=ArrayAdapter(this,android.R.layout.simple_spinner_item,stateList)
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinner.adapter=adapter
                        flag=1
                    }

                    txtState.text="Number of Cases in ${stateList.get(index)} - "
                    etConfirmed.text="Confirmed: $stateConfirmed"
                    etRecovered.text="Recovered: $stateRecovered"
                    etDeceased.text="Deceased: $stateDeceased"

                    val piedataSet=PieDataSet(confirmedY, "Confirmed Cases")
                    val piedata = PieData(districtX,piedataSet)
                    piedataSet.setColors(ColorTemplate.COLORFUL_COLORS)
                    pieChart.data = piedata
                    pieChart.animateXY(5000,5000)
                    pieChart.isDrawHoleEnabled = false
                    pieChart.setDescription("Confirmed Cases in ${stateList.get(index)}")
                    pieChart.setDescriptionTextSize(15f)
                    width=pieChart.width
                    height=pieChart.height
                    pieChart.setDescriptionPosition(width.toFloat(),height.toFloat()-1f)
//                    pieChart.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)

                    val activebardataset = BarDataSet(activeY, "Active Cases")
                    barChartActive.animateY(5000)
                    val activebardata: BarData = BarData(districtX,activebardataset)
                    activebardataset.setColors(ColorTemplate.COLORFUL_COLORS)
                    barChartActive.data = activebardata
                    barChartActive.setDescription("Active Cases in ${stateList.get(index)}")
                    barChartActive.setDescriptionTextSize(15f)
                    width=barChartActive.width
                    height=barChartActive.height
                    barChartActive.setDescriptionPosition(width.toFloat(),height.toFloat()-1f)

                    val recoveredbardataset = BarDataSet(recoveredY, "Recovered Cases")
                    barChartRecovered.animateY(5000)
                    val recoveredbardata: BarData = BarData(districtX,recoveredbardataset)
                    recoveredbardataset.setColors(ColorTemplate.COLORFUL_COLORS)
                    barChartRecovered.data = recoveredbardata
                    barChartRecovered.setDescription("Recovered Cases in ${stateList.get(index)}")
                    barChartRecovered.setDescriptionTextSize(15f)
                    width=barChartRecovered.width
                    height=barChartRecovered.height
                    barChartRecovered.setDescriptionPosition(width.toFloat(),height.toFloat()-1f)

                    val deceasedbardataset = BarDataSet(deceasedY, "Deceased Cases")
                    barChartDeceased.animateY(3000)
                    val deceasedbardata: BarData = BarData(districtX,deceasedbardataset)
                    deceasedbardataset.setColors(ColorTemplate.COLORFUL_COLORS)
                    barChartDeceased.data = deceasedbardata
                    barChartDeceased.setDescription("Deceased Cases in ${stateList.get(index)}")
                    barChartDeceased.setDescriptionTextSize(15f)
                    width=barChartDeceased.width
                    height=barChartDeceased.height
                    barChartDeceased.setDescriptionPosition(width.toFloat(),height.toFloat()-1f)

                }catch (e: Exception){
                    Toast.makeText(applicationContext,"Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
                    println(e.printStackTrace())
                }

            }, Response.ErrorListener {

                //Here we will handle the error
                println("Error is $it")
                if (application!=null){
                    Toast.makeText(applicationContext,"Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"

                    return headers      //Returning HashMap instead of MutableMap since both are almost same
                }
            }

            queue.add(jsonArrayRequest)

        }else{

            val dialog= AlertDialog.Builder(applicationContext)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is Not Found")
            dialog.setPositiveButton("Open Settings"){text,listener->
                //Open Settings

                val openSettings= Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(openSettings)
                this.finish()
            }
            dialog.setNegativeButton("Exit"){text,listener->
                //Exit the app
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()

        }

    }

    private fun loadConfirmed(){
        var total=0
        val queue= Volley.newRequestQueue(applicationContext)

        val dataUrl="https://api.covid19india.org/states_daily.json"
        val jsonObjectRequest= object: JsonObjectRequest(Request.Method.GET,dataUrl,null, Response.Listener {

                try {
//                    progressLayout.visibility= View.GONE
                    println("Object:$it")
                    val states_daily=it.getJSONArray("states_daily")
                    for(i in 0 until states_daily.length()) {

                        total=0
                        val obj = states_daily.getJSONObject(i)
                        val status = obj.getString("status")
                        val date=obj.getString("date")

                        if(status=="Confirmed"){
//                            for(j in 0 until stateCodeList.size){
//                                total += (obj.getString(stateCodeList[j])).toInt()
//                            }
//                            universalY.add(Entry(total.toFloat(),i/3))
                            universalY.add(Entry(obj.getString("tt").toFloat(),i/3))
                            universalX.add(date)
                            println("$date  Total:${total.toFloat()}   $i")
                            uniConfirmed+=obj.getString("tt").toInt()
                        }

                        else if(status=="Recovered"){
                            universalRY.add(Entry(obj.getString("tt").toFloat(),i/3))
                            universalRX.add(date)
                            uniRecovered+=obj.getString("tt").toInt()
                        }

                        else if(status=="Deceased"){
                            universalDY.add(Entry(obj.getString("tt").toFloat(),i/3))
                            universalDX.add(date)
                            uniDeceased+=obj.getString("tt").toInt()
                        }
                    }

//                    val xl: XAxis = lineChart.xAxis
//                    xl.setAvoidFirstLastClipping(true)
//                    val leftAxis: YAxis = lineChart.axisLeft
//                    leftAxis.isInverted = true
//                    val rightAxis: YAxis = lineChart.axisRight
//                    rightAxis.isEnabled = false

//                    mChart.setDrawGridBackground(false);
//                    mChart.setDescription("");
//                    mChart.setTouchEnabled(true);
//                    mChart.setDragEnabled(true);
//                    mChart.setScaleEnabled(true);
//                    mChart.setPinchZoom(true);
//                    mChart.setMarkerView(mv);

                    txtInd.text="Number of Cases in India - "
                    etConfirmedInd.text="Confirmed: $uniConfirmed"
                    etRecoveredInd.text="Recovered: $uniRecovered"
                    etDeceasedInd.text="Deceased: $uniDeceased"

                    val lineDataSet=LineDataSet(universalY,"Confirmed Cases (till present)")
                    lineDataSet.color = ContextCompat.getColor(this,R.color.colorPrimary)
                    lineDataSet.valueTextColor=ContextCompat.getColor(this,R.color.colorPrimaryDark)
                    lineDataSet.setCircleColor(Color.RED)
                    lineDataSet.lineWidth = 1f
                    lineDataSet.circleRadius = 2f
                    lineDataSet.setDrawCircleHole(false)

                    val dataSets = ArrayList<ILineDataSet>()
                    dataSets.add(lineDataSet)
                    val data = LineData(universalX,dataSets)
                    lineChart.data = data
                    lineChart.animateX(5000)
                    lineChart.setDescription("Confirmed Cases in India")
                    lineChart.setDescriptionTextSize(20f)
                    lineChart.invalidate()



                    val lineDataSetR=LineDataSet(universalRY,"Recovered Cases (till present)")
                    lineDataSetR.color = ContextCompat.getColor(this,R.color.colorPrimary)
                    lineDataSetR.valueTextColor=ContextCompat.getColor(this,R.color.colorPrimaryDark)
                    lineDataSetR.setCircleColor(Color.GREEN)
                    lineDataSetR.lineWidth = 1f
                    lineDataSetR.circleRadius = 2f
                    lineDataSetR.setDrawCircleHole(false)

                    val dataSetsR = ArrayList<ILineDataSet>()
                    dataSetsR.add(lineDataSetR)
                    val dataR = LineData(universalRX,dataSetsR)
                    lineChartRecovery.data = dataR
                    lineChartRecovery.animateX(5000)
                    lineChartRecovery.setDescription("Recovered Cases in India")
                    lineChartRecovery.setDescriptionTextSize(20f)
                    lineChartRecovery.invalidate()


                    val lineDataSetD=LineDataSet(universalDY,"Deceased Cases (till present)")
                    lineDataSetD.color = ContextCompat.getColor(this,R.color.colorPrimary)
                    lineDataSetD.valueTextColor=ContextCompat.getColor(this,R.color.colorPrimaryDark)
                    lineDataSetD.setCircleColor(Color.BLACK)
                    lineDataSetD.lineWidth = 1f
                    lineDataSetD.circleRadius = 2f
                    lineDataSetD.setDrawCircleHole(false)

                    val dataSetsD = ArrayList<ILineDataSet>()
                    dataSetsD.add(lineDataSetD)
                    val dataD = LineData(universalDX,dataSetsD)
                    lineChartDeceased.data = dataD
                    lineChartDeceased.animateX(5000)
                    lineChartDeceased.setDescription("Deceased Cases in India")
                    lineChartDeceased.setDescriptionTextSize(20f)
                    lineChartDeceased.invalidate()

                }catch (e: Exception){
                    Toast.makeText(applicationContext,"Some unexpected error occurred!!", Toast.LENGTH_SHORT).show()
                    println(e.printStackTrace())
                }

            }, Response.ErrorListener {

                //Here we will handle the error
                println("Error is $it")
                if (application!=null){
                    Toast.makeText(applicationContext,"Volley error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"

                    return headers      //Returning HashMap instead of MutableMap since both are almost same
                }
            }

            queue.add(jsonObjectRequest)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        loadSpinner(position)
        println("Position:$position")
    }
}