package com.vegasega.hrms.screens.main.policies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ItemEmployeeLeaveRequestBinding
import com.vegasega.hrms.databinding.ItemPoliciesBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.genericAdapter.GenericAdapter
import com.vegasega.hrms.models.leave.Data
import com.vegasega.hrms.models.leave.ItemLeaveRequest
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PoliciesVM @Inject constructor(private val repository: Repository): ViewModel() {
    fun policiesList(callBack: ItemLeaveRequest.() -> Unit) = viewModelScope.launch {
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


    val policiesAdapter = object : GenericAdapter<ItemPoliciesBinding, Data>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemPoliciesBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemPoliciesBinding,
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
//                textFromDateValue.setText(""+model?.from ?: "")
//                textToDateValue.setText(""+model?.to ?: "")
//
//                textStatus.setText("Status: "+model?.status ?: "")

                this.root.singleClick {
                    this.root.findNavController().navigate(R.id.action_policies_to_policiesDetail, Bundle().apply {
                        putString("key", ""+model.id)
//                        putString("from", "view")
                    })
                }
//
//                btEdit.singleClick {
//                    this.root.findNavController().navigate(R.id.action_employeesLeaveRequest_to_employeesLeaveRequestDetail, Bundle().apply {
//                        putString("key", ""+model.id)
//                        putString("from", "edit")
//                    })
//                }
            }
        }
    }

}