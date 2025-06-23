package com.vegasega.hrms.screens.main.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ItemAnnouncementsBinding
import com.vegasega.hrms.databinding.ItemBirthdaysBinding
import com.vegasega.hrms.databinding.ItemDashboardMenusBinding
import com.vegasega.hrms.databinding.ItemHolidaysBinding
import com.vegasega.hrms.databinding.ItemRecentActivitiesBinding
import com.vegasega.hrms.datastore.DataStoreKeys
import com.vegasega.hrms.datastore.DataStoreKeys.Complaint_Feedback_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.Information_Center_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.LIVE_NOTICE_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.LIVE_SCHEME_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.LIVE_TRAINING_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.datastore.DataStoreUtil.saveObject
import com.vegasega.hrms.genericAdapter.GenericAdapter
import com.vegasega.hrms.models.BaseResponseDC
import com.vegasega.hrms.models.ItemChat
import com.vegasega.hrms.models.Login
import com.vegasega.hrms.models.ItemHistory
import com.vegasega.hrms.models.ItemInformationCenter
import com.vegasega.hrms.models.ItemLiveNotice
import com.vegasega.hrms.models.ItemLiveScheme
import com.vegasega.hrms.models.ItemLiveTraining
import com.vegasega.hrms.models.attendance.Attendance
import com.vegasega.hrms.models.attendance.ItemAttendance
import com.vegasega.hrms.models.attendance.ItemAttendanceList
import com.vegasega.hrms.models.dashboard.Announcement
import com.vegasega.hrms.models.dashboard.EmployeesBirthdayThisMonth
import com.vegasega.hrms.models.dashboard.EmployeesMarriageAnniversaryThisMonth
import com.vegasega.hrms.models.dashboard.Holiday
import com.vegasega.hrms.models.dashboard.ItemDashboard
import com.vegasega.hrms.models.profile.ItemProfile
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.models.punch.breakIn.ItemBreakStart
import com.vegasega.hrms.models.punch.breakOut.ItemBreakEnd
import com.vegasega.hrms.models.punch.punchIn.ItemPunchIn
import com.vegasega.hrms.models.punch.punchOut.ItemPunchOut
import com.vegasega.hrms.models.punch.punchStatus.ItemPunchStatus
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.screens.main.complaintsFeedback.history.History
import com.vegasega.hrms.screens.main.informationCenter.InformationCenter
import com.vegasega.hrms.screens.main.notices.liveNotices.LiveNotices
import com.vegasega.hrms.screens.main.schemes.liveSchemes.LiveSchemes
import com.vegasega.hrms.screens.main.training.liveTraining.LiveTraining
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.vegasega.hrms.utils.getLocalTime
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.parcelize.RawValue
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class DashboardVM @Inject constructor(private val repository: Repository) : ViewModel() {

    var itemMain: ArrayList<ItemModel>? = ArrayList()

    init {
        itemMain?.add(
            ItemModel(
                MainActivity.context.get()!!.getString(R.string.profiles),
                R.drawable.item_profile
            )
        )
        itemMain?.add(
            ItemModel(
                MainActivity.context.get()!!.getString(R.string.nbpa),
                R.drawable.item_live_scheme
            )
        )
//        itemMain?.add(
//            ItemModel(
//                MainActivity.context.get()!!.getString(R.string.notices),
//                R.drawable.item_live_notices
//            )
//        )
//        itemMain?.add(
//            ItemModel(
//                MainActivity.context.get()!!.getString(R.string.training),
//                R.drawable.item_live_training
//            )
//        )
//        itemMain?.add(
//            ItemModel(
//                MainActivity.context.get()!!.getString(R.string.complaints_feedback),
//                R.drawable.item_feedback
//            )
//        )
//        itemMain?.add(
//            ItemModel(
//                MainActivity.context.get()!!.getString(R.string.information_center),
//                R.drawable.information_center
//            )
//        )
    }



    fun attendancesList(callBack: ItemAttendanceList.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemAttendanceList>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.attendancesList()

                override fun success(response: Response<ItemAttendanceList>) {
                    if (response.isSuccessful) {
//                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }






    val birthdayAdapter = object : GenericAdapter<ItemBirthdaysBinding, EmployeesBirthdayThisMonth>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemBirthdaysBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemBirthdaysBinding,
            dataClass: EmployeesBirthdayThisMonth,
            position: Int
        ) {
            binding.apply {
                textTitle.text = ""+dataClass.name+" — "
                textTitleDate.text = ""+dataClass.date_of_birth?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy").toString()
            }
        }
    }



    val anniversaryAdapter = object : GenericAdapter<ItemBirthdaysBinding, EmployeesMarriageAnniversaryThisMonth>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemBirthdaysBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemBirthdaysBinding,
            dataClass: EmployeesMarriageAnniversaryThisMonth,
            position: Int
        ) {
            binding.apply {
                textTitle.text = ""+dataClass.name+" — "
                textTitleDate.text = ""+dataClass.marriage_anniversary?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy").toString()
            }
        }
    }




    val holidaysAdapter = object : GenericAdapter<ItemHolidaysBinding, Holiday>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemHolidaysBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemHolidaysBinding,
            dataClass: Holiday,
            position: Int
        ) {
            binding.apply {
                textSno.text = ""+(position+1)+". "
                textTitle.text = ""+dataClass.name+" — "
                textTitleDate.text = dataClass.date
            }
        }
    }


    val announcementsAdapter = object : GenericAdapter<ItemAnnouncementsBinding, Announcement>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemAnnouncementsBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemAnnouncementsBinding,
            dataClass: Announcement,
            position: Int
        ) {
            binding.apply {
                textSno.text = ""+(position+1)+". "
                textTitle.text = "Name: "+dataClass.title+""
                textCreatedBy.text = "Created By: "+if (dataClass.created_by == 1) "Administrator" else ""
                textTitleDate.text = "Date: "+dataClass.created_at

                this.root.singleClick {
                    this.root.findNavController().navigate(R.id.action_dashboard_to_announcementsDetail, Bundle().apply {
                        putParcelable("key", dataClass)
                    })
                }
            }
        }
    }



    val dashboardAdapter = object : GenericAdapter<ItemDashboardMenusBinding, ItemModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemDashboardMenusBinding.inflate(inflater, parent, false)

        @SuppressLint("SuspiciousIndentation")
        override fun onBindHolder(
            binding: ItemDashboardMenusBinding,
            dataClass: ItemModel,
            position: Int
        ) {
            binding.apply {
//                if(dataClass.isNew == true){
//                    animationView.visibility = View.VISIBLE
//                    textDotTxt.visibility = View.VISIBLE
//                    layoutBottomRed.visibility = View.VISIBLE
//                } else {
//                    animationView.visibility = View.GONE
//                    textDotTxt.visibility = View.GONE
//                    layoutBottomRed.visibility = View.GONE
//                }
                if (position != 0) {
                    animationView.visibility = View.VISIBLE
                    textDotTxt.visibility = View.VISIBLE
                    layoutBottomRed.visibility = View.VISIBLE
                } else {
                    animationView.visibility = View.GONE
                    textDotTxt.visibility = View.GONE
                    layoutBottomRed.visibility = View.GONE
                }
                textHeaderTxt.setText(dataClass.name)
                ivLogo.setImageResource(dataClass.image)
                root.singleClick {
                    readData(LOGIN_DATA) { loginUser ->
                        if (loginUser != null) {
                            val data = Gson().fromJson(loginUser, Login::class.java)
                            when (position) {
                                0 -> root.findNavController()
                                    .navigate(R.id.action_dashboard_to_profile)
                                1 -> {
                                    when (data.status) {
                                        "approved" -> {
                                            isProductLoad = true
                                            root.findNavController()
                                                .navigate(R.id.action_dashboard_to_nbpaList)
                                        }
                                        "unverified" -> {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                        "pending" -> {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                        "rejected" -> {
                                            showSnackBar(root.resources.getString(R.string.registration_processed))
                                        }
                                    }

                                }


                                }


//
//                            1 -> {
//                                when (data.subscription_status) {
//                                    null -> root.findNavController()
//                                        .navigate(R.id.action_dashboard_to_liveSchemes)
//                                    "trial" -> root.findNavController()
//                                        .navigate(R.id.action_dashboard_to_liveSchemes)
//                                    "active" -> root.findNavController()
//                                        .navigate(R.id.action_dashboard_to_liveSchemes)
//                                    "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
//                                }
//                            }
//                            when (data.status) {
//                                "approved" -> {
//                                    when (position) {
//                                        0 -> root.findNavController()
//                                            .navigate(R.id.action_dashboard_to_profile)
//
//                                        1 -> {
//                                            when (data.subscription_status) {
//                                                null -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveSchemes)
//                                                "trial" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveSchemes)
//                                                "active" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveSchemes)
//                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
//                                            }
//                                        }
//
//                                        2 -> {
//                                            when (data.subscription_status) {
//                                                null -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveNotices)
//                                                "trial" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveNotices)
//                                                "active" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveNotices)
//                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
//                                            }
//                                        }
//
//                                        3 -> {
//                                            when (data.subscription_status) {
//                                                null -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveTraining)
//                                                "trial" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveTraining)
//                                                "active" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_liveTraining)
//                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
//                                            }
//                                        }
//
//                                        4 -> {
//                                            when (data.subscription_status) {
//                                                null -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_history)
//                                                "trial" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_history)
//                                                "active" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_history)
//                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
//                                            }
//                                        }
//
//                                        5 -> {
//                                            when (data.subscription_status) {
//                                                null -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_informationCenter)
//                                                "trial" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_informationCenter)
//                                                "active" -> root.findNavController()
//                                                    .navigate(R.id.action_dashboard_to_informationCenter)
//                                                "expired" -> showSnackBar(root.resources.getString(R.string.expired_account_message), type = 2, MainActivity.navHostFragment?.navController)
//                                            }
//                                        }
//                                    }
//                                }
//
//                                "unverified" -> {
//                                    when (position) {
//                                        0 -> root.findNavController()
//                                            .navigate(R.id.action_dashboard_to_profile)
//                                        else -> showSnackBar(root.resources.getString(R.string.registration_processed))
//                                    }
//                                }
//
//                                "pending" -> {
//                                    when (position) {
//                                        0 -> root.findNavController()
//                                            .navigate(R.id.action_dashboard_to_profile)
//                                        else -> showSnackBar(root.resources.getString(R.string.registration_processed))
//                                    }
//                                }
//
//                                "rejected" -> {
//                                    when (position) {
//                                        0 -> root.findNavController()
//                                            .navigate(R.id.action_dashboard_to_profile)
//                                        else -> showSnackBar(root.resources.getString(R.string.registration_processed))
//                                    }
//                                }
//                            }
                        }
                    }
                }
            }

        }
    }


    val recentAdapter = object : GenericAdapter<ItemRecentActivitiesBinding, ItemModel>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemRecentActivitiesBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemRecentActivitiesBinding,
            dataClass: ItemModel,
            position: Int
        ) {
            binding.apply {
                recyclerViewRecentItems.setHasFixedSize(true)
                val headlineAdapter = RecentChildAdapter(
                    binding.root.context,
                    dataClass.items,
                    position
                )
                recyclerViewRecentItems.adapter = headlineAdapter
                recyclerViewRecentItems.layoutManager = LinearLayoutManager(binding.root.context)
            }
        }
    }


    class RecentChildAdapter(context: Context, data: List<String>?, mainPosition: Int) :
        RecyclerView.Adapter<RecentChildAdapter.ChildViewHolder>() {
        private var items: List<String>? = data
        private var inflater: LayoutInflater = LayoutInflater.from(context)
        private var parentPosition: Int = mainPosition

        override
        fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
            val view = inflater.inflate(R.layout.item_recent_activities_items, parent, false)
            return ChildViewHolder(view)
        }

        override
        fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
