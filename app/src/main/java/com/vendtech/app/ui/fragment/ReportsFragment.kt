package com.vendtech.app.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.gson.Gson

import com.vendtech.app.R
import com.vendtech.app.adapter.transactions.DepositTransactionAdapter
import com.vendtech.app.adapter.transactions.RechargeTransactionAdapter
import com.vendtech.app.helper.SharedHelper
import com.vendtech.app.models.transaction.*
import com.vendtech.app.network.Uten
import com.vendtech.app.utils.Constants
import com.vendtech.app.utils.CustomDialog
import com.vendtech.app.utils.Utilities
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportsFragment : android.support.v4.app.Fragment(), View.OnClickListener {


    lateinit var addBalanceTV: TextView
    lateinit var tranhistoryTV: TextView
    internal var isFirstLaunch = true

var isFilter=false
    var recharge=false
    //ADD BALANCE LAYOUT
    lateinit var addBalanceLayout: ScrollView


    //TRANSACTION HISTORY LAYOUT
    lateinit var transactionHistoryLayout: LinearLayout
    lateinit var fragment_frame: FrameLayout
    lateinit var depositText: TextView
    lateinit var linedeposit: View
    lateinit var depositTRL: RelativeLayout
    lateinit var rechargeText: TextView
    lateinit var linerecharge: View
    lateinit var rechargeTRL: RelativeLayout
lateinit var filterReport:FloatingActionButton

    //ANIMATION
    lateinit var slide_in: Animation
    lateinit var slide_out: Animation


    //ADD DEPOSIT LAYOUT
    lateinit var typeSpinner: Spinner
    lateinit var selectPaytype: RelativeLayout
    var transactionMode = 1
    lateinit var sendNowTV: TextView
    lateinit var vendornameET: EditText
    lateinit var chxslipET: EditText
    lateinit var depositamountET: EditText
    lateinit var commentET: EditText
    lateinit var banknameTV: TextView
    lateinit var accnameTV: TextView
    lateinit var accnumberTV: TextView
    lateinit var accbbanTV: TextView


    //RECHARGE TRANSACTION AND DEPOSIT TRANSACTION LAYOUTS
    lateinit var recyclerviewRecharge: RecyclerView
    lateinit var nodataRecharge: TextView
    lateinit var recyclerviewDeposit: RecyclerView
    lateinit var nodataDeposit: TextView
    internal var rechargeListModel: MutableList<RechargeTransactionNewListModel.Result> = java.util.ArrayList()
    internal var depositListModel: MutableList<DepositTransactionNewListModel.Result> = java.util.ArrayList()
    lateinit var rechargetransAdapter: RechargeTransactionAdapter
    lateinit var deposittransAdapter: DepositTransactionAdapter
    var pageRecharge = 1
    public var pageDeposit = 1
    var totalItemsNo = 10
var banKAccountId=""
    var refId=""
    var depositType=1
    var posId=0
    var from:String=""
    var to:String=""
    var meterNumber:String=""
    var transId:String=""

    //ANIMATION
    lateinit var slide_up: Animation
    lateinit var slide_down: Animation


    //Pagination recharges
    var loadings_r = true
    var pastVisiblesItems_r = 0
    var visibleItemCount_r = 0
    var totalItemCount_r = 0


    //Pagination deposit
    var loadings_d = true
    var pastVisiblesItems_d = 0
    var visibleItemCount_d = 0
    var totalItemCount_d = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_reports, container, false)

        findviews(view)
        SetDepositLayout()
