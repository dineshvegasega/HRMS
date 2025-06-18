package com.vegasega.hrms.screens.main.profiles

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.PersonalDetailsBinding
import com.vegasega.hrms.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.Login
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.*
import com.vegasega.hrms.screens.interfaces.CallBackListener
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.hrms.screens.mainActivity.MainActivityVM.Companion.locale
import com.vegasega.hrms.utils.callNetworkDialog
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.getCameraPath
import com.vegasega.hrms.utils.getLocalTime
import com.vegasega.hrms.utils.getMediaFilePathFor
import com.vegasega.hrms.utils.isNetworkAvailable
import com.vegasega.hrms.utils.loadImage
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.showDropDownDialog
import com.vegasega.hrms.utils.showOptions
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@AndroidEntryPoint
class PersonalDetails : Fragment() , CallBackListener {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: PersonalDetailsBinding? = null
    private val binding get() = _binding!!


    companion object{
        var callBackListener: CallBackListener? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PersonalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        callBackListener = this

        binding.apply {

            readData(PROFILE_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    var data = Gson().fromJson(loginUser, Profile::class.java)

                    editDateOfBirth.setText(""+data?.employee?.employee_detail?.date_of_birth?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy")).toString()

                    editFatherName.setText(data?.employee?.employee_detail?.father_name?: " ")
                    editMotherName.setText(data?.employee?.employee_detail?.mother_name?: " ")
                    editSpouseName.setText(data?.employee?.employee_detail?.spouse_name?: " ")


                    editGender.setText(when(data?.employee?.employee_detail?.gender){
                        "M" -> "Male"
                        "F" -> "Female"
                        else -> "Other"
                    })

                    editMaritalStatus.setText(if (data?.employee?.employee_detail?.marriage_anniversary.isNullOrEmpty())  "Single" else "Married")

                    editMaritalStatusSince.setText(""+data?.employee?.employee_detail?.marriage_anniversary?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy")).toString()

                    editBloodgroup.setText(data?.employee?.employee_detail?.blood_group?: " ")
                    editMobile.setText(data?.employee?.employee_detail?.phone?: " ")
                    editEmergencymobile.setText(data?.employee?.employee_detail?.e_mobile?: " ")
                    editPersonalEmailID.setText(data?.employee?.employee_detail?.p_email?: " ")
                    editIdentityNumber.setText(data?.employee?.employee_detail?.identity_number?: " ")
                    editPanNumber.setText(data?.employee?.employee_detail?.pan_number?: " ")
                    editCountryofbirth.setText(data?.employee?.employee_detail?.country_of_birth?: " ")
                    editBirthplace.setText(data?.employee?.employee_detail?.birth_place?: " ")
                    editNationality.setText(data?.employee?.employee_detail?.nationality?: " ")
                    editTextAddress.setText(data?.employee?.employee_detail?.address?: " ")
                    editTextCity.setText(data?.employee?.employee_detail?.city?: " ")
                    editTextState.setText(data?.employee?.employee_detail?.state?: " ")
                    editTextPinCode.setText(data?.employee?.employee_detail?.pincode?: " ")
                    editTextCountry.setText(data?.employee?.employee_detail?.country_of_birth?: " ")
                    editPermanentaddress.setText(data?.employee?.employee_detail?.permanent_address?: " ")
                    editLastEducation.setText(data?.employee?.employee_detail?.last_education?: " ")

                }
            }


        }
    }

    override fun onCallBack(pos: Int) {
    }


}
