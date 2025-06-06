package com.vegasega.hrms.screens.main.employeesLeaveRequest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.EmployeesLeaveRequestBinding
import com.vegasega.hrms.databinding.EmployeesLeaveRequestDetailBinding
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
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


        }
    }


    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        _binding = null
        super.onDestroyView()
    }
}