//        GetBankDetails()

        return view
    }


    fun findviews(view: View) {

        addBalanceTV = view.findViewById<View>(R.id.addBalanceTV) as TextView
        tranhistoryTV = view.findViewById<View>(R.id.tranhistoryTV) as TextView
        slide_down = AnimationUtils.loadAnimation(activity, R.anim.slide_down)
        slide_up = AnimationUtils.loadAnimation(activity, R.anim.slide_up)

        //ADD BALANCE LAYOUT
        addBalanceLayout = view.findViewById<View>(R.id.layoutAddBalance) as ScrollView

        //TRANSACTION HISTORY LAYOUT
        transactionHistoryLayout = view.findViewById<View>(R.id.layoutTransactionHistory) as LinearLayout
        depositText = view.findViewById<View>(R.id.depositText) as TextView
        rechargeText = view.findViewById<View>(R.id.rechargeText) as TextView
        linedeposit = view.findViewById<View>(R.id.linedeposit) as View
        linerecharge = view.findViewById<View>(R.id.linerecharge) as View
        depositTRL = view.findViewById<View>(R.id.deposItTRL) as RelativeLayout
        rechargeTRL = view.findViewById<View>(R.id.rechargeTRL) as RelativeLayout
        filterReport=view.findViewById(R.id.filterReport) as FloatingActionButton
        rechargeTRL.setOnClickListener(this)
        depositTRL.setOnClickListener(this)

        //RECHARGE TRANSACTION AND DEPOSIT TRANSACTION LAYOUT
        recyclerviewDeposit = view.findViewById(R.id.recyclerviewDepositss)
        recyclerviewRecharge = view.findViewById(R.id.recyclerviewRechargess)
        nodataDeposit = view.findViewById(R.id.nodataDeposit)
        nodataRecharge = view.findViewById(R.id.nodataRecharge)

        slide_in = AnimationUtils.loadAnimation(activity, R.anim.slide_in)
        slide_out = AnimationUtils.loadAnimation(activity, R.anim.activity_back_out)

        //Deposit layout
        typeSpinner = view.findViewById<View>(R.id.typeSpinner) as Spinner
        selectPaytype = view.findViewById<View>(R.id.selectPaytype) as RelativeLayout
        sendNowTV = view.findViewById<View>(R.id.sendnowTV) as TextView
        vendornameET = view.findViewById<View>(R.id.vendornameET) as EditText
        chxslipET = view.findViewById<View>(R.id.chxslipET) as EditText
        depositamountET = view.findViewById<View>(R.id.depositamountET) as EditText
        commentET = view.findViewById<View>(R.id.commentET) as EditText
        /*  lateinit var banknameTV:TextView
    lateinit var accnameTV:TextView
    lateinit var accnumberTV:TextView
    lateinit var accbbanTV:TextView


*/
        banknameTV = view.findViewById<View>(R.id.banknameTV) as TextView
        accnameTV = view.findViewById<View>(R.id.accnameTV) as TextView
        accnumberTV = view.findViewById<View>(R.id.accnumberTV) as TextView
        accbbanTV = view.findViewById<View>(R.id.accbbanTV) as TextView

        addBalanceTV.setOnClickListener(this)
        tranhistoryTV.setOnClickListener(this)
        selectPaytype.setOnClickListener(this)
filterReport.setOnClickListener(this)
        rechargeTRL.performClick()

    }


    fun SetDepositLayout() {

        SetSpinnerData()

        sendNowTV.setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(vendornameET.text.toString().trim())) {
                Utilities.shortToast("Enter vendor name", requireActivity())
            } else if (TextUtils.isEmpty(chxslipET.text.toString().trim())) {
                if (transactionMode == 1) {
                    Utilities.shortToast("Enter slip id", requireActivity())
                } else {
                    Utilities.shortToast("Enter cheque id", requireActivity())
                }
            } else if (TextUtils.isEmpty(depositamountET.text.toString().trim())) {
                Utilities.shortToast("Enter deposit amount", requireActivity())
            } else if (TextUtils.isEmpty(commentET.text.toString().trim())) {
                Utilities.shortToast("Please type some comment", requireActivity())
            } else {
             //   DoDeposit()
            }
        })

    }


   /* fun DoDeposit() {

        var customDialog: CustomDialog
        customDialog = CustomDialog(requireActivity())
        customDialog.show()

        val call: Call<DepositRequestModel> = Uten.FetchServerData().deposit_request(SharedHelper.getString(requireActivity(), Constants.TOKEN), "10", depositamountET.text.toString().trim(), chxslipET.text.toString().trim(), commentET.text.toString().trim(), transactionMode.toString())
        call.enqueue(object : Callback<DepositRequestModel> {
            override fun onResponse(call: Call<DepositRequestModel>, response: Response<DepositRequestModel>) {

                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                var data = response.body()
                if (data != null) {
                    Utilities.shortToast(data.message, requireActivity())
                    if (data.status.equals("true")) {
                        ResetLayoutAddDeposit()
                    } else {
                        Utilities.CheckSessionValid(data.message, requireContext(), requireActivity())

                    }
                }
            }

            override fun onFailure(call: Call<DepositRequestModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
            }

        })
    }
*/
    fun ResetLayoutAddDeposit() {

        vendornameET.setText("")
        chxslipET.setText("")
        depositamountET.setText("")
        commentET.setText("")
        SetSpinnerData()

    }


    fun SetSpinnerData() {

        val list: MutableList<String> = ArrayList()
        list.add("Cash")
        list.add("Cheque")

        val adapte = ArrayAdapter<String>(requireActivity(), R.layout.spinner_text_second, list)
        adapte.setDropDownViewResource(R.layout.simple_spinner_dropdown)
        typeSpinner.setAdapter(adapte)

        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                //Toast.makeText(this@SignUpActivity, "Country ID: " + data[position].countryId, Toast.LENGTH_SHORT).show()
                if (position == 0) {
                    transactionMode = 1
                } else {
                    transactionMode = 2
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun SelectAddBalance() {

        addBalanceTV.setTextColor(resources.getColor(R.color.colorblack))
        addBalanceTV.background = resources.getDrawable(R.drawable.yellow_chooser_left)
        tranhistoryTV.setTextColor(resources.getColor(R.color.colorlightgrey))
        tranhistoryTV.background = resources.getDrawable(R.drawable.grey_chooser_right)


        if (transactionHistoryLayout.visibility == View.VISIBLE) {
            transactionHistoryLayout.startAnimation(slide_out)
        }
        transactionHistoryLayout.visibility = View.GONE


        if (addBalanceLayout.visibility == View.GONE) {
            addBalanceLayout.startAnimation(slide_in)
        }
        addBalanceLayout.visibility = View.VISIBLE


        //   addBalanceLayout.setVisibility(View.VISIBLE);
        //  transactionHistoryLayout.setVisibility(View.GONE);


    }

    fun SelectTransactionHistory() {


        recyclerviewRecharge.visibility = View.GONE
        recyclerviewDeposit.visibility = View.GONE
        nodataDeposit.visibility = View.GONE
        nodataRecharge.visibility = View.GONE

        addBalanceTV.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorlightgrey))
        addBalanceTV.background = ContextCompat.getDrawable(requireActivity(), R.drawable.grey_chooser_left)
        tranhistoryTV.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorblack))
        tranhistoryTV.background = ContextCompat.getDrawable(requireActivity(), R.drawable.yellow_chooser_right)

        if (addBalanceLayout.visibility == View.VISIBLE) {
            addBalanceLayout.startAnimation(slide_out)
        }
        addBalanceLayout.visibility = View.GONE



        if (transactionHistoryLayout.visibility == View.GONE) {
            transactionHistoryLayout.startAnimation(slide_in)
        }
        transactionHistoryLayout.visibility = View.VISIBLE


        Handler().postDelayed({
            SelectRechargeTrans()
        }, 500)


        // addBalanceLayout.setVisibility(View.GONE);
        // transactionHistoryLayout.setVisibility(View.VISIBLE);

    }

    override fun onClick(v: View) {


        when (v.id) {

            R.id.addBalanceTV ->
                SelectAddBalance()

            R.id.tranhistoryTV ->
                SelectTransactionHistory()

            R.id.selectPaytype ->
                typeSpinner.performClick()

            R.id.rechargeTRL ->
                SelectRechargeTrans()

            R.id.deposItTRL ->
                SelectDepositTrans()

            R.id.filterReport->
                ShowFilterDialog()
        }
    }

    private fun ShowFilterDialog() {
        pageRecharge=1
        val dialog=ReportFilterDialog()
       // dialog.dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        val bundle=Bundle()
        bundle.putBoolean("isRecharge",recharge)
        dialog.arguments=bundle
        dialog.initFragment(this)
        dialog.show(activity?.supportFragmentManager!!,"filter")
    }

    companion object {


        fun newInstance(): ReportsFragment {
            return ReportsFragment()
        }
    }


    fun SelectRechargeTrans() {
        from=""
        to=""
        this.posId=0
        this.meterNumber=""
        this.transId=""
        this.banKAccountId=""
        this.refId=""
        this.depositType=0
        isFilter=false
recharge=true
        pageDeposit = 1
        linerecharge.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorYellow))
        rechargeText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorYellow))
        linedeposit.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))
        depositText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))

      //  LoadRechargeTransactionFragment()
        filterRechargeData(posId,from, to, meterNumber, transId)
       // val call: Call<RechargeTransactionNewListModel> = Uten.FetchServerData().getSalesReport(SharedHelper.getString(requireActivity(), Constants.TOKEN),0,"","","","", pageRecharge, totalItemsNo)


    }


    fun SelectDepositTrans() {
        from=""
        to=""
        this.posId=0
        this.meterNumber=""
        this.transId=""
        this.banKAccountId=""
        this.refId=""
        this.depositType=0
        isFilter=false
recharge=false
        pageRecharge = 1
        linerecharge.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))
        rechargeText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorWhite))
        linedeposit.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.colorYellow))
        depositText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorYellow))
      //  posId: Int, from: String, to: String, meterNumber: String, refNumber: String, transId: String, bankAccountId: String, depositType: Int
