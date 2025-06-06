package com.vegasega.hrms.screens.mainActivity.menu

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import org.json.JSONObject
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.screens.mainActivity.MainActivity
import java.util.Locale


open class JsonHelper(private var context: Context) {
    private var newspaperList: MutableList<ItemMenuModel>? = null

    @SuppressLint("SuspiciousIndentation")
    open fun getMenuData(locale: Locale, roleId: Int): List<ItemMenuModel>? {

        if (newspaperList == null)
            newspaperList = ArrayList()
        try {
            var jsonObject : JSONObject = JSONObject()
            if (MainActivity.context.get()!!.getString(R.string.englishVal) == ""){
                Log.e("TAG", "AAAAAA")
                if(roleId == 3){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                } else if(roleId == 1){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english_admin.json")!!)
                }
            } else if (MainActivity.context.get()!!.getString(R.string.englishVal) == ""+locale){
                Log.e("TAG", "BBBBBBB")
                if(roleId == 3){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                } else if(roleId == 1){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english_admin.json")!!)
                }
            } else {
                Log.e("TAG", "CCCCCCC")
                Log.e("localeAA", ""+locale)
                if(roleId == 3){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english.json")!!)
                } else if(roleId == 1){
                    jsonObject = JSONObject(getJSONFromAssets("json/menu_data_english_admin.json")!!)
                }
            }

            val jsonArray = jsonObject.getJSONArray("menu")
            val k = jsonArray.length()

            for (i in 0 until k) {
                val tempJsonObject = jsonArray.getJSONObject(i).toString()
               // if (i != 9){
                    newspaperList?.add(Gson().fromJson(tempJsonObject, ItemMenuModel::class.java))
              //  }
            }
            return newspaperList
        } catch (e: Exception) {
            Log.e("TAG", "DDDDDDD")
            e.printStackTrace()
            return null
        }
    }

    private fun getJSONFromAssets(fileName: String): String? {
        val json: String
        try {
            val inputStream = context.assets.open(fileName)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return json
    }
}