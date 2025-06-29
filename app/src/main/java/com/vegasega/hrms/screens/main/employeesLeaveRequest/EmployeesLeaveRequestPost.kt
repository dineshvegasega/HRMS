package com.vegasega.hrms.screens.main.employeesLeaveRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.EmployeesLeaveRequestPostBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.email
import com.vegasega.hrms.networking.getJsonRequestBody
import com.vegasega.hrms.networking.password
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.hrms.utils.callNetworkDialog
import com.vegasega.hrms.utils.showDropDownDialog
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import org.json.JSONObject
import kotlin.getValue

@AndroidEntryPoint
class EmployeesLeaveRequestPost : Fragment() {
    private val viewModel: EmployeesLeaveRequestVM by viewModels()
    private var _binding: EmployeesLeaveRequestPostBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EmployeesLeaveRequestPostBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.createANewEmployeeLeaveRequest)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            radioGroupLeaveType.setOnCheckedChangeListener(object :
                RadioGroup.OnCheckedChangeListener {
                override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                    when (checkedId) {
                        radioButtonSickLeave.id -> viewModel.leaveType = radioButtonSickLeave.text.toString()
                        radioButtonCasualLeave.id -> viewModel.leaveType = radioButtonCasualLeave.text.toString()
                        radioButtonLOP.id -> viewModel.leaveType = radioButtonLOP.text.toString()
                    }
                }
            })



            editTextFrom.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    binding.editTextFrom.setText(name)
                    viewModel.from = title
                }
            }
            editTextTo.singleClick {
                requireActivity().showDropDownDialog(type = 20){
                    binding.editTextTo.setText(name)
                    viewModel.to = title
                }
            }


            btSubmit.singleClick {
                if (editTextFrom.text.toString().isEmpty()){
                    showSnackBar("Select Leave From Date")
                } else if (editTextTo.text.toString().isEmpty()){
                    showSnackBar("Select Leave To Date")
                }else if (editTextMessage.text.toString().isEmpty()){
                    showSnackBar("Enter Some Message")
                } else {
                    readData(PROFILE_DATA) { profile ->
                        if (profile != null) {
                            val data = Gson().fromJson(profile, Profile::class.java)

                            val obj: JSONObject = JSONObject().apply {
                                put("employee_id", ""+data.employee.id)
                                put("from", editTextFrom.text.toString())
                                put("to", editTextTo.text.toString())
                                put("message", editTextMessage.text.toString())
                            }
                            if(networkFailed) {
                                viewModel.employeesLeaveRequestPost(obj.getJsonRequestBody()){
                                    view.findNavController().navigateUp()
                                }
                            } else {
                                requireContext().callNetworkDialog()
                            }
                        }
                    }

                }
            }
        }
    }
}