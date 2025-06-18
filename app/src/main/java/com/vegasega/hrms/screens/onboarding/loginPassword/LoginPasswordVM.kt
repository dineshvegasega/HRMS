package com.vegasega.hrms.screens.onboarding.loginPassword


import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.vegasega.hrms.networking.ApiInterface
import com.vegasega.hrms.networking.CallHandler
import com.vegasega.hrms.networking.Repository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.vegasega.hrms.R
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.datastore.DataStoreUtil.saveData
import com.vegasega.hrms.datastore.DataStoreUtil.saveObject
import com.vegasega.hrms.models.BaseResponseDC
import com.vegasega.hrms.models.Login
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.networking.*
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.TOKEN
import com.vegasega.hrms.utils.getToken
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.showSnackBar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class LoginPasswordVM @Inject constructor(private val repository: Repository
): ViewModel() {


    var loginType = 1

    fun login(view: View, jsonObject: JSONObject) = viewModelScope.launch {
        repository.callApi(
            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
                override suspend fun sendRequest(apiInterface: ApiInterface) =
                    apiInterface.login(requestBody = jsonObject.getJsonRequestBody())
                @SuppressLint("SuspiciousIndentation")
                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
                    if (response.isSuccessful){
                        if(response.body()!!.user != null){
                            val data = Gson().fromJson(response.body()!!.user, Login::class.java)
                            saveData(AUTH, response.body()!!.token ?: "")
                            saveObject(LOGIN_DATA, data)
                            TOKEN = response.body()!!.token ?: ""

                            if (data.role_id == 3){
                                if (loginType == 1){
                                    profile(view){
                                        showSnackBar(view.resources.getString(R.string.logged_in_successfully))
                                        MainActivity.mainActivity.get()?.reloadActivity("en", Main)
                                    }
                                } else {
                                    showSnackBar("Invalid Login Details")
                                }
                            } else if (data.role_id == 1){
                                if (loginType == 1){
                                    profile(view){
                                        showSnackBar(view.resources.getString(R.string.logged_in_successfully))
                                        MainActivity.mainActivity.get()?.reloadActivity("en", Main)
                                    }
                                } else {
                                    showSnackBar("Invalid Login Details")
                                }
                            }


//                            if (data.is_active != 1){
//                                view.findNavController().navigate(R.id.action_loginPassword_to_resetPassword, Bundle().apply {
//                                    putString("email", data.email)
//                                    putString("from", "login")
//                                })
//                            } else {
//                                Log.e("data", "Dataaaaa "+ data.user_role.toString())
//                                profile(view){
//                                    showSnackBar(view.resources.getString(R.string.logged_in_successfully))
//                                    MainActivity.mainActivity.get()?.reloadActivity("en", Main)
//                                }
//                            }
                        }else if(response.body()!!.message == "User does not exist"){
                            showSnackBar(view.resources.getString(R.string.user_does_not_exist))
                        } else {
                            showSnackBar(view.resources.getString(R.string.please_provide_valid_password))
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



//    fun token(jsonObject: JSONObject) = viewModelScope.launch {
//        repository.callApiWithoutLoader(
//            callHandler = object : CallHandler<Response<BaseResponseDC<JsonElement>>> {
//                override suspend fun sendRequest(apiInterface: ApiInterface) =
//                    apiInterface.mobileToken(requestBody = jsonObject.getJsonRequestBody())
//                override fun success(response: Response<BaseResponseDC<JsonElement>>) {
//                    if (response.isSuccessful){
//                    }
//                }
//                override fun error(message: String) {
////                    super.error(message)
//                }
//                override fun loading() {
////                    super.loading()
//                }
//            }
//        )
//    }



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