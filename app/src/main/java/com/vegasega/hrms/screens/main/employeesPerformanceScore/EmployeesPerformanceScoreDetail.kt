package com.vegasega.hrms.screens.main.employeesPerformanceScore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.EmployeesPerformanceScoreBinding
import com.vegasega.hrms.databinding.EmployeesPerformanceScoreDetailBinding
import com.vegasega.hrms.models.dashboard.Announcement
import com.vegasega.hrms.models.score.Data
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class EmployeesPerformanceScoreDetail : Fragment() {
    private val viewModel: EmployeesPerformanceScoreVM by viewModels()
    private var _binding: EmployeesPerformanceScoreDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EmployeesPerformanceScoreDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.employeesPerformanceScore)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE


            val model = arguments?.parcelable<Data>("key")


            editTextTitle.setText(model?.employee?.name  ?: "")
            editScoredBy.setText(model?.scored_by?.name ?: "")
            editDate.setText(""+model?.created_at ?: "")
            editDiscipline.setText(""+model?.score  ?: "")
            editHardworking.setText(""+model?.scored_by?.department_id+ "")

            editTextTitle.isEnabled = false
            editScoredBy.isEnabled = false
            editDate.isEnabled = false
            editDiscipline.isEnabled = false
            editHardworking.isEnabled = false
        }
    }

    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        _binding = null
        super.onDestroyView()
    }
}