//            val item = items?.get(position)
            //holder.tvTitle.text = item?.title
            holder.itemView.singleClick {
            }
        }

        override
        fun getItemCount(): Int {
//            return items?.size?:0
            return 3
        }

        class ChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//            var tvTitle: AppCompatTextView = itemView.findViewById(R.id.titleChild)
        }
    }




    private var itemLiveSchemesCountResult = MutableLiveData<BaseResponseDC<JsonElement>>()
    val itemLiveSchemesCount : LiveData<BaseResponseDC<JsonElement>> get() = itemLiveSchemesCountResult

    fun liveSchemeCount(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.getPopularMoviesListCount(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemLiveSchemesCountResult.value = response.body()
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    private var itemLiveSchemesCountResultTotal = MutableLiveData<BaseResponseDC<JsonElement>>()
    val itemLiveSchemesCountTotal : LiveData<BaseResponseDC<JsonElement>> get() = itemLiveSchemesCountResultTotal

    fun liveSchemeCountTotal(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.getPopularMoviesListCount(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemLiveSchemesCountResultTotal.value = response.body()
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    private var itemLiveSchemesMembersCountResult = MutableLiveData<BaseResponseDC<JsonElement>>()
    val itemLiveSchemesMembersCount : LiveData<BaseResponseDC<JsonElement>> get() = itemLiveSchemesMembersCountResult
    fun liveSchemeMembersCount(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.getPopularMoviesListMembersCount(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemLiveSchemesMembersCountResult.value = response.body()
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    private var itemLiveSchemesMembersCountResultTotal = MutableLiveData<BaseResponseDC<JsonElement>>()
    val itemLiveSchemesMembersCountTotal : LiveData<BaseResponseDC<JsonElement>> get() = itemLiveSchemesMembersCountResultTotal
    fun liveSchemeMembersCountTotal(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.getPopularMoviesListMembersCount(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        itemLiveSchemesMembersCountResultTotal.value = response.body()
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    private var itemLiveSchemesResult = MutableLiveData<List<ItemLiveScheme>>()
    val itemLiveSchemes : LiveData<List<ItemLiveScheme>> get() = itemLiveSchemesResult

    fun liveScheme(jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.schemeHistoryList(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
                        val changeValue =
                            Gson().fromJson<List<ItemLiveScheme>>(
                                Gson().toJson(response.body()!!.data),
                                typeToken
                            )
                        itemLiveSchemesResult.value = changeValue

//                        mainThread {
//                            if (itemsCustomerOrders.size == 0) {
//                                changeValue.forEach { mapData ->
//                                    Log.e("TAG", "schemeDataInnerFalseA: " + mapData.id)
//                                    itemsCustomerOrders.add(mapData)
//                                }
//                            } else {
//
//
//
//                                changeValue.forEach { mapData ->
//                                    var aaa = getData(mapData)
//                                    Log.e( "TAG",  "aaa: " + mapData.id + " :::: "+ aaa)
//                                    if (aaa == true) {
//                                        itemsCustomerOrders.add(mapData)
//                                    }
//
////                                    getData
//
////                                    itemsCustomerOrders.forEach { schemeDataInner ->
////                                        if (schemeDataInner.id != mapData.id) {
////                                            Log.e( "TAG",  "schemeDataInnerFalseB: " + mapData.id + " :::: "+ schemeDataInner.id)
////                                            itemsCustomerOrders.add(mapData)
////                                        }
////                                    }
//
////                                    itemsCustomerOrders.forEach { schemeDataInner ->
////                                        if (schemeDataInner.id != mapData.id) {
////                                            Log.e(
////                                                "TAG",
////                                                "schemeDataInnerFalse: " + schemeDataInner.id
////                                            )
////                                            itemsCustomerOrders.add(schemeDataInner)
////                                        }
////
////                                        if (schemeDataInner.id == mapData.id) {
////                                            Log.e(
////                                                "TAG",
////                                                "schemeDataInnerTrue: " + schemeDataInner.id
////                                            )
////                                        }
////                                    }
//                                }
//                            }
////                                itemsCustomerOrders.map { mapData ->
////                                    changeValue.map { schemeDataInner ->
////                                        if (schemeDataInner.id != mapData.id) {
////                                            Log.e("TAG", "schemeDataInner: " + schemeDataInner.id)
////                                            itemsCustomerOrders.add(schemeDataInner)
////                                        }
////                                    }
////                                }
//////                            }
//
//                            Log.e("TAG", "onViewCreatedXXX: " + itemsCustomerOrders.size)
//                            itemLiveSchemesResult.value = true
//                        }


                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                    showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }






    var isScheme = MutableLiveData<Boolean>(false)
    fun liveScheme(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.liveScheme(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        val typeToken = object : TypeToken<List<ItemLiveScheme>>() {}.type
                        val changeValue = Gson().fromJson<List<ItemLiveScheme>>(
                            Gson().toJson(response.body()!!.data),
                            typeToken
                        )
                        readData(LIVE_SCHEME_DATA) { loginUser ->
                            if (loginUser != null) {
                                val savedValue =
                                    Gson().fromJson<List<ItemLiveScheme>>(loginUser, typeToken)
                                if (changeValue != savedValue) {
                                    isScheme.value = true
                                } else {
                                    isScheme.value = false
                                }
                            } else {
                                saveObject(
                                    DataStoreKeys.LIVE_SCHEME_DATA, changeValue
                                )
                                isScheme.value = false
                            }
//                            Log.e("TAG", "LiveSchemes.isReadLiveSchemes"+LiveSchemes.isReadLiveSchemes)
                            if (LiveSchemes.isReadLiveSchemes == true) {
                                saveObject(
                                    DataStoreKeys.LIVE_SCHEME_DATA, changeValue
                                )
                                isScheme.value = false
                                LiveSchemes.isReadLiveSchemes = false
                            }

                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //  showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var isNotice = MutableLiveData<Boolean>(false)
    fun liveNotice(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.liveNotice(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        val typeToken = object : TypeToken<List<ItemLiveNotice>>() {}.type
                        val changeValue = Gson().fromJson<List<ItemLiveNotice>>(
                            Gson().toJson(response.body()!!.data),
                            typeToken
                        )
                        readData(LIVE_NOTICE_DATA) { loginUser ->
                            if (loginUser != null) {
                                val savedValue =
                                    Gson().fromJson<List<ItemLiveNotice>>(loginUser, typeToken)
                                if (changeValue != savedValue) {
                                    isNotice.value = true
                                } else {
                                    isNotice.value = false
                                }
                            } else {
                                saveObject(
                                    DataStoreKeys.LIVE_NOTICE_DATA, changeValue
                                )
                                isNotice.value = false
                            }
                            if (LiveNotices.isReadLiveNotices == true) {
                                saveObject(
                                    DataStoreKeys.LIVE_NOTICE_DATA, changeValue
                                )
                                isNotice.value = false
                                LiveNotices.isReadLiveNotices = false
                            }
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //   showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var isTraining = MutableLiveData<Boolean>(false)
    fun liveTraining(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.liveTraining(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        //isTraining.value = true
                        val typeToken = object : TypeToken<List<ItemLiveTraining>>() {}.type
                        val changeValue = Gson().fromJson<List<ItemLiveTraining>>(
                            Gson().toJson(response.body()!!.data),
                            typeToken
                        )
                        readData(LIVE_TRAINING_DATA) { loginUser ->
                            if (loginUser != null) {
                                val savedValue =
                                    Gson().fromJson<List<ItemLiveTraining>>(loginUser, typeToken)
                                if (changeValue != savedValue) {
                                    isTraining.value = true
                                } else {
                                    isTraining.value = false
                                }
                            } else {
                                saveObject(
                                    DataStoreKeys.LIVE_TRAINING_DATA, changeValue
                                )
                                isTraining.value = false
                            }

                            if (LiveTraining.isReadLiveTraining == true) {
                                saveObject(
                                    DataStoreKeys.LIVE_TRAINING_DATA, changeValue
                                )
                                isTraining.value = false
                                LiveTraining.isReadLiveTraining = false
                            }
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //   showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    var isComplaintFeedback = MutableLiveData<Boolean>(false)
    fun complaintFeedbackHistory(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.complaintFeedbackHistory(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        val typeToken = object : TypeToken<List<ItemHistory>>() {}.type
                        val changeValue = Gson().fromJson<List<ItemHistory>>(
                            Gson().toJson(response.body()!!.data),
                            typeToken
                        )
                        changeValue.map {
//                            runBlocking {
//                                feedbackConversationDetails(view, ""+it.feedback_id)
//                            }
//                            Log.e("TAG", "aaaaa_idXXX ")
//                            val call1 = async { feedbackConversationDetails(view, ""+it.feedback_id)}
//                            val data1Response:BaseResponse<Data1>?
//                            try {
//                                data1Response = call1.await()
//
//                            } catch (ex: Exception) {
//                                ex.printStackTrace()
//                            }

//                            feedbackConversationDetails(view, ""+it.feedback_id){
//                                Log.e("TAG", "aaaaa_idZZZ "+it.feedback_id)
//                                Log.e("TAG", "aaaaa_idXXX "+this.toString())
//                            }
                        }
//                        feedbackConversationDetails(view, "82"){
////                            Log.e("TAG", "aaaaa_idZZZ "+it.feedback_id)
//                            Log.e("TAG", "aaaaa_idXXX "+this.toString())
//                        }
                        readData(Complaint_Feedback_DATA) { loginUser ->
                            if (loginUser != null) {
                                val savedValue =
                                    Gson().fromJson<List<ItemHistory>>(loginUser, typeToken)
                                if (changeValue != savedValue) {
                                    isComplaintFeedback.value = true
                                } else {
                                    isComplaintFeedback.value = false
                                }
                            } else {
                                saveObject(
                                    DataStoreKeys.Complaint_Feedback_DATA, changeValue
                                )
                                isComplaintFeedback.value = false
                            }

                            if (History.isReadComplaintFeedback == true) {
                                saveObject(
                                    DataStoreKeys.Complaint_Feedback_DATA, changeValue
                                )
                                isComplaintFeedback.value = false
                                History.isReadComplaintFeedback = false
                            }
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //     showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    //    private var feedbackConversationLiveData = MutableLiveData<ItemChat>()
//    val feedbackConversationLive : LiveData<ItemChat> get() = feedbackConversationLiveData
    fun feedbackConversationDetails(view: View, _id: String, callBack: ItemChat.() -> Unit) =
        viewModelScope.launch {
            repository.callApi(
                callHandler = object : CallHandler<Response<ItemChat>> {
                    override suspend fun sendRequest(apiInterface: ApiInterface) =
                        apiInterface.feedbackConversationDetails(_id, "1")

                    override fun success(response: Response<ItemChat>) {
                        if (response.isSuccessful) {
                            callBack(response.body()!!)
//                        if(response.body()!!.data != null){
//                            Log.e("TAG", "aaaaa "+response.body()!!.data.toString())
//                            Log.e("TAG", "aaaaa_id "+_id)
//
////                            feedbackConversationLiveData.value = response.body()
//                        }
                        }
                    }

                    override fun error(message: String) {
                        super.error(message)
                    }

                    override fun loading() {
                        super.loading()
                    }
                }
            )
        }


    var isInformationCenter = MutableLiveData<Boolean>(false)
    fun informationCenter(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.informationCenter(requestBody = jsonObject.getJsonRequestBody())

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        //isTraining.value = true
                        val typeToken = object : TypeToken<List<ItemInformationCenter>>() {}.type
                        val changeValue = Gson().fromJson<List<ItemInformationCenter>>(
                            Gson().toJson(response.body()!!.data),
                            typeToken
                        )
                        readData(Information_Center_DATA) { loginUser ->
                            if (loginUser != null) {
                                val savedValue = Gson().fromJson<List<ItemInformationCenter>>(
                                    loginUser,
                                    typeToken
                                )
                                if (changeValue != savedValue) {
                                    isInformationCenter.value = true
                                } else {
                                    isInformationCenter.value = false
                                }
                            } else {
                                saveObject(
                                    DataStoreKeys.Information_Center_DATA, changeValue
                                )
                                isInformationCenter.value = false
                            }

                            if (InformationCenter.isReadInformationCenter == true) {
                                saveObject(
                                    DataStoreKeys.Information_Center_DATA, changeValue
                                )
                                isInformationCenter.value = false
                                InformationCenter.isReadInformationCenter = false
                            }
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                    //         showSnackBar(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


//
//    private var itemAdsResult = MutableLiveData< ArrayList<ItemAds>>()
//    val itemAds : LiveData< ArrayList<ItemAds>> get() = itemAdsResult
//    fun adsList(view: View) = viewModelScope.launch {
//        repository.callApi(
//            callHandler = object : CallHandler<Response<BaseResponseDC<List<ItemAds>>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.adsList()
//                override fun success(response: Response<BaseResponseDC<List<ItemAds>>>) {
//                    if (response.isSuccessful){
//                        itemAdsResult.value = response.body()?.data as ArrayList<ItemAds>
//                    }
//                }
//
//                override fun error(message: String) {
//                    super.error(message)
//                }
//
//                override fun loading() {
//                    super.loading()
//                }
//            }
//        )
//    }


    fun profile(view: View) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.itemProfile()

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.profile != null) {
                            val data = Gson().fromJson(response.body()!!.profile, Profile::class.java)
                            Log.e("TAG", "dataAA: "+data.toString())
                            saveObject(
                                PROFILE_DATA,
                                Gson().fromJson(response.body()!!.profile, Profile::class.java)
                            )

                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    private var itemDashboardResult = MutableLiveData<ItemDashboard>()
    val itemDashboardLive : LiveData<ItemDashboard> get() = itemDashboardResult
    fun dashboard(view: View, callBack: ItemDashboard.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemDashboard>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.dashboard()

                override fun success(response: Response<ItemDashboard>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            callBack(response.body()!!)
                            itemDashboardResult.value = response.body()
                        }
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    fun attendances(requestBody: RequestBody, callBack: Attendance.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemAttendance>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.attendances(requestBody)

                override fun success(response: Response<ItemAttendance>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!.attendance)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    fun punchStatus(callBack: ItemPunchStatus.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemPunchStatus>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.punchStatus()

                override fun success(response: Response<ItemPunchStatus>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    fun punchIn(requestBody: RequestBody, callBack: ItemPunchIn.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemPunchIn>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.punchIn(requestBody)

                override fun success(response: Response<ItemPunchIn>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun punchOut(requestBody: RequestBody, callBack: ItemPunchOut.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemPunchOut>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.punchOut(requestBody)

                override fun success(response: Response<ItemPunchOut>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }



    fun breakStart(requestBody: RequestBody, callBack: ItemBreakStart.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemBreakStart>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.breakStart(requestBody)

                override fun success(response: Response<ItemBreakStart>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }




    fun breakEnd(requestBody: RequestBody, callBack: ItemBreakEnd.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemBreakEnd>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.breakEnd(requestBody)

                override fun success(response: Response<ItemBreakEnd>) {
                    if (response.isSuccessful) {
                        showSnackBar(response.body()!!.message)
                        callBack(response.body()!!)
                    }
                }

                override fun error(message: String) {
                    super.error(message)
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

}

data class ItemModel(
    var name: String = "",
    var image: Int = 0,
    var isNew: Boolean = false,
    val items: @RawValue ArrayList<String>? = ArrayList(),
)