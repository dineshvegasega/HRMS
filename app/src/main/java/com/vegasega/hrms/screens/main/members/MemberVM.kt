package com.vegasega.hrms.screens.main.members

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vegasega.hrms.models.ItemMember
import com.vegasega.hrms.models.ItemMemberRoot
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.utils.mainThread
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class MemberVM @Inject constructor(private val repository: Repository) : ViewModel() {

    var filterNameBoolean = false
    var filterMobileBoolean = false
    var filterAadhaarBoolean = false
    var filterStartDateBoolean = false
    var filterEndDateBoolean = false

    var filterName = ""
    var filterMobile= ""
    var filterAadhaar = ""
    var filterStartDate = ""
    var filterEndDate = ""


    val itemsProduct: ArrayList<ItemMember> = ArrayList()


    private var itemProducstResult = MutableLiveData<ItemMemberRoot>()
    val itemProducts: LiveData<ItemMemberRoot> get() = itemProducstResult
    fun getProducts(emptyMap: JSONObject, pageNumber: Int) =
        viewModelScope.launch {
            if (pageNumber == 0 || pageNumber == 1) {
                repository.callApi(
                    callHandler = object : CallHandler<Response<ItemMemberRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getPopularMoviesListMember(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemMemberRoot>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        var mMineUserEntity = response.body()!!
                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

                            }
                        }

                        override fun error(message: String) {

                        }

                        override fun loading() {
                            super.loading()
                        }
                    }
                )
            } else {
                repository.callApiWithoutLoader (
                    callHandler = object : CallHandler<Response<ItemMemberRoot>> {
                        override suspend fun sendRequest(apiInterface: ApiInterface) =
                            apiInterface.getPopularMoviesListMember(emptyMap.getJsonRequestBody())

                        @SuppressLint("SuspiciousIndentation")
                        override fun success(response: Response<ItemMemberRoot>) {
                            if (response.isSuccessful) {
                                mainThread {
                                    try {
                                        Log.e("TAG", "successAA: ${response.body().toString()}")
                                        var mMineUserEntity = response.body()!!
                                        itemProducstResult.value = mMineUserEntity
                                    } catch (e: Exception) {
                                    }
                                }

                            }
                        }

                        override fun error(message: String) {

                        }

                        override fun loading() {
                            super.loading()
                        }
                    }
                )
            }
        }



}