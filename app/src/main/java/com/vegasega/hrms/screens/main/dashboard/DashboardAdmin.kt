package com.vegasega.hrms.screens.main.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.DashboardAdminBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.punch.punchStatus.ItemPunchStatus
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.callPermissionDialogGPS
import com.vegasega.hrms.utils.getAddress
import com.vegasega.hrms.utils.getLocalTime
import com.vegasega.hrms.utils.isLocationEnabled
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.getValue
import kotlin.math.abs

@AndroidEntryPoint
class DashboardAdmin  : Fragment() {
    private val viewModel: DashboardVM by viewModels()
    private var _binding: DashboardAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DashboardAdminBinding.inflate(inflater)
        return binding.root
    }

//    val repeatableJob = CoroutineScope(Dispatchers.Default).launch {
//        while (true) {
//            delay(1000)
//            Log.e("TAG", "1212122")
//        }
//    }

    @SuppressLint("NotifyDataSetChanged", "MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // val menuHost: MenuHost = requireActivity()
        //createMenu(menuHost)
        MainActivity.mainActivity.get()?.callFragment(1)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.apply {
            imagePunchIn.singleClick {
                imagePosition = 1
                callMediaPermissionsWithLocation()
            }
            imagePunchOut.singleClick {
                imagePosition = 2
                callMediaPermissionsWithLocation()
            }

            btBreakIn.singleClick {
                val obj: JSONObject = JSONObject().apply {
//                    put("message", "BreakIn")
//                    put("attendance_time_id", 3)
//                    put("sick", 0)
                }
                viewModel.breakStart(obj.getJsonRequestBody()) {
//                    textCheckIn.setText(this.created_at)
                    callApi()
                }
            }
            btBreakOut.singleClick {
                val obj: JSONObject = JSONObject().apply {
//                    put("message", "BreakIn")
//                    put("attendance_time_id", 3)
//                    put("sick", 0)
                }
                viewModel.breakEnd(obj.getJsonRequestBody()) {
//                    textCheckIn.setText(this.created_at)
                    callApi()
                }
            }



            callApi()




        }




    }



    var punchStatusTimeAPI : ItemPunchStatus ?= null
    fun callApi(){
        viewModel.punchStatus() {
            var punchStatusTime = this
            punchStatusTimeAPI = punchStatusTime

            var punchIn = punchStatusTimeAPI?.attendance_today?.punch_in_time ?: null
            var punchOut = punchStatusTimeAPI?.attendance_today?.punch_out_time ?: null

            if(punchIn == null && punchOut == null){
                Log.e("TAG", "AAA111")
                stop()
            }


            if(punchIn != null && punchOut != null){
                Log.e("TAG", "AAA222")
                stop()
                start()
            }


            if(punchIn != null && punchOut == null){
                Log.e("TAG", "AAA333")
                stop()
                start()
            }
        }
    }



    var mHandler = Handler(Looper.getMainLooper())
    val task = {
        updateTimer()
        start()
    }

    fun start() {
        mHandler.postDelayed(task, 1000)
    }

    fun stop() {
        mHandler.removeCallbacksAndMessages(null);
    }




    fun updateTimer(){
        //                var punchIn = null
        var punchIn = punchStatusTimeAPI?.attendance_today?.punch_in_time ?: null
//            var punchOut = null
        var punchOut = punchStatusTimeAPI?.attendance_today?.punch_out_time ?: null


        binding.apply {
            if(punchIn == null && punchOut == null){
                Log.e("TAG", "AAA111")
                imagePunchIn.setEnabled(true)
                imagePunchOut.setEnabled(false)

                linearTime.visibility = View.GONE
            }


            if(punchIn != null && punchOut != null){
                Log.e("TAG", "AAA222")

                textCheckInTime.setText(""+punchIn?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss").toString())
                textCheckOutTime.setText(""+punchOut?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss").toString())

                var punchInTime = punchIn?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()
                var punchOutTime = punchOut?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()

                val formatter =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val datestart: Date = formatter.parse(punchInTime)
                val dateend: Date = formatter.parse(punchOutTime)

                val difference: Long = dateend.getTime() - datestart.getTime()

                var totalTime = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(difference),
                    TimeUnit.MILLISECONDS.toMinutes(difference) -
                            TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(
                                    difference
                                )
                            ), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(difference) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    difference
                                )
                            )
                )

                linearTime.visibility = View.VISIBLE
                textNumberTime.setText(""+totalTime)
            }


            if(punchIn != null && punchOut == null){
                Log.e("TAG", "AAA333")
                imagePunchIn.setEnabled(false)
                imagePunchOut.setEnabled(true)

                textCheckInTime.setText(""+punchIn?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss").toString())
//                    textCheckOutTime.setText(""+punchOut?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "HH:mm:ss").toString())

                var punchInTime = punchIn?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()
//                    var punchOutTime = punchOut?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()

                val formatter =
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val datestart: Date = formatter.parse(punchInTime)
//                    val dateend: Date = formatter.parse(punchOutTime)

//                    val difference: Long = dateend.getTime() - datestart.getTime()

                var cal = Calendar.getInstance()
                cal.time = Date()

                var difference = cal.timeInMillis - datestart.time

                var totalTime = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(difference),
                    TimeUnit.MILLISECONDS.toMinutes(difference) -
                            TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(
                                    difference
                                )
                            ), // The change is in this line
                    TimeUnit.MILLISECONDS.toSeconds(difference) -
                            TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(
                                    difference
                                )
                            )
                )

                linearTime.visibility = View.VISIBLE
                textNumberTime.setText(""+totalTime)



                linearBreakInOut.visibility = View.VISIBLE
                linearBreakTime.visibility = View.VISIBLE


                var breaks = punchStatusTimeAPI?.attendance_today?.breaks ?: emptyList()


                if (breaks.size == 0){
                    Log.e("TAG", "AAA444")
                    btBreakIn.setEnabled(true)
                    btBreakOut.setEnabled(false)
                } else {
                    Log.e("TAG", "AAA555")

                    var millsTotal: Long = 0
                    breaks.forEach {
                        var breakIn = it?.break_start_time ?: null
                        var breakOut = it?.break_end_time ?: null

                        if(breakIn == null && breakOut == null){
                            Log.e("TAG", "AAA666")
                            btBreakIn.setEnabled(true)
                            btBreakOut.setEnabled(false)
                        }

                        if(breakIn != null && breakOut != null){
                            Log.e("TAG", "AAA777")
                            btBreakIn.setEnabled(true)
                            btBreakOut.setEnabled(false)
                            imagePunchIn.setEnabled(false)
                            imagePunchOut.setEnabled(true)

                            var breckInTime = breakIn?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()
                            var breckOutTime = breakOut?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()

                            val formatter = SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            )
                            val datestart: Date = formatter.parse(breckInTime)
                            val dateend: Date = formatter.parse(breckOutTime)
                            val millse: Long = dateend.getTime() - datestart.getTime()
                            millsTotal = millsTotal + millse
                        }

                        if(breakIn != null && breakOut == null){
                            Log.e("TAG", "AAA888")
                            btBreakIn.setEnabled(false)
                            btBreakOut.setEnabled(true)
                            imagePunchIn.setEnabled(false)
                            imagePunchOut.setEnabled(false)
                            var breckInTime = breakIn?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss").toString()
                            Log.e("TAG", "AAA999 "+breckInTime)
                            val formatter = SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss",
                                Locale.getDefault()
                            )
                            val datestart: Date = formatter.parse(breckInTime)

                            var cal = Calendar.getInstance()
                            cal.time = Date()
                            var diff = cal.timeInMillis - datestart.time

                            millsTotal = millsTotal + diff
                        }

                    }


                    var breckTime = String.format(
                        "%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millsTotal),
                        TimeUnit.MILLISECONDS.toMinutes(millsTotal) -
                                TimeUnit.HOURS.toMinutes(
                                    TimeUnit.MILLISECONDS.toHours(
                                        millsTotal
                                    )
                                ), // The change is in this line
                        TimeUnit.MILLISECONDS.toSeconds(millsTotal) -
                                TimeUnit.MINUTES.toSeconds(
                                    TimeUnit.MILLISECONDS.toMinutes(
                                        millsTotal
                                    )
                                )
                    )

                    textNumberBreakTime.setText(""+breckTime)

                }

