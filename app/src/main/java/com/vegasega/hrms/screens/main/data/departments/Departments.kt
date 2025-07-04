package com.vegasega.hrms.screens.main.data.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ChangeMobileBinding
import com.vegasega.hrms.databinding.DepartmentsBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.screens.main.data.DataVM
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class Departments : Fragment() {
    private val viewModel: DataVM by viewModels()
    private var _binding: DepartmentsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DepartmentsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.departmentsData)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            readData(AUTH) { token ->
//                Log.e("TAG", "btCheckOut " + token)
                recyclerView.setHasFixedSize(true)
                binding.recyclerView.adapter = viewModel.departmentsAdapter
                viewModel.departmentsList() {
                    viewModel.departmentsAdapter.notifyDataSetChanged()
                    viewModel.departmentsAdapter.submitList(this.data)
                }

//                btCreate.singleClick {
//                    this.root.findNavController().navigate(R.id.action_employeesLeaveRequest_to_employeesLeaveRequestPost)
//                }

            }
        }

    }
}