package com.vegasega.hrms.screens.main.profiles

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.vegasega.hrms.databinding.EmployeeConfigurationBinding
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class EmployeeConfiguration : Fragment() {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: EmployeeConfigurationBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmployeeConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
//        callBackListener = this

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = "Employee Configuration"
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE
        }
    }
}