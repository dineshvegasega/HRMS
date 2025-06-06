package com.vegasega.hrms.di


import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.PACKAGE_NAME
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.SIGNATURE_NAME
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.TOKEN
import okhttp3.Interceptor
import retrofit2.http.Header


/**
 * Status Code Interceptor
 * */
object NetworkInterceptor {
    val interceptor = Interceptor { chain ->
        var request = chain.request()
        request = request.newBuilder().apply {
//            header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            header("Content-Type", "application/json;charset=utf-8")
            header("User-Agent","Mozilla/5.0")
            header("X-Android-Package", PACKAGE_NAME)
            header("X-Android-Cert", SIGNATURE_NAME)
            header("Authorization", "Bearer "+TOKEN)
            method(request.method, request.body)
        }.build()
        val response = chain.proceed(request)
        response
    }

}