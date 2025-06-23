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
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.DashboardBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.attendance.Attendance
import com.vegasega.hrms.models.dashboard.Announcement
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.hrms.utils.callNetworkDialog
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.callPermissionDialogGPS
import com.vegasega.hrms.utils.changeDateFormat
import com.vegasega.hrms.utils.getAddress
import com.vegasega.hrms.utils.getDuration
import com.vegasega.hrms.utils.getLocalTime
import com.vegasega.hrms.utils.isLocationEnabled
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.showOptions
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.concurrent.TimeUnit
import kotlin.collections.List
import kotlin.collections.forEach
import kotlin.math.abs


@AndroidEntryPoint
class Dashboard : Fragment() {
    private val viewModel: DashboardVM by viewModels()
    private var _binding: DashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var timer = Timer()

    var startTime = ""
    var endTime = ""
    var type = 0
    var isBreakType = 0
    var prevMillsTotal: Long = 0

    var breakInTime = ""
    var prevBreakInTimeMillsTotal: Long = 0


    var imagePosition = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DashboardBinding.inflate(inflater)
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

            binding.recyclerViewBirthday.setHasFixedSize(true)
            binding.recyclerViewBirthday.adapter = viewModel.birthdayAdapter

            binding.recyclerViewAnniversary.setHasFixedSize(true)
            binding.recyclerViewAnniversary.adapter = viewModel.anniversaryAdapter

            binding.recyclerViewHoliday.setHasFixedSize(true)
            binding.recyclerViewHoliday.adapter = viewModel.holidaysAdapter

            binding.recyclerViewAnnouncements.setHasFixedSize(true)
            binding.recyclerViewAnnouncements.adapter = viewModel.announcementsAdapter


            viewModel.itemDashboardLive.observe(viewLifecycleOwner, Observer {
//                var dashboardData = it

                viewModel.birthdayAdapter.notifyDataSetChanged()
                viewModel.birthdayAdapter.submitList(it.employees_birthday_this_month)

                if (it.employees_birthday_this_month.size > 0) {
                    binding.layoutBirthday.visibility = View.VISIBLE
                } else {
                    binding.layoutBirthday.visibility = View.GONE
                }



                viewModel.anniversaryAdapter.notifyDataSetChanged()
                viewModel.anniversaryAdapter.submitList(it.employees_marriage_anniversary_this_month)
                if (it.employees_marriage_anniversary_this_month.size > 0) {
                    binding.layoutAnniversary.visibility = View.VISIBLE
                } else {
                    binding.layoutAnniversary.visibility = View.GONE
                }



                viewModel.holidaysAdapter.notifyDataSetChanged()
                viewModel.holidaysAdapter.submitList(it.holidays)
                if (it.holidays.size > 0) {
                    binding.layoutHoliday.visibility = View.VISIBLE
                } else {
                    binding.layoutHoliday.visibility = View.GONE
                }

                textHeaderAnnouncementsTxt.post(object : Runnable {
                    override fun run() {
                        textHeaderAnnouncementsTxt.setSelected(true)
                    }
                })



                var announcements : ArrayList<Announcement> = ArrayList()
                    var countOfAn = 0
                    it.announcements.forEach {
                        if (countOfAn <= 2){
                            announcements.add(it)
                            countOfAn++
                        }
                    }
                viewModel.announcementsAdapter.notifyDataSetChanged()
                viewModel.announcementsAdapter.submitList(announcements)


                if (it.announcements.size > 0) {
                    binding.layoutAnnouncements.visibility = View.VISIBLE
                    textHeaderAnnouncementsTxt.post(object : Runnable {
                        override fun run() {
                            textHeaderAnnouncementsTxt.setSelected(true)
                        }
                    })
//                    textHeaderAnnouncementsTxt.setText(HtmlCompat.fromHtml(requireContext().getString(R.string.textmarque), HtmlCompat.FROM_HTML_MODE_LEGACY));

                } else {
                    binding.layoutAnnouncements.visibility = View.GONE
                }
            })



