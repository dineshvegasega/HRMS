package com.vegasega.hrms.screens.main.employeesPerformanceScore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ItemEmployeeLeaveRequestBinding
import com.vegasega.hrms.databinding.ItemEmployeePerformanceScoreBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.genericAdapter.GenericAdapter
import com.vegasega.hrms.models.attendance.Attendance
import com.vegasega.hrms.models.leave.ItemLeaveRequest
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.models.score.Data
import com.vegasega.hrms.models.score.ItemEmployeeScore
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class EmployeesPerformanceScoreVM @Inject constructor(private val repository: Repository): ViewModel() {

    fun employeesPerformanceScoreList(callBack: ItemEmployeeScore.() -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<ItemEmployeeScore>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.employeesPerformanceScoreList()

                override fun success(response: Response<ItemEmployeeScore>) {
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



    val employeesPerformanceScoreAdapter = object : GenericAdapter<ItemEmployeePerformanceScoreBinding, Data>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemEmployeePerformanceScoreBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemEmployeePerformanceScoreBinding,
            model: Data,
            position: Int
        ) {
            binding.apply {
                textTitle.setText("Employee Name: "+model?.employee?.name ?: "")
                texDisciplineValue.setText(""+model?.score ?: "")
                textHardworkingValue.setText(""+model?.scored_by?.department_id ?: "")
                textScoredBy.setText("Scored By: "+model?.scored_by?.name ?: "")
                textDateValue.setText("Date: "+model?.created_at ?: "")

                this.root.singleClick {
                    this.root.findNavController().navigate(R.id.action_employeesPerformanceScore_to_employeesPerformanceScoreDetail, Bundle().apply {
                        putParcelable("key", model)
                    })
                }
            }
        }
    }
}