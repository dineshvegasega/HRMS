package com.vegasega.hrms.screens.onboarding.walkThrough

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import com.vegasega.hrms.networking.Repository
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.OnboardItemBinding
import com.vegasega.hrms.databinding.WalkThroughItemBinding
import com.vegasega.hrms.genericAdapter.GenericAdapter
import com.vegasega.hrms.screens.onboarding.onboard.Onboard
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WalkThroughVM @Inject constructor(private val repository: Repository): ViewModel() {

    var itemMain : ArrayList<WalkThrough.Item> ?= ArrayList()

    init {
        itemMain?.add(WalkThrough.Item(MainActivity.context.get()!!.getString(R.string.LearnAboutSchemes), MainActivity.context.get()!!.getString(R.string.desc1),R.drawable.walk1))
        itemMain?.add(WalkThrough.Item(MainActivity.context.get()!!.getString(R.string.ComplaintRedressal), MainActivity.context.get()!!.getString(R.string.desc2),R.drawable.walk2))
        itemMain?.add(WalkThrough.Item(MainActivity.context.get()!!.getString(R.string.KnowYourRights), MainActivity.context.get()!!.getString(R.string.desc3),R.drawable.walk3))
    }

    val photosAdapter = object : GenericAdapter<WalkThroughItemBinding, Onboard.Item>() {
        override fun onCreateView(
            inflater: LayoutInflater,
            parent: ViewGroup,
            viewType: Int
        ) = OnboardItemBinding.inflate(inflater, parent, false)

        override fun onBindHolder(binding: WalkThroughItemBinding, dataClass: Onboard.Item, position: Int) {
//            binding.textHeaderadfdsfTxt3.text = dataClass.name
//            Picasso.get().load(
//                dataClass.image
//            ).into(binding!!.imageLogo)
//            binding.root.singleClick {
//                when(position) {
//                    0 -> it.findNavController().navigate(R.id.action_onboard_to_quickRegistration)
//                    1 -> it.findNavController().navigate(R.id.action_onboard_to_loginPassword)
//                    2 -> it.findNavController().navigate(R.id.action_onboard_to_loginOtp)
//                    3 -> it.findNavController().navigate(R.id.action_onboard_to_completeRegistration)
//                    4 -> it.findNavController().navigate(R.id.action_onboard_to_completeRegistration)
//
//                }
//            }
        }
    }

}