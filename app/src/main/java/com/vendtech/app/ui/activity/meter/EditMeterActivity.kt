package com.vendtech.app.ui.activity.meter

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.gson.Gson

import com.vendtech.app.R
import com.vendtech.app.adapter.meter.MeterMakeAdapter
import com.vendtech.app.helper.SharedHelper
import com.vendtech.app.models.meter.AddMeterModel
import com.vendtech.app.network.Uten
import com.vendtech.app.utils.Constants
import com.vendtech.app.utils.CustomDialog
import com.vendtech.app.utils.Utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditMeterActivity : Activity(), View.OnClickListener {

    lateinit var back: ImageView
    lateinit var saveTV: TextView

    lateinit var meterNo: EditText
    lateinit var meterName: EditText
    lateinit var meterAddress: EditText
    lateinit var meterDate: TextView
    lateinit var customDialog: CustomDialog

    lateinit var spMeterMake: Spinner
    lateinit var meterMakeAdapter: MeterMakeAdapter


    var mnumber = ""
    var mname = ""
    var maddress = ""
    var mdate = ""
    var meterId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_meter)

        mnumber = intent.getStringExtra("mnumber").toString()
        mname = intent.getStringExtra("mname").toString()
        maddress = intent.getStringExtra("maddress").toString()
        mdate = intent.getStringExtra("mdate").toString()
        meterId = intent.getStringExtra("mid").toString()

        initViews()
        customDialog = CustomDialog(this)
        bindSpMeterMake()
    }


    fun initViews() {
        spMeterMake = findViewById(R.id.spMeterMake) as Spinner
        back = findViewById<View>(R.id.imgBack) as ImageView
        saveTV = findViewById<View>(R.id.saveTV) as TextView
        meterNo = findViewById<View>(R.id.meterno) as EditText
        meterName = findViewById<View>(R.id.metername) as EditText
        meterAddress = findViewById<View>(R.id.address) as EditText
        meterDate = findViewById<View>(R.id.metermake) as TextView


        meterNo.setText(mnumber)
        meterName.setText(mname)
        meterAddress.setText(maddress)
        meterDate.setText(mdate)


        back.setOnClickListener(this)
        saveTV.setOnClickListener(this)
        meterDate.setOnClickListener(this)

    }

    private fun bindSpMeterMake() {
        var list: MutableList<String> = ArrayList()
        list.add("CONLOG")
        list.add("HOLLEY")
        list.add("SAGENCOM")
        meterDate.text = list[0]
        meterMakeAdapter = MeterMakeAdapter(this, list)
        spMeterMake.adapter = meterMakeAdapter
        spMeterMake.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View,
                                        position: Int, id: Long) {
                val item = adapterView.getItemAtPosition(position) as String
                if (item != null) {
                    meterDate.text = item
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })
    }

    override fun onClick(v: View) {


        when (v.id) {

            R.id.imgBack -> {
                finish()
                overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out)
            }

            R.id.metermake -> {
                DatePick()
            }

            R.id.saveTV -> {
                if (TextUtils.isEmpty(meterNo.text.toString().trim())) {
                    Utilities.shortToast("Enter meter number", this)
                } else if (meterNo.text.toString().trim().length != 11) {
                    Utilities.shortToast("Meter number must be of 11 digits", this)
                } else if (TextUtils.isEmpty(meterName.text.toString().trim())) {
                    Utilities.shortToast("Enter meter name", this)
                } else if (TextUtils.isEmpty(meterAddress.text.toString().trim())) {
                    Utilities.shortToast("Enter address", this)
                } else if (meterAddress.text.toString().trim().length < 7) {
                    Utilities.shortToast(resources.getString(R.string.address_length), this)
                } else if (TextUtils.isEmpty(meterDate.text.toString().trim())) {
                    Utilities.shortToast("Enter meter make", this)
                } else {
                    if (Uten.isInternetAvailable(this)) {
                        EditMeter()
                    } else {
                        Utilities.shortToast("No internet connection. Please check your network connectivity.", this)
                    }
                }
            }
        }

    }


    fun DatePick() {

        var cal = Calendar.getInstance()

        val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, monthOfYear)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val myFormat = "dd MMM, yyyy" // mention the format you need
            val sdf = SimpleDateFormat(myFormat, Locale.US)
            meterDate.setText(sdf.format(cal.time))
        }

        val dialog = DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
        dialog.datePicker.maxDate = cal.getTimeInMillis()
        dialog.show()
    }


    fun EditMeter() {

        customDialog.show()

        val call: Call<AddMeterModel> = Uten.FetchServerData().update_meter(SharedHelper.getString(this, Constants.TOKEN), meterName.text.toString().trim(), meterDate.text.toString().trim(), meterAddress.text.toString().trim(), meterNo.text.toString().trim(), meterId)

        call.enqueue(object : Callback<AddMeterModel> {
            override fun onResponse(call: Call<AddMeterModel>, response: Response<AddMeterModel>) {
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                var data = response.body()
                if (data != null) {
                    Utilities.shortToast(data.message, this@EditMeterActivity)
                    if (data.status.equals("true")) {
                        onBackPressed()
                        finish()
                    } else {
                        Utilities.CheckSessionValid(data.message, this@EditMeterActivity, this@EditMeterActivity)
                    }
                }
            }

            override fun onFailure(call: Call<AddMeterModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
            }

        })


    }

}
