package com.vegasega.hrms.screens.main.accounts.roles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.RolesBinding
import com.vegasega.hrms.screens.main.accounts.AccountsVM
import com.vegasega.hrms.screens.mainActivity.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class Roles : Fragment() {
    private val viewModel: AccountsVM by viewModels()
    private var _binding: RolesBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RolesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.roles)
            idDataNotFound.textDesc.text = getString(R.string.currently_no_schemes)
        }
    }
}