            btCheckIn.setEnabled(true)
            btCheckIn.singleClick {
                imagePosition = 1
                callMediaPermissionsWithLocation()
//                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
//                    var latLong = LatLng(location!!.latitude, location.longitude)
//                }
//                readData(AUTH) { token ->
//                    Log.e("TAG", "btCheckIn " + token)
//                    val obj: JSONObject = JSONObject().apply {
//                        put("message", "Arrived at work")
//                        put("attendance_time_id", 1)
//                        put("sick", 0)
//                    }
//                    viewModel.attendances(obj.getJsonRequestBody()) {
//                        textCheckIn.setText(getLocalTime(this.created_at))
//                        callAttendancesList()
//                    }
//                }
            }

            btCheckOut.setEnabled(true)
            btCheckOut.singleClick {
                imagePosition = 2
                callMediaPermissionsWithLocation()
            }


            btBreakIn.singleClick {
                readData(AUTH) { token ->
                    Log.e("TAG", "btBreakIn " + token)
                    val obj: JSONObject = JSONObject().apply {
                        put("message", "BreakIn")
                        put("attendance_time_id", 3)
                        put("sick", 0)
                    }
                    viewModel.attendances(obj.getJsonRequestBody()) {
                        textCheckIn.setText(this.created_at)
                        callAttendancesList()
                    }
                }
            }

            btBreakOut.singleClick {
                readData(AUTH) { token ->
                    Log.e("TAG", "btBreakOut " + token)
                    val obj: JSONObject = JSONObject().apply {
                        put("message", "BreakOut")
                        put("attendance_time_id", 4)
                        put("sick", 0)
                    }
                    viewModel.attendances(obj.getJsonRequestBody()) {
                        textCheckIn.setText(this.created_at)
                        callAttendancesList()
                    }
                }
            }


