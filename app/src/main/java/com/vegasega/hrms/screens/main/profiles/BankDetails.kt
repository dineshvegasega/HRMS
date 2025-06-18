package com.vegasega.hrms.screens.main.profiles

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.Gson
import com.vegasega.hrms.databinding.BankDetailsBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.utils.getLocalTime
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class BankDetails : Fragment() {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: BankDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BankDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            readData(PROFILE_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    var data = Gson().fromJson(loginUser, Profile::class.java)

                    editBankName.setText(data?.employee?.employee_detail?.bank_name?: " ")
                    editAccountHolderName.setText(data?.employee?.employee_detail?.account_holder_name?: " ")
                    editBankAccountNumber.setText(data?.employee?.employee_detail?.bank_account_number?: " ")
                    editIFSCCode.setText(data?.employee?.employee_detail?.ifsc_code?: " ")
                    editBranchNameAddress.setText(data?.employee?.employee_detail?.branch_name_address?: " ")
                    editAccountType.setText(data?.employee?.employee_detail?.account_type?: " ")

                }
            }


        }

    }
}