package com.vegasega.hrms.screens.main.announcements

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.AnnouncementsBinding
import com.vegasega.hrms.databinding.ChangeMobileBinding
import com.vegasega.hrms.datastore.DataStoreKeys.AUTH
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class Announcements : Fragment() {
    private val viewModel: AnnouncementsVM by viewModels()
    private var _binding: AnnouncementsBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AnnouncementsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.announcements)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)

            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            binding.recyclerViewAnnouncements.setHasFixedSize(true)
            binding.recyclerViewAnnouncements.adapter = viewModel.announcementsAdapter


            viewModel.itemDashboardLive.observe(viewLifecycleOwner, Observer {
                viewModel.announcementsAdapter.notifyDataSetChanged()
                viewModel.announcementsAdapter.submitList(it.announcements)
//                if(it.announcements.size > 0){
//                    binding.layoutAnnouncements.visibility = View.VISIBLE
//                } else {
//                    binding.layoutAnnouncements.visibility = View.GONE
//                }
            })

            readData(AUTH) { token ->
                Log.e("TAG", "btCheckOut " + token)
                viewModel.dashboard(view = requireView()){


                }
            }
        }
    }
}