//                var breakIn = punchStatusTimeAPI?.attendance_today?.break_in_time ?: null
//                var breakOut = punchStatusTimeAPI?.attendance_today?.break_out_time ?: null

            }
        }
    }






    var imagePosition = 0
    private fun callMediaPermissionsWithLocation() {
        if (isLocationEnabled(requireActivity()) == true) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Log.e("TAG", "AAAAAAAAAAA")
                activityResultLauncherWithLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Log.e("TAG", "BBBBBBBBB")
                activityResultLauncherWithLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                Log.e("TAG", "CCCCCCCCC")
                activityResultLauncherWithLocation.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } else {
            requireActivity().callPermissionDialogGPS {
                someActivityResultLauncherWithLocationGPS.launch(this)
            }
        }
    }


    @SuppressLint("MissingPermission")
    private val activityResultLauncherWithLocation =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {

                if (imagePosition == 1) {
                    Log.e("TAG", "AAAAAAAAAAA")
                    checkInMethod()
                } else if (imagePosition == 2) {
                    Log.e("TAG", "BBBBBBBBBBB")
                    checkOutMethod()
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncherWithLocation.launch(this)
                }
            }
        }


    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissionsWithLocation()
    }

    var someActivityResultLauncherWithLocationGPS =
        registerForActivityResult<Intent, ActivityResult>(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.e("TAG", "result.resultCode " + result.resultCode)
            callMediaPermissionsWithLocation()
        }


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun checkInMethod(){
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            var latLong = LatLng(location!!.latitude, location.longitude)
            Log.e("TAG", "latLong1111 " + latLong)
            mainThread {
                binding.inLocation.text = "" + requireActivity().getAddress(latLong)

                val obj: JSONObject = JSONObject().apply {
//                    put("message", "BreakIn")
//                    put("attendance_time_id", 3)
//                    put("sick", 0)
                }
                viewModel.punchIn(obj.getJsonRequestBody()) {
//                    textCheckIn.setText(this.created_at)
                    callApi()
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun checkOutMethod(){
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            var latLong = LatLng(location!!.latitude, location.longitude)
            Log.e("TAG", "latLong1111 " + latLong)
            mainThread {
                binding.outLocation.text = "" + requireActivity().getAddress(latLong)

                val obj: JSONObject = JSONObject().apply {
//                    put("message", "BreakIn")
//                    put("attendance_time_id", 3)
//                    put("sick", 0)
                }
                viewModel.punchOut(obj.getJsonRequestBody()) {
//                    textCheckIn.setText(this.created_at)
//                    callApi()
                }
            }
        }
    }

}