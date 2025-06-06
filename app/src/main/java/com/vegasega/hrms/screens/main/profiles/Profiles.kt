package com.vegasega.hrms.screens.main.profiles

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ProfilesBinding
import com.vegasega.hrms.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.Login
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.getCameraPath
import com.vegasega.hrms.utils.getMediaFilePathFor
import com.vegasega.hrms.utils.loadImage
import com.vegasega.hrms.utils.showDropDownDialog
import com.vegasega.hrms.utils.showOptions
import com.vegasega.hrms.utils.singleClick
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.IMAGE_URL
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class Profiles : Fragment() {
    private val viewModel: ProfilesVM by activityViewModels()
    private var _binding: ProfilesBinding? = null
    private val binding get() = _binding!!

//    companion object{
//        var callBackListener: CallBackListener? = null
//        var tabPosition = 0
//    }

    lateinit var adapter : ProfilePagerAdapter

    var data : Profile ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProfilesBinding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(1)
//        callBackListener = this

        binding.apply {
            inclideHeaderSearch.textHeaderTxt.text = getString(R.string.your_Profile)
            inclideHeaderSearch.editTextSearch.visibility = View.GONE

            inclideHeaderSearch.textHeaderEditTxt.visibility = View.GONE
            inclideHeaderSearch.textHeaderEditTxt.singleClick {
                inclideHeaderSearch.textHeaderEditTxt.visibility = View.INVISIBLE
                btSave.visibility = View.VISIBLE
                btCancel.visibility = View.VISIBLE
                viewModel.isEditable.value = true
            }

            btSave.singleClick {
//                checkValidationClick()
            }


            btCancel.singleClick {
                inclideHeaderSearch.textHeaderEditTxt.visibility = View.VISIBLE
                btSave.visibility = View.GONE
                btCancel.visibility = View.GONE
                viewModel.isEditable.value = false
            }

            inclideHeaderSearch.btNominee.singleClick {
                view.findNavController().navigate(R.id.action_profiles_to_nomineeDetails)
            }


            readData(PROFILE_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    data = Gson().fromJson(loginUser, Profile::class.java)

                    ivProfileImage.loadImage(type = 1, url = { IMAGE_URL+data?.employee?.employee_detail?.photo?: "" })

                    editTextName.setText(data?.employee?.employee_detail?.name?: "")
                    editEmail.setText(data?.employee?.employee_detail?.email?: "")
                    editMobile.setText(data?.employee?.employee_detail?.phone?: "")
                    editTextAddress.setText(data?.employee?.employee_detail?.address?: "")

                    includeProfileInfo.apply {
                        textStartDateValue.setText(data?.employee?.start_of_contract?: "")
                        textEndDateValue.setText(data?.employee?.end_of_contract?: "")

                        editDepartment.setText(data?.employee?.department?.name?: "")
                        editPosition.setText(data?.employee?.position?.name?: "")
                        editGender.setText(data?.employee?.employee_detail?.gender?: "")
                        editDateOfBirth.setText(data?.employee?.employee_detail?.date_of_birth?: "")

                        editFatherName.setText(data?.employee?.employee_detail?.father_name?: "")
                        editMotherName.setText(data?.employee?.employee_detail?.mother_name?: "")
                        editSpouseName.setText(data?.employee?.employee_detail?.spouse_name?: "")
                        editMarriageAnniversary.setText(data?.employee?.employee_detail?.marriage_anniversary?: "")

                        editIdentityNumber.setText(data?.employee?.employee_detail?.identity_number?: "")
                        editPanNumber.setText(data?.employee?.employee_detail?.pan_number?: "")
                        editUanNumber.setText(data?.employee?.employee_detail?.uan_no?: "")
                        editPermanentaddress.setText(data?.employee?.employee_detail?.permanent_address?: "")

                        editCountryofbirth.setText(data?.employee?.employee_detail?.country_of_birth?: "")
                        editBirthplace.setText(data?.employee?.employee_detail?.birth_place?: "")
                        editNationality.setText(data?.employee?.employee_detail?.nationality?: "")
                        editPersonalemail.setText(data?.employee?.employee_detail?.p_email?: "")

                        editBloodgroup.setText(data?.employee?.employee_detail?.blood_group?: "")
                        editEmergencymobile.setText(data?.employee?.employee_detail?.e_mobile?: "")
                        editLastEducation.setText(data?.employee?.employee_detail?.last_education?: "")
                        editGPA.setText(data?.employee?.employee_detail?.gpa?: "")
                        editWorkExperience.setText(""+data?.employee?.employee_detail?.work_experience_in_years?: "")
                    }








                    viewModel.isEditable.value = false
                    fieldsEdit()
                }
            }
        }
    }




    private fun fieldsEdit() {
        binding.apply {
            viewModel.isEditable.observe(viewLifecycleOwner, Observer {
                editTextName.isEnabled = it
                editEmail.isEnabled = it
                editMobile.isEnabled = it
                editTextAddress.isEnabled = it
                ivProfileImage.isEnabled = true
                btnProfileImage.isEnabled = true

                if (it == true){
                    btnProfileImage.visibility = View.VISIBLE
                    ivProfileImage.singleClick {
                        imagePosition = 1
                        callMediaPermissions()
                    }
                }
                lateinit var viewer: StfalconImageViewer<String>

                if (it == false){
                    ivProfileImage.loadImage(type = 1, url = { IMAGE_URL+data?.employee?.employee_detail?.photo?: "" })

                    btnProfileImage.visibility = View.GONE
                    ivProfileImage.singleClick {
                        viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(IMAGE_URL+data?.employee?.employee_detail?.photo?: "")) { view, image ->
                            Picasso.get().load(image).into(view)
                        }.withImageChangeListener {
                            viewer.updateTransitionImage(ivProfileImage)
                        }
                            .withBackgroundColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color._D9000000
                                )
                            )
                            .show()
                    }
                }




                includeProfileInfo.apply {
                    textStartDateValue.isEnabled = it
                    textEndDateValue.isEnabled = it

                    editDepartment.isEnabled = it
                    editPosition.isEnabled = it
                    editGender.isEnabled = it
                    editDateOfBirth.isEnabled = it

                    editFatherName.isEnabled = it
                    editMotherName.isEnabled = it
                    editSpouseName.isEnabled = it
                    editMarriageAnniversary.isEnabled = it

                    editIdentityNumber.isEnabled = it
                    editPanNumber.isEnabled = it
                    editUanNumber.isEnabled = it
                    editPermanentaddress.isEnabled = it

                    editCountryofbirth.isEnabled = it
                    editBirthplace.isEnabled = it
                    editNationality.isEnabled = it
                    editPersonalemail.isEnabled = it

                    editBloodgroup.isEnabled = it
                    editEmergencymobile.isEnabled = it
                    editLastEducation.isEnabled = it
                    editGPA.isEnabled = it
                    editWorkExperience.isEnabled = it
                }

            })
        }
    }






    var imagePosition = 0
    @SuppressLint("MissingPermission")
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            lifecycleScope.launch {
                if (uri != null) {
                    when (imagePosition) {
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.profile_image_name = compressedImageFile.path
//                            binding.textViewPassportSizeImage.setText(File(viewModel.data.profile_image_name!!).name)
                            binding.ivProfileImage.loadImage(type = 1, url = { viewModel.data.profile_image_name!! })

                        }
                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
                            viewModel.data.aadhar_card_doc = compressedImageFile.path
//                            binding.textViewAadhaarImage.setText(File(viewModel.data.aadhar_card_doc!!).name)
                        }
                    }
                }
            }
        }


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch {
            if (uri == true) {

                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
                        viewModel.data.profile_image_name = compressedImageFile.path
//                        binding.textViewPassportSizeImage.setText(File(viewModel.data.profile_image_name!!).name)
                        binding.ivProfileImage.loadImage(type = 1, url = { viewModel.data.profile_image_name!! })
                    }
                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )

                        viewModel.data.aadhar_card_doc = compressedImageFile.path
//                        binding.textViewAadhaarImage.setText(compressedImageFile.name)
                    }
                }
            }
        }
    }


    private fun callMediaPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }


    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                requireActivity().showOptions {
                    when (this) {
                        1 -> forCamera()
                        2 -> forGallery()
                    }
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
    }




    private fun forCamera() {
        requireActivity().getCameraPath {
            uriReal = this
            captureMedia.launch(uriReal)
        }
    }

    private fun forGallery() {
        requireActivity().runOnUiThread() {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }



}