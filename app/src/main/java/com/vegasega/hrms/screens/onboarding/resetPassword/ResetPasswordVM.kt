package com.vegasega.hrms.screens.onboarding.resetPassword

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vegasega.hrms.R
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.saveObject
import com.vegasega.hrms.models.BaseResponseDC
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ResetPasswordVM @Inject constructor(private val repository: Repository): ViewModel() {

    var itemUpdatePasswordResult = MutableLiveData<Boolean>(false)
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





    fun profile(view: View, callBack : () -> Unit) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.itemProfile()

                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        if(response.body()!!.profile != null){
                            val data = Gson().fromJson(response.body()!!.profile, Profile::class.java)
                            Log.e("TAG","PROFILE_DATAA "+data.toString())
                            mainThread {
                                saveObject(PROFILE_DATA, data)
                                callBack()
                            }
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