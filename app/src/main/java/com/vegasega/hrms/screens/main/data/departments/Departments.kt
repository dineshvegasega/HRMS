package com.vegasega.hrms.screens.main.data.departments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ChangeMobileBinding
import com.vegasega.hrms.databinding.DepartmentsBinding
import com.vegasega.hrms.screens.main.data.DataVM
import com.vegasega.hrms.screens.mainActivity.MainActivity
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

//            loadFirstPage()
//            recyclerView.setHasFixedSize(true)
//            binding.recyclerView.adapter = viewModel.adapter
//            binding.recyclerView.itemAnimator = DefaultItemAnimator()
//
//            observerDataRequest()
//
//            recyclerViewScroll()
//
//            searchHandler()
        }

    }
}