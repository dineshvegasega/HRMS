package com.vegasega.hrms.screens.main.attendances

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.AttendancesBinding
import com.vegasega.hrms.databinding.ChangeMobileBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.attendance.Attendance
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.Locale
import kotlin.getValue
import kotlin.math.abs

@AndroidEntryPoint
class Attendances : Fragment() {
    private val viewModel: AttendancesVM by viewModels()
    private var _binding: AttendancesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AttendancesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.attendances)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            readData(AUTH) { token ->
//                Log.e("TAG", "btCheckOut " + token)
                recyclerView.setHasFixedSize(true)
                binding.recyclerView.adapter = viewModel.attendanceAdapter
                viewModel.attendancesList() {
                    viewModel.attendanceAdapter.notifyDataSetChanged()
                    viewModel.attendanceAdapter.submitList(this.attendances.data)

                    val sdf = SimpleDateFormat("dd-MM-yyyy")
                    val currentDate = sdf.format(Date())

                    var allList = this.attendances.data
                    Collections.reverse(allList)

//                    val dataAttendanceIn : ArrayList<Attendance> = ArrayList()
//                    val dataAttendanceOut : ArrayList<Attendance> = ArrayList()

                    val breakIn : ArrayList<Attendance> = ArrayList()
                    val breakOut : ArrayList<Attendance> = ArrayList()


                    this.attendances.data.forEach {
                        if (it.created_at.contains(currentDate)) {
                            Log.e("TAG", "attendancesid " + it.id)

//                            if (it.attendance_time_id == 1) {
//                                dataAttendanceIn.add(it)
//                            }
//
//                            if (it.attendance_time_id == 2) {
//                                dataAttendanceOut.add(it)
//                            }

                            if (it.attendance_time_id == 3) {
                                breakIn.add(it)
                            }

                            if (it.attendance_time_id == 4) {
                                breakOut.add(it)
                            }
                        }
                    }

//                    var first = dataAttendanceIn.first()
//                    var last = dataAttendanceOut.last()
//
//                    Log.e("TAG", "attendancesfirst " + first.toString())
//                    Log.e("TAG", "attendanceslast " + last.toString())
//                    if (dataAttendanceIn.size > dataAttendanceOut.size){
//                        var millsTotal : Long = 0
//                        var counter = 0
//                        dataAttendanceOut.forEach {
//                            var timeIn = dataAttendanceIn[counter].created_at
//                            var timeOut = dataAttendanceOut[counter].created_at
//                            Log.e("TAG", "timeIn " + timeIn)
//                            Log.e("TAG", "timeOut " + timeOut)
//
//                            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                            val datestart: Date = formatter.parse(timeIn)
//                            val dateend: Date = formatter.parse(timeOut)
//
//                            val millse: Long = datestart.getTime() - dateend.getTime()
//
//                            millsTotal = millsTotal + millse
//
//                            counter = counter + 1
//                        }
//
//                        var timeIn = dataAttendanceIn[dataAttendanceIn.size - 1].created_at
//                        Log.e("TAG", "timeIntimeIn " + timeIn)
//                        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                        val datestart: Date = formatter.parse(timeIn)
//
//                        var cal = Calendar.getInstance()
//                        cal.time = Date()
//
//                        var diff = cal.timeInMillis - datestart.time
//
//
//                        Log.e("TAG", "diffdiffdiffdiffBBB " + diff)
//
//
//                        val millsTotalTime = abs(millsTotal.toDouble()).toLong()
//                        Log.e("TAG", "diffdiffdiffdiffAAA " + millsTotalTime)
//                        var mills = millsTotalTime + diff
//                        val hours = (mills / (1000 * 60 * 60)).toInt()
//                        val mins = (mills / (1000 * 60)).toInt() % 60
//                        val secs = ((mills / 1000).toInt() % 60).toLong()
//
//                        var dateTime =
//                            "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
//                        Log.e("TAG", "differenceTime " + dateTime)
//                    } else {
//                        var millsTotal : Long = 0
//                        var counter = 0
//                        dataAttendanceIn.forEach {
//                            var timeIn = it.created_at
//                            var timeOut = dataAttendanceOut[counter].created_at
//                            Log.e("TAG", "timeIn " + timeIn)
//                            Log.e("TAG", "timeOut " + timeOut)
//
//                            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
//                            val datestart: Date = formatter.parse(timeIn)
//                            val dateend: Date = formatter.parse(timeOut)
//
//                            val millse: Long = datestart.getTime() - dateend.getTime()
//
//                            millsTotal = millsTotal + millse
//
//                            counter = counter + 1
//                        }
//
//
//                        val mills = abs(millsTotal.toDouble()).toLong()
//                        val hours = (mills / (1000 * 60 * 60)).toInt()
//                        val mins = (mills / (1000 * 60)).toInt() % 60
//                        val secs = ((mills / 1000).toInt() % 60).toLong()
//
//                        var dateTime =
//                            "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
//                        Log.e("TAG", "differenceTime " + dateTime)
//                    }

                    Log.e("TAG", "breakIn " + breakIn.size)
                    Log.e("TAG", "breakOut " + breakOut.size)


                    if (breakIn.size > 0){
                        var firstIn = breakIn.first()

                        Log.e("TAG", "attendancesfirst " + firstIn.toString())

//                        var lastOut = breakOut.last()
//                        Log.e("TAG", "attendanceslast " + lastOut.toString())


                        if (breakIn.size > breakOut.size){
                            var millsTotal : Long = 0
                            var counter = 0
                            breakIn.forEach {
                                var timeIn = breakIn[counter].created_at

                                if (breakOut.size != 0){
                                    try {
                                        var timeOut = breakOut[counter].created_at
                                        Log.e("TAG", "timeIn " + timeIn)
                                        Log.e("TAG", "timeOut " + timeOut)

                                        val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                                        val datestart: Date = formatter.parse(timeIn)
                                        val dateend: Date = formatter.parse(timeOut)

                                        val millse: Long = datestart.getTime() - dateend.getTime()

                                        millsTotal = millsTotal + millse
                                    } catch (e : Exception){

                                    }
                                    counter = counter + 1
                                } else {

                                }


                            }

                            var timeIn = breakIn[breakIn.size - 1].created_at
                            Log.e("TAG", "timeIntimeIn " + timeIn)
                            val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                            val datestart: Date = formatter.parse(timeIn)

                            var cal = Calendar.getInstance()
                            cal.time = Date()

                            var diff = cal.timeInMillis - datestart.time


                            Log.e("TAG", "diffdiffdiffdiffBBB " + diff)


                            val millsTotalTime = abs(millsTotal.toDouble()).toLong()
                            Log.e("TAG", "diffdiffdiffdiffAAA " + millsTotalTime)
                            var mills = millsTotalTime + diff
                            val hours = (mills / (1000 * 60 * 60)).toInt()
                            val mins = (mills / (1000 * 60)).toInt() % 60
                            val secs = ((mills / 1000).toInt() % 60).toLong()

                            var dateTime =
                                "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
                            Log.e("TAG", "differenceTimeA " + dateTime)
                        } else {
                            var millsTotal : Long = 0
                            var counter = 0
                            breakIn.forEach {
                                var timeIn = it.created_at
                                var timeOut = breakOut[counter].created_at
                                Log.e("TAG", "timeIn " + timeIn)
                                Log.e("TAG", "timeOut " + timeOut)

                                val formatter = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
                                val datestart: Date = formatter.parse(timeIn)
                                val dateend: Date = formatter.parse(timeOut)

                                val millse: Long = datestart.getTime() - dateend.getTime()

                                millsTotal = millsTotal + millse

                                counter = counter + 1
                            }


                            val mills = abs(millsTotal.toDouble()).toLong()
                            val hours = (mills / (1000 * 60 * 60)).toInt()
                            val mins = (mills / (1000 * 60)).toInt() % 60
                            val secs = ((mills / 1000).toInt() % 60).toLong()

                            var dateTime =
                                "" + (if (hours.toString().length == 1) "0" + hours else hours) + ":" + (if (mins.toString().length == 1) "0" + mins else mins) + ":" + (if (secs.toString().length == 1) "0" + secs else secs)
                            Log.e("TAG", "differenceTimeB " + dateTime)
                        }
                    }

                }
            }
        }
    }
}