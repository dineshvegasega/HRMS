package com.vegasega.hrms.screens.main.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ItemAnnouncementsPageBinding
import com.vegasega.hrms.genericAdapter.GenericAdapter
import com.vegasega.hrms.models.dashboard.Announcement
import com.vegasega.hrms.models.dashboard.ItemDashboard
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class AnnouncementsVM @Inject constructor(private val repository: Repository): ViewModel() {

    val announcementsAdapter = object : GenericAdapter<ItemAnnouncementsPageBinding, Announcement>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = ItemAnnouncementsPageBinding.inflate(inflater, parent, false)

        override fun onBindHolder(
            binding: ItemAnnouncementsPageBinding,
            dataClass: Announcement,
            position: Int
        ) {
            binding.apply {
                textSno.text = ""+(position+1)+". "
                textTitle.text = "Name: "+dataClass.title+""
                textCreatedBy.text = "Created By: "+if (dataClass.created_by == 1) "Administrator" else ""
                textTitleDate.text = "Date: "+dataClass.created_at

                this.root.singleClick {
                    this.root.findNavController().navigate(R.id.action_announcements_to_announcementsDetail, Bundle().apply {
                        putParcelable("key", dataClass)
                    })
                }

            }
        }
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

}