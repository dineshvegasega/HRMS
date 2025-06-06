package com.vegasega.hrms.screens.main.announcements

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.AnnouncementsDetailBinding
import com.vegasega.hrms.models.dashboard.Announcement
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.parcelable
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class AnnouncementsDetail : Fragment() {
    private val viewModel: AnnouncementsVM by viewModels()
    private var _binding: AnnouncementsDetailBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnnouncementsDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(3)
        binding.apply {
            val model = arguments?.parcelable<Announcement>("key")

            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.announcementDetail)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE



            editTextTitle.setText(model?.title ?: "")
            editDescription.setText(model?.description ?: "")
            editFor.setText(""+model?.department_id ?: "")

            editTextTitle.isEnabled = false
            editDescription.isEnabled = false
            editFor.isEnabled = false

        }
    }


    override fun onDestroyView() {
        MainActivity.mainActivity.get()?.callFragment(4)
        _binding = null
        super.onDestroyView()
    }
}