package com.vegasega.hrms.screens.main.profiles

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.vegasega.hrms.databinding.EmploymentDetailsBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.utils.getLocalTime
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class EmploymentDetails : Fragment() {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: EmploymentDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EmploymentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            readData(PROFILE_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    var data = Gson().fromJson(loginUser, Profile::class.java)

                    editOfficialEmailID.setText(data?.employee?.employee_detail?.email?: " ")
                    editDateOfJoining.setText( if (!data?.employee?.date_of_joining.isNullOrEmpty()) ""+data?.employee?.date_of_joining?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy") else " ")
                    editUanNumber.setText(data?.employee?.employee_detail?.uan_no?: " ")
                    editPreviousEmploymentDetails.setText(data?.employee?.employee_detail?.previous_employee_details?: " ")
                    editWorkExperience.setText(""+data?.employee?.employee_detail?.work_experience_in_years?: " ")
                    editStartContract.setText(if (!data?.employee?.contract_start_date.isNullOrEmpty()) data?.employee?.contract_start_date?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy") else " ")
                    editEndContract.setText(if (!data?.employee?.contract_end_date.isNullOrEmpty()) data?.employee?.contract_end_date?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy") else " ")
                    editDepartment.setText(data?.employee?.department?.name?: " ")
                    editPosition.setText(data?.employee?.position?.name?: " ")
                    editEmployeeType.setText(data?.employee?.employee_type?.name?: " ")
                    editShiftTime.setText(data?.employee?.shift_time?.name?: " ")
                    editWeekoff.setText(data?.employee?.week_off?.name?: " ")
                    editAttendanceTrackingType.setText(data?.employee?.attendance_tracking_type?.name?: " ")
                    ediWorkType.setText(data?.employee?.work_type?.name?: " ")
                    ediDivision.setText(data?.employee?.division?.name?: " ")
                }
            }


        }
    }
}