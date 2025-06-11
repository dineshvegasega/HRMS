package com.vegasega.hrms.screens.main.employeesPerformanceScore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.EmployeesPerformanceScoreBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class EmployeesPerformanceScore : Fragment() {
    private val viewModel: EmployeesPerformanceScoreVM by viewModels()
    private var _binding: EmployeesPerformanceScoreBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmployeesPerformanceScoreBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.employeesPerformanceScore)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            readData(AUTH) { token ->
//                Log.e("TAG", "btCheckOut " + token)
                recyclerView.setHasFixedSize(true)
                binding.recyclerView.adapter = viewModel.employeesPerformanceScoreAdapter
                viewModel.employeesPerformanceScoreList() {
                    viewModel.employeesPerformanceScoreAdapter.notifyDataSetChanged()
                    viewModel.employeesPerformanceScoreAdapter.submitList(this.data)
                }
            }


        }
    }
}