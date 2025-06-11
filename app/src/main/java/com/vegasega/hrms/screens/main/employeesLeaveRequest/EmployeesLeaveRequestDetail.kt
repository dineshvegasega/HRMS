package com.vegasega.hrms.screens.main.employeesLeaveRequest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.EmployeesLeaveRequestDetailBinding
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
import org.json.JSONObject
import kotlin.getValue

@AndroidEntryPoint
class EmployeesLeaveRequestDetail : Fragment() {
    private val viewModel: EmployeesLeaveRequestVM by viewModels()
    private var _binding: EmployeesLeaveRequestDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EmployeesLeaveRequestDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.employeesLeaveRequestDetail)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE




            var _id = arguments?.getString("key").toString()
            viewModel.employeesLeaveRequestGet(_id) {
                Log.e("TAG", "onViewCreated: "+this.toString())
                var from = arguments?.getString("from").toString()
                if (from == "view"){
                    editTextEmployeeName.setText(this?.leave_request?.employee?.name ?: "")
                    editFromDateValue.setText(this?.leave_request?.from ?: "")
                    editToDateValue.setText(this?.leave_request?.to ?: "")
                    editMessage.setText(this?.leave_request?.message ?: "")
                    editStatus.setText(this?.leave_request?.status ?: "")
                    editCheckedBy.setText(this?.leave_request?.checked_by?.toString() ?: "")
                    editComment.setText(this?.leave_request?.comment ?: "")

                    editEmployeeSickLeaves.setText(""+this?.leave_info?.sick_leave_quota ?: "")
                    editLeaveDaysCount.setText(""+this?.leave_info?.used_leaves ?: "")
                    editEmployeeCasualLeaves.setText(""+this?.leave_info?.casual_leave_quota ?: "")
                    editEmployeeLOPLeaves.setText(""+this?.leave_info?.lop ?: "")

                    editTextEmployeeName.isEnabled = false
                    editFromDateValue.isEnabled = false
                    editToDateValue.isEnabled = false
                    editMessage.isEnabled = false
                    editStatus.isEnabled = false
                    editCheckedBy.isEnabled = false
                    editComment.isEnabled = false

                    editEmployeeSickLeaves.isEnabled = false
                    editLeaveDaysCount.isEnabled = false
                    editEmployeeCasualLeaves.isEnabled = false
                    editEmployeeLOPLeaves.isEnabled = false
                    btUpdate.visibility = View.GONE
                } else if (from == "edit"){
                    editFromDateValue.singleClick {
                        requireActivity().showDropDownDialog(type = 20){
                            binding.editFromDateValue.setText(name)
                            viewModel.from = title
                        }
                    }
                    editToDateValue.singleClick {
                        requireActivity().showDropDownDialog(type = 20){
                            binding.editToDateValue.setText(name)
                            viewModel.to = title
                        }
                    }

                    editTextEmployeeName.setText(this?.leave_request?.employee?.name ?: "")
                    editFromDateValue.setText(this?.leave_request?.from ?: "")
                    editToDateValue.setText(this?.leave_request?.to ?: "")
                    editMessage.setText(this?.leave_request?.message ?: "")
                    editStatus.setText(this?.leave_request?.status ?: "")
                    editCheckedBy.setText(this?.leave_request?.checked_by?.toString() ?: "")
                    editComment.setText(this?.leave_request?.comment ?: "")

                    editEmployeeSickLeaves.setText(""+this?.leave_info?.sick_leave_quota ?: "")
                    editLeaveDaysCount.setText(""+this?.leave_info?.used_leaves ?: "")
                    editEmployeeCasualLeaves.setText(""+this?.leave_info?.casual_leave_quota ?: "")
                    editEmployeeLOPLeaves.setText(""+this?.leave_info?.lop ?: "")

                    editTextEmployeeName.isEnabled = true
                    editFromDateValue.isEnabled = true
                    editToDateValue.isEnabled = true
                    editMessage.isEnabled = true
                    editStatus.isEnabled = true
                    editCheckedBy.isEnabled = true
                    editComment.isEnabled = true

                    editEmployeeSickLeaves.isEnabled = true
                    editLeaveDaysCount.isEnabled = true
                    editEmployeeCasualLeaves.isEnabled = true
                    editEmployeeLOPLeaves.isEnabled = true

                    editTextEmployeeName.visibility = View.GONE
                    editFromDateValue.visibility = View.VISIBLE
                    editToDateValue.visibility = View.VISIBLE
                    editMessage.visibility = View.VISIBLE

                    textStatusTxt.visibility = View.GONE
                    editStatus.visibility = View.GONE
                    textCheckedByTxt.visibility = View.GONE
                    editCheckedBy.visibility = View.GONE
                    textCommentTxt.visibility = View.GONE
                    editComment.visibility = View.GONE

                    textEmployeeSickLeavesTxt.visibility = View.GONE
                    editEmployeeSickLeaves.visibility = View.GONE

                    textLeaveDaysCountTxt.visibility = View.GONE
                    editLeaveDaysCount.visibility = View.GONE

                    textEmployeeLOPLeavesTxt.visibility = View.GONE
                    editEmployeeLOPLeaves.visibility = View.GONE

                    textEmployeeCasualLeavesTxt.visibility = View.GONE
                    editEmployeeCasualLeaves.visibility = View.GONE
                    btUpdate.visibility = View.VISIBLE

                    btUpdate.singleClick {
                        if (editFromDateValue.text.toString().isEmpty()){
                            showSnackBar("Select Leave From Date")
                        } else if (editToDateValue.text.toString().isEmpty()){
                            showSnackBar("Select Leave To Date")
                        }else if (editMessage.text.toString().isEmpty()){
                            showSnackBar("Enter Some Message")
                        } else {
//                            readData(PROFILE_DATA) { profile ->
//                                if (profile != null) {
//                                    val data = Gson().fromJson(profile, Profile::class.java)

                                    val obj: JSONObject = JSONObject().apply {
                                        put("type", "edit")
                                        put("from", editFromDateValue.text.toString())
                                        put("to", editToDateValue.text.toString())
                                        put("message", editMessage.text.toString())
                                    }
                                    if(networkFailed) {
                                        viewModel.employeesLeaveRequestEdit(_id, obj.getJsonRequestBody()){
                                            view.findNavController().navigateUp()
                                        }
                                    } else {
                                        requireContext().callNetworkDialog()
                                    }
//                                }
//                            }
                        }
                    }
                }

            }

        }
    }


    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        _binding = null
        super.onDestroyView()
    }
}