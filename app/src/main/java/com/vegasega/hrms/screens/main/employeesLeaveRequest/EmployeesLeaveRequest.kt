package com.vegasega.hrms.screens.main.employeesLeaveRequest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.EmployeesLeaveRequestBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class EmployeesLeaveRequest : Fragment() {
    private val viewModel: EmployeesLeaveRequestVM by viewModels()
    private var _binding: EmployeesLeaveRequestBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = EmployeesLeaveRequestBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.employeesLeaveRequest)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            readData(AUTH) { token ->
//                Log.e("TAG", "btCheckOut " + token)
                recyclerView.setHasFixedSize(true)
                binding.recyclerView.adapter = viewModel.employeesLeaveRequestAdapter
                viewModel.employeesLeaveRequestList() {
                    viewModel.employeesLeaveRequestAdapter.notifyDataSetChanged()
                    viewModel.employeesLeaveRequestAdapter.submitList(this.data)
                }

                btCreate.singleClick {
                    this.root.findNavController().navigate(R.id.action_employeesLeaveRequest_to_employeesLeaveRequestPost)
                }

            }

        }
    }
}