            if (networkFailed) {
                callApis()
                callAttendancesList()
            } else {
                requireContext().callNetworkDialog()
            }

//            viewModel.itemLiveSchemesCount.observe(viewLifecycleOwner, Observer {
//                textNumberOfNBPA.text = "" + it.meta!!.total_items
//            })
//
//            viewModel.itemLiveSchemesCountTotal.observe(viewLifecycleOwner, Observer {
//                textTotalNumberOfNBPA.text = "" + it.meta!!.total_items
//            })

//            viewModel.itemLiveSchemesMembersCount.observe(viewLifecycleOwner, Observer {
//                var jsonObject = JSONObject(it.data!!.toString())
//                textNumberOfDistributers.text = "" + jsonObject.getString("total_user")
//            })
//
//            viewModel.itemLiveSchemesMembersCountTotal.observe(viewLifecycleOwner, Observer {
//                var jsonObject = JSONObject(it.data!!.toString())
//                textNumberOfDistributersTotal.text = "" + jsonObject.getString("total_user")
//            })

//            viewModel.adsList(view)
//            val adapter = BannerViewPagerAdapter(requireContext())
//
//            viewModel.itemAds.observe(viewLifecycleOwner, Observer {
//                if (it != null) {
//                    viewModel.itemAds.value?.let { it1 ->
//                        adapter.submitData(it1)
//                        banner.adapter = adapter
//                        tabDots.setupWithViewPager(banner, true)
//                        banner.autoScroll()
//                    }
//                }
//            })
        }
    }

    private fun callApis() {
        readData(AUTH) { token ->
            Log.e("TAG", "btCheckOut " + token)
            viewModel.profile(view = requireView())
            viewModel.dashboard(view = requireView()) {


            }
        }
    }


    private fun callAttendancesList() {
        readData(AUTH) { token ->
            viewModel.attendancesList() {
                if (this.attendances.data.size > 0) {
                    val sdf = SimpleDateFormat("yyyy-MM-dd")
                    val currentDate = sdf.format(Date())

                    val dataAttendanceIn: ArrayList<Attendance> = ArrayList()
                    val dataAttendanceOut: ArrayList<Attendance> = ArrayList()
                    val breakIn: ArrayList<Attendance> = ArrayList()
                    val breakOut: ArrayList<Attendance> = ArrayList()
                    var allList = this.attendances.data
                    Collections.reverse(allList)

                    this.attendances.data.forEach {
//                        Log.e("TAG", "attendancesid " + it.created_at + "   "+currentDate)
                        if (it.created_at.contains(currentDate)) {
                            Log.e("TAG", "attendancesid " + it.id)

                            if (it.attendance_time_id == 1) {
                                dataAttendanceIn.add(it)
                            }

                            if (it.attendance_time_id == 2) {
                                dataAttendanceOut.add(it)
                            }

                            if (it.attendance_time_id == 3) {
                                breakIn.add(it)
                            }

                            if (it.attendance_time_id == 4) {
                                breakOut.add(it)
                            }
                        }
                    }


                    if (dataAttendanceIn.size > 0) {
                        var first = dataAttendanceIn.first()
                        Log.e("TAG", "attendancesfirst " + first.toString())
                        binding.textCheckIn.text = first.created_at
                    }



                    if (dataAttendanceIn.size > dataAttendanceOut.size) {
                        var millsTotal: Long = 0
                        var counter = 0
                        dataAttendanceOut.forEach {
                            var timeIn = dataAttendanceIn[counter].created_at
                            var timeOut = dataAttendanceOut[counter].created_at
                            Log.e("TAG", "timeIn " + timeIn)
                            Log.e("TAG", "timeOut " + timeOut)

                            val formatter =
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                            val datestart: Date = formatter.parse(timeIn)
                            val dateend: Date = formatter.parse(timeOut)

                            val millse: Long = datestart.getTime() - dateend.getTime()

                            millsTotal = millsTotal + millse

                            counter = counter + 1
                        }

                        var timeIn =
                           dataAttendanceIn[dataAttendanceIn.size - 1].created_at
                        Log.e("TAG", "timeIntimeIn " + timeIn)
                        binding.textNumberTime.text = ""
                        endTime = timeIn
                        type = 1
                        prevMillsTotal = millsTotal
                        Log.e("TAG", "11111111111111111 ")
                        binding.textCheckOut.text = ""
                        stop()
                        start()
                    } else {
                        var millsTotal: Long = 0
                        var counter = 0
                        dataAttendanceIn.forEach {
                            var timeIn = it.created_at
                            var timeOut = dataAttendanceOut[counter].created_at
                            Log.e("TAG", "timeIn " + timeIn)
                            Log.e("TAG", "timeOut " + timeOut)

//                            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                            val datestart: Date = formatter.parse(timeIn)
//                            val dateend: Date = formatter.parse(timeOut)
//
//                            val millse: Long = datestart.getTime() - dateend.getTime()


                            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val date1 = format.parse(timeIn)
                            val date2 = format.parse(timeOut)
                            val difference = date2.getTime() - date1.getTime()
                            Log.e("TAG", "calBreakIndiffdifference" + difference)

                            var ss = String.format(
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
                            );
                            Log.e("TAG", "ssQQQQQ " + ss)

                            millsTotal = millsTotal + difference
//                            val millsTotalTime = abs(millsTotal.toDouble()).toLong()
//                            var mills = millsTotalTime
//                            val hours = (mills / (1000 * 60 * 60)).toInt()
//                            val mins = (mills / (1000 * 60)).toInt() % 60
//                            val secs = ((mills / 1000).toInt() % 60).toLong()
//
//                            var dateTime =
//                                "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
//                            Log.e("TAG", "differenceTimeAAAAAAAAA " + dateTime)
                            counter = counter + 1
                        }
                        prevMillsTotal = millsTotal
                        type = 3
                        if (dataAttendanceIn.size > 0) {
                            var last = dataAttendanceOut.last()
                            Log.e("TAG", "attendanceslast " + last.toString())
                            binding.textCheckOut.text =last.created_at
                            type = 2
                        }
                        stop()
                        start()
                        Log.e("TAG", "22222222222222222222222 ")
                    }


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    if (breakIn.size > 0) {
                        var firstIn = breakIn.first()

//                        Log.e("TAG", "attendancesfirst " + firstIn.toString())

//                        var lastOut = breakOut.last()
//                        Log.e("TAG", "attendanceslast " + lastOut.toString())


                        if (breakIn.size > breakOut.size) {
                            var millsTotal: Long = 0
                            var counter = 0
                            breakIn.forEach {
                                var timeIn = breakIn[counter].created_at

                                if (breakOut.size != 0) {
                                    try {
                                        var timeOut = breakOut[counter].created_at
//                                        Log.e("TAG", "timeIn " + timeIn)
//                                        Log.e("TAG", "timeOut " + timeOut)

                                        val formatter = SimpleDateFormat(
                                            "yyyy-MM-dd HH:mm:ss",
                                            Locale.getDefault()
                                        )
                                        val datestart: Date = formatter.parse(timeIn)
                                        val dateend: Date = formatter.parse(timeOut)

                                        val millse: Long = datestart.getTime() - dateend.getTime()

                                        millsTotal = millsTotal + millse
                                    } catch (e: Exception) {

                                    }
                                    counter = counter + 1
                                } else {

                                }
                            }
                            prevBreakInTimeMillsTotal = millsTotal
                            isBreakType = 1

                            var timeIn = breakIn[breakIn.size - 1].created_at

                            breakInTime = timeIn
////                            Log.e("TAG", "timeIntimeIn " + timeIn)
//                            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                            val datestart: Date = formatter.parse(timeIn)
//
//                            var cal = Calendar.getInstance()
//                            cal.time = Date()
//
//                            var diff = cal.timeInMillis - datestart.time
//
//
////                            Log.e("TAG", "diffdiffdiffdiffBBB " + diff)
//
//
//                            val millsTotalTime = abs(millsTotal.toDouble()).toLong()
////                            Log.e("TAG", "diffdiffdiffdiffAAA " + millsTotalTime)
//                            var mills = millsTotalTime + diff
//                            val hours = (mills / (1000 * 60 * 60)).toInt()
//                            val mins = (mills / (1000 * 60)).toInt() % 60
//                            val secs = ((mills / 1000).toInt() % 60).toLong()
//
//                            var dateTime =
//                                "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
//                            Log.e("TAG", "differenceTimeA " + dateTime)


                        } else {
                            var millsTotal: Long = 0
                            var counter = 0
                            breakIn.forEach {
                                var timeIn = it.created_at
                                var timeOut = breakOut[counter].created_at
//                                Log.e("TAG", "timeIn " + timeIn)
//                                Log.e("TAG", "timeOut " + timeOut)

                                val formatter =
                                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                                val datestart: Date = formatter.parse(timeIn)
                                val dateend: Date = formatter.parse(timeOut)

                                val millse: Long = datestart.getTime() - dateend.getTime()

                                millsTotal = millsTotal + millse

                                counter = counter + 1

                                binding.linearBreakTime.visibility = View.GONE
                            }
                            prevBreakInTimeMillsTotal = millsTotal
                            isBreakType = 2

//                            val mills = abs(millsTotal.toDouble()).toLong()
//                            val hours = (mills / (1000 * 60 * 60)).toInt()
//                            val mins = (mills / (1000 * 60)).toInt() % 60
//                            val secs = ((mills / 1000).toInt() % 60).toLong()
//
//                            var dateTime =
//                                "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
//                            Log.e("TAG", "differenceTimeB " + dateTime)
                        }
                    } else {
                        binding.linearBreakTime.visibility = View.GONE
                    }


                } else {
                    binding.btCheckIn.setEnabled(true)
                    binding.btCheckOut.setEnabled(false)

                    binding.linearBreakInOut.visibility = View.GONE
                    binding.linearBreakTime.visibility = View.GONE
                }
            }
        }
    }


    val task = {
        updateTimer(startTime, endTime, type)
        start()
    }

    fun start() {
        mHandler.postDelayed(task, 1000)
    }

    fun stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    var mHandler = Handler(Looper.getMainLooper())


    override fun onStop() {
        super.onStop()
        Log.e("TAG", "onStop")
//        isProductLoad = true
//        isProductLoadMember = true
        stop()
    }


    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isProductLoad = false
