package com.vegasega.hrms.screens.main.policies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.PoliciesBinding
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class PoliciesDetail : Fragment() {
    private val viewModel: PoliciesVM by viewModels()
    private var _binding: PoliciesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PoliciesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.policy_detail)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            var _id = arguments?.getString("key").toString()
//            viewModel.employeesLeaveRequestGet(_id) {
//
//            }

        }
    }



    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        _binding = null
        super.onDestroyView()
    }

}