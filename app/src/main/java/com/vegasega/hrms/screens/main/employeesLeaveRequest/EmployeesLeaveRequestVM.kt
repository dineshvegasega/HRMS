package com.vegasega.hrms.screens.main.employeesLeaveRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ItemAttendanceBinding
import com.vegasega.hrms.databinding.ItemEmployeeLeaveRequestBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.genericAdapter.GenericAdapter
import com.vegasega.hrms.models.BaseResponseDC
import com.vegasega.hrms.models.attendance.Attendance
import com.vegasega.hrms.models.attendance.ItemAttendanceList
import com.vegasega.hrms.models.leave.Data
import com.vegasega.hrms.models.leave.ItemLeaveRequest
import com.vegasega.hrms.models.leave.ItemLeaveRequestPost
import com.vegasega.hrms.models.leave.viewLeave.ItemViewLeave
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class EmployeesLeaveRequestVM @Inject constructor(private val repository: Repository): ViewModel() {


    var leaveType = "Sick Leave"
    var from = ""
    var to = ""

    fun employeesLeaveRequestList(callBack: ItemLeaveRequest.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemLeaveRequest>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.employeesLeaveRequestList()

                override fun success(response: Response<ItemLeaveRequest>) {
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




    val employeesLeaveRequestAdapter = object : GenericAdapter<ItemEmployeeLeaveRequestBinding, Data>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemEmployeeLeaveRequestBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemEmployeeLeaveRequestBinding,
            model: Data,
            position: Int
        ) {
            binding.apply {
                readData(PROFILE_DATA) { profile ->
                    if (profile != null) {
                        val data = Gson().fromJson(profile, Profile::class.java)
                        textTitle.setText("Employee Name: "+data?.name ?: "")
                    }
                }

//                var inOut = if(model?.attendance_time_id == 1)
//                    "IN"
//                else if(model?.attendance_time_id == 2)
//                    "OUT"
//                else "" ?: ""
//                textInOut.setText("In / Out: "+inOut)
//
//                var type = if(model?.attendance_type_id == 1)
//                    "ONTIME"
//                else if(model?.attendance_type_id == 2)
//                    "LATE"
//                else if(model?.attendance_type_id == 3)
//                    "OVERTIME"
//                else if(model?.attendance_type_id == 4)
//                    "SICK"
//                else if(model?.attendance_type_id == 5)
//                    "ABSENT"
//                else if(model?.attendance_type_id == 6)
//                    "ON_LEAVE_DAYS"
//                else if(model?.attendance_type_id == 7)
//                    "HALF_DAY"
//                else "" ?: ""
//                textType.setText("Type: "+type)
                textMessage.setText("Message: "+model?.message ?: "")
                textFromDateValue.setText(""+model?.from ?: "")
                textToDateValue.setText(""+model?.to ?: "")

                textStatus.setText("Status: "+model?.status ?: "")

                this.root.singleClick {
                    this.root.findNavController().navigate(R.id.action_employeesLeaveRequest_to_employeesLeaveRequestDetail, Bundle().apply {
                        putString("key", ""+model.id)
                        putString("from", "view")
                    })
                }

                btEdit.singleClick {
                    this.root.findNavController().navigate(R.id.action_employeesLeaveRequest_to_employeesLeaveRequestDetail, Bundle().apply {
                        putString("key", ""+model.id)
                        putString("from", "edit")
                    })
                }
            }
        }
    }



    fun employeesLeaveRequestPost(requestBody: RequestBody, callback: () -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemLeaveRequestPost>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.employeesLeaveRequestPost(requestBody)
                override fun success(response: Response<ItemLeaveRequestPost>) {
                    if (response.isSuccessful){
                        showSnackBar(MainActivity.mainActivity.get()!!.getString(R.string.requests_applied_successfully))
                        callback()
                    }
                }

                override fun error(message: String) {
                    super.error(message)
//                   var jsonObject = JSONObject(message)
//                    if (jsonObject.has("errors")){
//                        var jsonObjectErrors = jsonObject.getJSONObject("errors")
//                        var jsonArrayCurrentPassword = jsonObjectErrors.getJSONArray("current_password")
//                        if (jsonArrayCurrentPassword.length() > 0){
//                            showSnackBar(jsonArrayCurrentPassword.getString(0))
//                        } else {
//                            showSnackBar(jsonObject.getString("message"))
//                        }
//                    } else {
//                    showSnackBar("Current password is incorrect.")
//                    }
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun employeesLeaveRequestEdit(_id: String, requestBody: RequestBody, callback: () -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemLeaveRequestPost>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.employeesLeaveRequestEdit(_id, requestBody)
                override fun success(response: Response<ItemLeaveRequestPost>) {
                    if (response.isSuccessful){
                        showSnackBar(MainActivity.mainActivity.get()!!.getString(R.string.requests_applied_Updated_successfully))
                        callback()
                    }
                }

                override fun error(message: String) {
//                    super.error(message)
//                   var jsonObject = JSONObject(message)
//                    if (jsonObject.has("errors")){
//                        var jsonObjectErrors = jsonObject.getJSONObject("errors")
//                        var jsonArrayCurrentPassword = jsonObjectErrors.getJSONArray("current_password")
//                        if (jsonArrayCurrentPassword.length() > 0){
//                            showSnackBar(jsonArrayCurrentPassword.getString(0))
//                        } else {
//                            showSnackBar(jsonObject.getString("message"))
//                        }
//                    } else {
//                    showSnackBar("Current password is incorrect.")
//                    }
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }


    fun employeesLeaveRequestGet(_id : String, callBack: ItemViewLeave.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemViewLeave>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.employeesLeaveRequestGet(_id)

                override fun success(response: Response<ItemViewLeave>) {
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




}