filterDepositData(0,"","","","","","",0)
        //LoadDepositTransactionFragment()
    }


    fun LoadRechargeTransactionFragment() {

        var customDialog: CustomDialog
        customDialog = CustomDialog(requireActivity())
        customDialog.show()

        val call: Call<RechargeTransactionNewListModel> = Uten.FetchServerData().getSalesReport(SharedHelper.getString(requireActivity(), Constants.TOKEN),0,"","","","", pageRecharge, totalItemsNo)
        call.enqueue(object : Callback<RechargeTransactionNewListModel> {
            override fun onResponse(call: Call<RechargeTransactionNewListModel>, response: Response<RechargeTransactionNewListModel>) {


                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                var data = response.body()
                if (data != null) {
                    if (data.status.equals("true")) {

                        if (data.result.size > 0) {

                            if (data.result.size < totalItemsNo) {
                                loadings_r = false
                            } else {
                                loadings_r = true
                            }

                            if (pageRecharge == 1) {
                                rechargeListModel.clear()
                                rechargeListModel.addAll(data.result)
                            } else {
                                rechargeListModel.addAll(data.result)
                            }

                            if (pageRecharge == 1) {
                                ShowRechargeTransactionFlow()
                            } else {
                                rechargetransAdapter.notifyDataSetChanged()
                            }

                        } else {


                            if (rechargeListModel.size < 1) {
                                nodataDeposit.visibility = View.GONE
                                nodataRecharge.visibility = View.VISIBLE
                                recyclerviewDeposit.visibility = View.GONE
                                recyclerviewRecharge.visibility = View.GONE

                            }
                        }
                    } else {
                        Utilities.CheckSessionValid(data.message, requireContext(), requireActivity())

                    }
                }

            }

            override fun onFailure(call: Call<RechargeTransactionNewListModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                Utilities.shortToast("Something went wrong.", requireActivity())
            }

        })
    }


    fun LoadDepositTransactionFragment() {

        var customDialog: CustomDialog
        customDialog = CustomDialog(requireActivity())
        customDialog.show()
        val call: Call<DepositTransactionNewListModel> = Uten.FetchServerData().getDepositReports(SharedHelper.getString(requireActivity(), Constants.TOKEN),0,"","","","","","",0, pageDeposit, totalItemsNo)
        call.enqueue(object : Callback<DepositTransactionNewListModel> {
            override fun onResponse(call: Call<DepositTransactionNewListModel>, response: Response<DepositTransactionNewListModel>) {
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                val g = Gson()
                g.toJson(response.body())

                var data = response.body()
                if (data != null) {
                    if (data.status.equals("true")) {


                        if (data.result.size > 0) {

                            if (data.result.size < totalItemsNo) {
                                loadings_d = false
                            } else {
                                loadings_d = true
                            }

                            if (pageDeposit == 1) {
                                depositListModel.clear()
                                depositListModel.addAll(data.result)
                            } else {
                                depositListModel.addAll(data.result)
                            }
                            if (pageDeposit == 1) {
                                ShowDepositTransactionFlow()
                            } else {
                                deposittransAdapter.notifyDataSetChanged()
                            }

                        } else {

                            if (depositListModel.size < 1) {

                                nodataDeposit.visibility = View.VISIBLE
                                nodataRecharge.visibility = View.GONE
                                recyclerviewDeposit.visibility = View.GONE
                                recyclerviewRecharge.visibility = View.GONE

                            }
                        }
                    } else {
                        Utilities.CheckSessionValid(data.message, requireContext(), requireActivity())

                    }
                }
            }

            override fun onFailure(call: Call<DepositTransactionNewListModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                Utilities.shortToast("Something went wrong.", requireActivity())
            }

        })
    }


    fun ShowRechargeTransactionFlow() {


        val mLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        rechargetransAdapter = RechargeTransactionAdapter(rechargeListModel, requireActivity(), requireActivity())
        recyclerviewRecharge.adapter = rechargetransAdapter
        recyclerviewRecharge.layoutManager = mLayoutManager
        recyclerviewRecharge.setHasFixedSize(true)
        rechargetransAdapter.notifyDataSetChanged()

        nodataRecharge.visibility = View.GONE
        nodataDeposit.visibility = View.GONE
        recyclerviewDeposit.visibility = View.GONE
        recyclerviewRecharge.visibility = View.GONE


        if (recyclerviewRecharge.visibility == View.GONE) {
            recyclerviewRecharge.startAnimation(slide_up)
        }
        recyclerviewRecharge.visibility = View.VISIBLE



        recyclerviewRecharge.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    visibleItemCount_r = mLayoutManager.childCount
                    totalItemCount_r = mLayoutManager.itemCount
                    pastVisiblesItems_r = mLayoutManager.findFirstVisibleItemPosition()

                    if (loadings_r) {
                        if (visibleItemCount_r + pastVisiblesItems_r >= totalItemCount_r) {
                            loadings_r = false
                            pageRecharge++
                            Log.v("WalletFragment", "-------------------------------------------Last Item Wow !--------------------")
                            //Do pagination.. i.e. fetch new data
                           /* if(!isFilter)
                            LoadRechargeTransactionFragment()
                            else*/ filterRechargeData(posId, from, to, meterNumber, transId)
                        }
                    }
                }
            }
        })


    }


    fun GetBankDetails() {


        var customDialog: CustomDialog
        customDialog = CustomDialog(requireActivity())
        customDialog.show()

        val call: Call<BankDetailsModel> = Uten.FetchServerData().bank_details(SharedHelper.getString(requireActivity(), Constants.TOKEN))
        call.enqueue(object : Callback<BankDetailsModel> {
            override fun onResponse(call: Call<BankDetailsModel>, response: Response<BankDetailsModel>) {

                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                var data = response.body()
                if (data != null) {
                    //  Utilities.shortToast(data.message,requireActivity())
                    if (data.status.equals("true")) {

                        /* lateinit var banknameTV:TextView
    lateinit var accnameTV:TextView
    lateinit var accnumberTV:TextView
    lateinit var accbbanTV:TextView
*/
                        if (data.result.size > 0) {
                            banknameTV.text = data.result.get(0).bankName
                            accnameTV.text = data.result.get(0).accountName
                            accnumberTV.text = data.result.get(0).accountNumber
                            accbbanTV.text = data.result.get(0).bban

                        }


                    } else {
                        Utilities.CheckSessionValid(data.message, requireContext(), requireActivity())
                    }
                }
            }

            override fun onFailure(call: Call<BankDetailsModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
            }

        })
    }

    fun SetBankDetails() {


    }

    fun ShowDepositTransactionFlow() {

        val mLayoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

        deposittransAdapter = DepositTransactionAdapter(depositListModel, requireActivity(), requireActivity())
        recyclerviewDeposit.adapter = deposittransAdapter
        recyclerviewDeposit.layoutManager = mLayoutManager
        recyclerviewDeposit.setHasFixedSize(true)
        deposittransAdapter.notifyDataSetChanged()

        nodataRecharge.visibility = View.GONE
        nodataDeposit.visibility = View.GONE
        recyclerviewDeposit.visibility = View.GONE
        recyclerviewRecharge.visibility = View.GONE


        if (recyclerviewDeposit.visibility == View.GONE) {
            recyclerviewDeposit.startAnimation(slide_up)
        }
        recyclerviewDeposit.visibility = View.VISIBLE




        recyclerviewDeposit.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0)
                //check for scroll down
                {
                    visibleItemCount_d = mLayoutManager.childCount
                    totalItemCount_d = mLayoutManager.itemCount
                    pastVisiblesItems_d = mLayoutManager.findFirstVisibleItemPosition()

                    if (loadings_d) {
                        if (visibleItemCount_d + pastVisiblesItems_d >= totalItemCount_d) {
                            loadings_d = false
                            pageDeposit++
                            Log.v("WalletFragment", "-------------------------------------------Last Item Wow !--------------------")
                            //Do pagination.. i.e. fetch new data
                           /* if(!isFilter)
                                LoadDepositTransactionFragment()
                            else */filterDepositData(posId,from,to,meterNumber,refId,transId,banKAccountId,depositType)
                        //    L

                        }
                    }
                }
            }
        })

    }

    fun filterRechargeData(posId: Int, from: String, to: String, meterNumber: String, transId: String) {
this.posId=posId
        this.from=from
        this.to=to
        this.meterNumber=meterNumber
        this.transId=transId
        var customDialog: CustomDialog
        customDialog = CustomDialog(requireActivity())
        customDialog.show()
/*if(rechargeListModel.isNotEmpty()){
    rechargeListModel.clear()
}*/
        isFilter=true
        val call: Call<RechargeTransactionNewListModel> = Uten.FetchServerData().getSalesReport(SharedHelper.getString(requireActivity(), Constants.TOKEN),posId,from,to,meterNumber,transId,pageRecharge,totalItemsNo)
        call.enqueue(object : Callback<RechargeTransactionNewListModel> {
            override fun onResponse(call: Call<RechargeTransactionNewListModel>, response: Response<RechargeTransactionNewListModel>) {
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                var data = response.body()
                if (data != null) {
                    if (data.status.equals("true")) {

                        if (data.result.size > 0) {

                            if (data.result.size < totalItemsNo) {
                                loadings_r = false
                            } else {
                                loadings_r = true
                            }

                            if (pageRecharge == 1) {
                                rechargeListModel.clear()
                                rechargeListModel.addAll(data.result)
                            } else {
                                rechargeListModel.addAll(data.result)
                            }

                            if (pageRecharge == 1) {
                                ShowRechargeTransactionFlow()
                            } else {
                                rechargetransAdapter.notifyDataSetChanged()
                            }

                        } else {


                            if (rechargeListModel.size < 1) {
                                nodataDeposit.visibility = View.GONE
                                nodataRecharge.visibility = View.VISIBLE
                                recyclerviewDeposit.visibility = View.GONE
                                recyclerviewRecharge.visibility = View.GONE

                            }
                        }
                    } else {
                        Utilities.CheckSessionValid(data.message, requireContext(), requireActivity())

                    }
                }

            }

            override fun onFailure(call: Call<RechargeTransactionNewListModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                Utilities.shortToast("Something went wrong.", requireActivity())
            }

        })
    }

    fun filterDepositData(posId: Int, from: String, to: String, meterNumber: String, refNumber: String, transId: String, bankAccountId: String, depositType: Int) {
        this.posId=posId
        this.from=from
        this.to=to
        this.meterNumber=meterNumber
        this.transId=transId
        this.banKAccountId=bankAccountId
        this.refId=refId
        this.depositType=depositType
        var customDialog: CustomDialog
        customDialog = CustomDialog(requireActivity())
        customDialog.show()
       /* if(rechargeListModel.isNotEmpty()){
            rechargeListModel.clear()
        }*/
        isFilter=true
        val call: Call<DepositTransactionNewListModel> = Uten.FetchServerData().getDepositReports(SharedHelper.getString(requireActivity(), Constants.TOKEN),posId,from,to,meterNumber,refNumber,transId,bankAccountId,depositType,pageDeposit,totalItemsNo)
        call.enqueue(object : Callback<DepositTransactionNewListModel> {
            override fun onResponse(call: Call<DepositTransactionNewListModel>, response: Response<DepositTransactionNewListModel>) {

                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                val g = Gson()
                g.toJson(response.body())

                var data = response.body()
                if (data != null) {
                    if (data.status.equals("true")) {


                        if (data.result.size > 0) {

                            if (data.result.size < totalItemsNo) {
                                loadings_d = false
                            } else {
                                loadings_d = true
                            }

                            if (pageDeposit == 1) {
                                depositListModel.clear()
                                depositListModel.addAll(data.result)
                            } else {
                                depositListModel.addAll(data.result)
                            }
                            if (pageDeposit == 1) {
                                ShowDepositTransactionFlow()
                            } else {
                                deposittransAdapter.notifyDataSetChanged()
                            }

                        } else {

                            if (depositListModel.size < 1) {

                                nodataDeposit.visibility = View.VISIBLE
                                nodataRecharge.visibility = View.GONE
                                recyclerviewDeposit.visibility = View.GONE
                                recyclerviewRecharge.visibility = View.GONE

                            }
                        }
                    } else {
                        Utilities.CheckSessionValid(data.message, requireContext(), requireActivity())

                    }
                }

            }

            override fun onFailure(call: Call<DepositTransactionNewListModel>, t: Throwable) {
                val gs = Gson()
                gs.toJson(t.localizedMessage)
                if (customDialog.isShowing) {
                    customDialog.dismiss()
                }
                Utilities.shortToast("Something went wrong.", requireActivity())
            }

        })
    }

    override fun onResume() {
        super.onResume()

    }
}
