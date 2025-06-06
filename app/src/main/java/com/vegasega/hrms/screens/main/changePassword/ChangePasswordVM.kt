package com.vegasega.hrms.screens.main.changePassword

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.google.gson.JsonElement
import com.vegasega.hrms.R
import com.vegasega.hrms.models.BaseResponseDC
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ChangePasswordVM @Inject constructor(private val repository: Repository): ViewModel() {
    var isAgree = MutableLiveData<Boolean>(false)


    var itemUpdatePasswordResult = MutableLiveData<Boolean>(false)
    fun updatePassword(hashMap: RequestBody) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.passwordUpdate(hashMap)
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        itemUpdatePasswordResult.value = true
                        showSnackBar(MainActivity.mainActivity.get()!!.getString(R.string.password_changed_successfully))
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




    fun resetPassword(hashMap: RequestBody, callback: () -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.changeUpdate(hashMap)
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
//                        itemUpdatePasswordResult.value = true
                        showSnackBar(MainActivity.mainActivity.get()!!.getString(R.string.password_changed_successfully))
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
                    showSnackBar("Current password is incorrect.")
//                    }
                }

                override fun loading() {
                    super.loading()
                }
            }
        )
    }

}