//        isProductLoadMember = false
//    }


    private fun updateTimer(createdAt: String, endTime: String, type: Int) {
        requireActivity().runOnUiThread {

//            Log.e("TAG", "typeAAAAAAA " + type)

            try {
                if (type == 1) {
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val datestart: Date = formatter.parse(endTime)

                    var cal = Calendar.getInstance()
                    cal.time = Date()

                    var diff = cal.timeInMillis - datestart.time


//                    Log.e("TAG", "diffdiffdiffdiffBBB " + diff)


                    val millsTotalTime = abs(prevMillsTotal.toDouble()).toLong()
//                    Log.e("TAG", "diffdiffdiffdiffAAA " + millsTotalTime)
                    var mills = millsTotalTime + diff
                    val hours = (mills / (1000 * 60 * 60)).toInt()
                    val mins = (mills / (1000 * 60)).toInt() % 60
                    val secs = ((mills / 1000).toInt() % 60).toLong()

                    var dateTime =
                        "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
                    Log.e("TAG", "differenceTimeAA " + dateTime)

                    binding.textNumberTime.text = dateTime
                    binding.linearTime.visibility = View.VISIBLE
                    binding.linearTime.visibility = View.VISIBLE


                    binding.linearBreakInOut.visibility = View.VISIBLE
//                    binding.linearBreakTime.visibility = View.VISIBLE
                    binding.btCheckIn.setEnabled(false)
                    binding.btCheckOut.setEnabled(true)
                    Log.e("TAG", "isBreakTypeisBreakType " + isBreakType)


                    if (isBreakType == 1) {
                        binding.btCheckIn.setEnabled(false)


                        binding.btBreakIn.setEnabled(false)
                        binding.btBreakOut.setEnabled(true)

//
//                        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                        val datestart: Date = formatter.parse(breakInTime)
//
//                        var cal = Calendar.getInstance()
//                        cal.time = Date()
//
//                        var diff = cal.timeInMillis - datestart.time
//
//
////                            Log.e("TAG", "diffdiffdiffdiffBBB " + diff)


                        var cal = Calendar.getInstance()
                        cal.time = Date()
                        var timeInA = cal.timeInMillis
                        Log.e("TAG", "timeInA " + timeInA)

//        val time1 = "06-06-2025 01:52:06"
//        val time2 = "06-06-2025 03:15:00"

                        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        val date1 = format.parse(breakInTime)
//                        val date2 = format.parse(timeInA)
                        val difference =timeInA - date1.getTime()


                        val millsTotalTime = abs(prevBreakInTimeMillsTotal.toDouble()).toLong()
//                            Log.e("TAG", "diffdiffdiffdiffAAA " + millsTotalTime)
                        var mills = millsTotalTime + difference
                        val hours = (mills / (1000 * 60 * 60)).toInt()
                        val mins = (mills / (1000 * 60)).toInt() % 60
                        val secs = ((mills / 1000).toInt() % 60).toLong()

                        var dateTime =
                            "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
                        Log.e("TAG", "differenceTimeA " + dateTime)

                        binding.linearBreakTime.visibility = View.VISIBLE
                        binding.textNumberBreakTime.text = dateTime

                        binding.btCheckIn.setEnabled(false)
                        binding.btCheckOut.setEnabled(false)

                    } else if (isBreakType == 2) {
                        val mills = abs(prevBreakInTimeMillsTotal.toDouble()).toLong()
                        val hours = (mills / (1000 * 60 * 60)).toInt()
                        val mins = (mills / (1000 * 60)).toInt() % 60
                        val secs = ((mills / 1000).toInt() % 60).toLong()

                        var dateTime =
                            "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
                        Log.e("TAG", "differenceTimeB " + dateTime)

                        binding.linearBreakTime.visibility = View.VISIBLE
                        binding.textNumberBreakTime.text = dateTime

                        binding.btCheckIn.setEnabled(false)
                        binding.btCheckOut.setEnabled(true)

                        binding.btBreakIn.setEnabled(true)
                        binding.btBreakOut.setEnabled(false)
                    } else if (isBreakType == 0) {
                        binding.btBreakOut.setEnabled(false)
                    }

                }




                if (type == 2) {
                    val mills = abs(prevMillsTotal.toDouble()).toLong()
                    val hours = (mills / (1000 * 60 * 60)).toInt()
                    val mins = (mills / (1000 * 60)).toInt() % 60
                    val secs = ((mills / 1000).toInt() % 60).toLong()

                    var dateTime =
                        "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
                    Log.e("TAG", "differenceTimeBB " + dateTime)

                    binding.textNumberTime.text = dateTime
                    binding.linearTime.visibility = View.VISIBLE


                    binding.btCheckIn.setEnabled(true)
                    binding.btCheckOut.setEnabled(false)

                    binding.linearBreakInOut.visibility = View.GONE
                    binding.linearBreakTime.visibility = View.GONE


                }


                if (type == 3) {
                    binding.textNumberTime.text = ""
                    binding.linearTime.visibility = View.VISIBLE

                    binding.btCheckIn.setEnabled(true)
                    binding.btCheckOut.setEnabled(false)

                    binding.linearBreakInOut.visibility = View.GONE
                    binding.linearBreakTime.visibility = View.GONE
                    binding.linearTime.visibility = View.GONE

                }


//                var dateTime2 =
//                    createdAt.changeDateFormat("dd-MM-yyyy HH:mm:ss", "dd MM, yyyy hh:mm:ss")
//                Log.e("TAG", "dateTimeFirst " + dateTime2)

//                if (type == 1) {
//                    var cal = Calendar.getInstance()
//                    cal.time = Date()
//                    val formatter = SimpleDateFormat("dd MM, yyyy hh:mm:ss", Locale.getDefault())
//                    val date: Date = formatter.parse(dateTime2)
//                    println(date)
////                    Log.e("TAG", "dateFFF " + date.time)
//
//                    var diff = cal.timeInMillis - date.time
//
//                    val diffSeconds: Long = diff / 1000 % 60
//                    val diffMinutes: Long = diff / (60 * 1000) % 60
//                    val diffHours: Long = diff / (60 * 60 * 1000) % 24
//                    var dateTime =
//                        "" + (if (diffHours.toString().length == 1) "0" + diffHours else diffHours) + ":" + (if (diffMinutes.toString().length == 1) "0" + diffMinutes else diffMinutes) + ":" + (if (diffSeconds.toString().length == 1) "0" + diffSeconds else diffSeconds)
////                    Log.e("TAG", "differenceTime " + dateTime)
//                    binding.textNumberTime.text = dateTime
//                    binding.linearTime.visibility = View.VISIBLE
//                }
//
//                if (type == 2) {
//                    val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                    val datestart: Date = formatter.parse(createdAt)
//                    val dateend: Date = formatter.parse(endTime)
//
//                    val millse: Long = datestart.getTime() - dateend.getTime()
//                    val mills = abs(millse.toDouble()).toLong()
//                    val hours = (mills / (1000 * 60 * 60)).toInt()
//                    val mins = (mills / (1000 * 60)).toInt() % 60
//                    val secs = ((mills / 1000).toInt() % 60).toLong()
//
//                    var dateTime =
//                        "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
////                    Log.e("TAG", "differenceTime " + dateTime)
//
//                    binding.textNumberTime.text = dateTime
//                    binding.linearTime.visibility = View.VISIBLE
//                }
            } catch (e: Exception) {

            }

        }
    }


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
    fun checkInMethod() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            var latLong = LatLng(location!!.latitude, location.longitude)
            Log.e("TAG", "latLong1111 " + latLong)
            mainThread {
                binding.textCheckInAddr.text =
                    getString(R.string.geAddress) + " " + requireActivity().getAddress(latLong)

                readData(AUTH) { token ->
                    Log.e("TAG", "btCheckOut " + token)
                    val obj: JSONObject = JSONObject().apply {
                        put("message", "Arrived at work")
                        put("attendance_time_id", 1)
                        put("sick", 0)
                    }
                    viewModel.attendances(obj.getJsonRequestBody()) {
                        binding.textCheckIn.setText(this.created_at)
                        callAttendancesList()
                    }
                }
            }
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun checkOutMethod() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            var latLong = LatLng(location!!.latitude, location.longitude)
            Log.e("TAG", "latLong2222 " + latLong)
            mainThread {
                binding.textCheckOutAddr.text =
                    getString(R.string.geAddress) + " " + requireActivity().getAddress(latLong)

                readData(AUTH) { token ->
                    Log.e("TAG", "btCheckOut " + token)
                    val obj: JSONObject = JSONObject().apply {
                        put("message", "Leave at work")
                        put("attendance_time_id", 2)
                        put("sick", 0)
                    }
                    viewModel.attendances(obj.getJsonRequestBody()) {
                        binding.textCheckOut.setText(this.created_at)
                        callAttendancesList()
                    }
                }
            }
        }
    }


    private fun stopTimer() {
        if (timer != null) {
            timer?.cancel()
            timer?.purge()
        }
    }
}