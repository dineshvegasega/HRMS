package com.vegasega.hrms.screens.main.profiles

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ProfilesBinding
import com.vegasega.hrms.datastore.DataStoreKeys.PROFILE_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.profile.Profile
import com.vegasega.hrms.networking.IMAGE_URL
import com.vegasega.hrms.screens.main.subscription.SubscriptionHistory
import com.vegasega.hrms.screens.main.subscription.SubscriptionPagerAdapter
import com.vegasega.hrms.screens.main.subscription.ViewManage
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.changeDateFormat
import com.vegasega.hrms.utils.getCameraPath
import com.vegasega.hrms.utils.getLocalTime
import com.vegasega.hrms.utils.getMediaFilePathFor
import com.vegasega.hrms.utils.loadImage
import com.vegasega.hrms.utils.showOptions
import com.vegasega.hrms.utils.singleClick
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
        savedInstanceState: Bundle?,
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
            inclideHeaderSearch.textHeaderTxt.text = "Personal Details"
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

            adapter= ProfilePagerAdapter(requireActivity())
            adapter.notifyDataSetChanged()
            introViewPager.setUserInputEnabled(false);
            adapter.addFragment(PersonalDetails())
            adapter.addFragment(EmploymentDetails())
            adapter.addFragment(BankDetails())
            Handler(Looper.getMainLooper()).postDelayed({
                introViewPager.adapter = adapter
                val array = listOf<String>("Personal Details", "Employment Details", "Bank Details")
                TabLayoutMediator(tabLayout, introViewPager) { tab, position ->
                    tab.text = array[position]
                    //setTabStyle(tabLayout, array[position])
                }.attach()
            }, 100)


//            layoutOfficialEmploymentDetails.setOnClickListener {
////                val lastChild: View = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1)
////                val bottom: Int = lastChild.getBottom() + nestedScrollView.getPaddingBottom()
////                val sy: Int = nestedScrollView.getScrollY()
////                val sh: Int = nestedScrollView.getHeight()
////                val delta = bottom - (sy + sh)
////
////                nestedScrollView.smoothScrollBy(0, delta)
//
//                if (officialEmploymentDetails.root.isVisible == true) {
//                    officialEmploymentDetails.root.visibility = View.GONE
//                    ivHideShow.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.arrow_right
//                        )
//                    )
//                } else {
//                    officialEmploymentDetails.root.visibility = View.VISIBLE
//                    ivHideShow.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.ic_arrow_down
//                        )
//                    )
//                }
//
//
////                val scrollTo: Int =
////                    (officialEmploymentDetails.root.getParent().getParent() as View).getTop() + officialEmploymentDetails.root.bottom
////                nestedScrollView.smoothScrollTo(0, scrollTo)
//
//
//
//
//                Handler(Looper.getMainLooper()).postDelayed({
//                    nestedScrollView.fullScroll(View.FOCUS_DOWN)
//               }, 100)
//            }
//
//
//            layoutBankDetails.setOnClickListener {
////                val lastChild: View = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1)
////                val bottom: Int = lastChild.getBottom() + nestedScrollView.getPaddingBottom()
////                val sy: Int = nestedScrollView.getScrollY()
////                val sh: Int = nestedScrollView.getHeight()
////                val delta = bottom - (sy + sh)
////
////                nestedScrollView.smoothScrollBy(0, delta)
//
//
//
//
//                if (bankDetails.root.isVisible == true) {
//                    bankDetails.root.visibility = View.GONE
//                    ivHideShowBank.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.arrow_right
//                        )
//                    )
//                } else {
////                    val lastChild: View = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1)
////                    val bottom: Int = lastChild.getBottom() + nestedScrollView.getPaddingBottom()
////                    val sy: Int = nestedScrollView.getScrollY()
////                    val sh: Int = nestedScrollView.getHeight()
////                    val delta = bottom - (sy + sh)
////
////                    nestedScrollView.smoothScrollBy(0, delta)
//
//                    bankDetails.root.visibility = View.VISIBLE
//                    ivHideShowBank.setImageDrawable(
//                        ContextCompat.getDrawable(
//                            root.context,
//                            R.drawable.ic_arrow_down
//                        )
//                    )
//                }
//
//
////                val scrollUp: Int =
////                    (nestedScrollView.getParent().getParent() as View).bottom
////
////                val scrollDown: Int =
////                    (bankDetails.root.getParent().getParent() as View).height
////                nestedScrollView.smoothScrollTo(0, scrollTo)
//
//                Handler(Looper.getMainLooper()).postDelayed({
////                    nestedScrollView.smoothScrollTo(bankDetails.root.height, (nestedScrollView.height - bankDetails.root.height))
//                    nestedScrollView.fullScroll(View.FOCUS_DOWN)
////                    nestedScrollView.pageScroll(View.FOCUS_DOWN);
////                    nestedScrollView.fling(View.FOCUS_DOWN);
////                    nestedScrollView.smoothScrollTo(0, nestedScrollView.bottom)
////                    nestedScrollView.fling(0);
////                    nestedScrollView.smoothScrollTo(0, 500);
//
////                    nestedScrollView.scrollBy(view.bottom, 0);
////                    ObjectAnimator.ofInt(nestedScrollView, "scrollY",  view.bottom).setDuration(700).start();
//
////                    nestedScrollView.fling(0)
////                    nestedScrollView.fullScroll(NestedScrollView.FOCUS_UP)
////                    nestedScrollView.smoothScrollTo(0, NestedScrollView.FOCUS_UP);
//                }, 100)
//
//            }




            readData(PROFILE_DATA) { loginUser ->
                if (loginUser != null) {
//                    Log.e("TAG", "loginUser "+loginUser)
                    data = Gson().fromJson(loginUser, Profile::class.java)

                    ivProfileImage.loadImage(type = 1, url = { IMAGE_URL+data?.employee?.employee_detail?.photo?: "" })

                    textName.setText(data?.employee?.employee_detail?.name?: "")
//                    data?.employee?.employee_detail?.date_of_birth?.let {
//                        editDateOfBirth.text = ""+it.changeDateFormat("yyyy-MM-dd", "dd MMM, yyyy")
//                    }


//                    var dob = data?.employee?.employee_detail?.date_of_birth?.replace(".000000Z", "")
//                    dob?.replace(data?.employee?.employee_detail?.date_of_birth, "" )
//
//                    var dob = data?.employee?.employee_detail?.date_of_birth?.replace(".000000Z", "")(getLocalTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM, yyyy"), "").toString()
//
//                    editDateOfBirth.setText(""+data?.employee?.employee_detail?.date_of_birth?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy")).toString()
//
////                    editDateOfBirth.setText(data?.employee?.employee_detail?.date_of_birth?: "")
//                    editFatherName.setText(data?.employee?.employee_detail?.father_name?: "")
//                    editMotherName.setText(data?.employee?.employee_detail?.mother_name?: "")
//                    editSpouseName.setText(data?.employee?.employee_detail?.spouse_name?: "")
//                    editGender.setText(data?.employee?.employee_detail?.gender?: "")
//
//                    editMaritalStatus.setText(if (data?.employee?.employee_detail?.marriage_anniversary.isNullOrEmpty())  "Single" else "Married")
////                    editMaritalStatus.setText(data?.employee?.employee_detail?.marriage_anniversary?: "Single")
//
//                    editMaritalStatusSince.setText(""+data?.employee?.employee_detail?.marriage_anniversary?.replace(".000000Z", "")?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss", "dd MMM, yyyy")).toString()
//
////                    editMaritalStatusSince.setText(""+data?.employee?.employee_detail?.marriage_anniversary?.getLocalTime("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM, yyyy").toString())
//
////                    editMaritalStatusSince.setText(data?.employee?.employee_detail?.marriage_anniversary?: "")
//                    editBloodgroup.setText(data?.employee?.employee_detail?.blood_group?: "NA")
//                    editMobile.setText(data?.employee?.employee_detail?.phone?: "")
//                    editEmergencymobile.setText(data?.employee?.employee_detail?.e_mobile?: "")
//                    editIdentityNumber.setText(data?.employee?.employee_detail?.identity_number?: "")
//                    editPanNumber.setText(data?.employee?.employee_detail?.pan_number?: "")
//                    editCountryofbirth.setText(data?.employee?.employee_detail?.country_of_birth?: "")
//                    editBirthplace.setText(data?.employee?.employee_detail?.birth_place?: "")
//                    editNationality.setText(data?.employee?.employee_detail?.nationality?: "")
//                    editTextAddress.setText(data?.employee?.employee_detail?.address?: "")
//                    editTextCity.setText(data?.employee?.employee_detail?.city?: "")
//                    editTextState.setText(data?.employee?.employee_detail?.state?: "")
//                    editTextPinCode.setText(data?.employee?.employee_detail?.pincode?: "")
//                    editTextCountry.setText(data?.employee?.employee_detail?.country_of_birth?: "")
//                    editPermanentaddress.setText(data?.employee?.employee_detail?.permanent_address?: "")
//                    editLastEducation.setText(data?.employee?.employee_detail?.last_education?: "NA")
//
//
//
//
//                    officialEmploymentDetails.apply {
//                        editOfficialEmailID.setText(data?.employee?.employee_detail?.email?: "")
//                        editPersonalEmailID.setText(data?.employee?.employee_detail?.p_email?: "")
//                        editPreviousEmploymentDetails.setText(data?.employee?.employee_detail?.previous_employee_details?: "")
//                        editUanNumber.setText(data?.employee?.employee_detail?.uan_no?: "")
//                        editDateOfJoining.setText(data?.employee?.employee_detail?.marriage_anniversary?: "")
//                        editWorkExperience.setText(""+data?.employee?.employee_detail?.work_experience_in_years?: "")
//                    }
//
//
//
//                    bankDetails.apply {
//                        editBankName.setText(data?.employee?.employee_detail?.bank_name?: "")
//                        editAccountHolderName.setText(data?.employee?.employee_detail?.account_holder_name?: "")
//                        editBankAccountNumber.setText(data?.employee?.employee_detail?.bank_account_number?: "")
//                        editIFSCCode.setText(data?.employee?.employee_detail?.ifsc_code?: "")
//                        editBranchNameAddress.setText(data?.employee?.employee_detail?.branch_name_address?: "")
//                        editAccountType.setText(data?.employee?.employee_detail?.account_type?: "")
//
//
////                        editPosition.setText(data?.employee?.position?.name?: "")
////                        editMotherName.setText(data?.employee?.employee_detail?.mother_name?: "")
////                        editSpouseName.setText(data?.employee?.employee_detail?.spouse_name?: "")
////                        editMarriageAnniversary.setText(data?.employee?.employee_detail?.marriage_anniversary?: "")
////
////                        editIdentityNumber.setText(data?.employee?.employee_detail?.identity_number?: "")
////                        editPanNumber.setText(data?.employee?.employee_detail?.pan_number?: "")
////                        editUanNumber.setText(data?.employee?.employee_detail?.uan_no?: "")
////                        editPermanentaddress.setText(data?.employee?.employee_detail?.permanent_address?: "")
////
////                        editCountryofbirth.setText(data?.employee?.employee_detail?.country_of_birth?: "")
////                        editBirthplace.setText(data?.employee?.employee_detail?.birth_place?: "")
////                        editNationality.setText(data?.employee?.employee_detail?.nationality?: "")
////                        editPersonalemail.setText(data?.employee?.employee_detail?.p_email?: "")
////
////                        editBloodgroup.setText(data?.employee?.employee_detail?.blood_group?: "")
////                        editEmergencymobile.setText(data?.employee?.employee_detail?.e_mobile?: "")
////                        editLastEducation.setText(data?.employee?.employee_detail?.last_education?: "")
////                        editGPA.setText(data?.employee?.employee_detail?.gpa?: "")
////                        editWorkExperience.setText(""+data?.employee?.employee_detail?.work_experience_in_years?: "")
//                    }





                    viewModel.isEditable.value = false
//                    fieldsEdit()
                }
            }



        }
    }



//
//    private fun fieldsEdit() {
//        binding.apply {
//            viewModel.isEditable.observe(viewLifecycleOwner, Observer {
//                textName.isEnabled = it
////                editEmail.isEnabled = it
//                editMobile.isEnabled = it
//                editTextAddress.isEnabled = it
//                ivProfileImage.isEnabled = true
//                btnProfileImage.isEnabled = true
//
//                if (it == true){
//                    btnProfileImage.visibility = View.VISIBLE
//                    ivProfileImage.singleClick {
//                        imagePosition = 1
//                        callMediaPermissions()
//                    }
//                }
//                lateinit var viewer: StfalconImageViewer<String>
//
//                if (it == false){
//                    ivProfileImage.loadImage(type = 1, url = { IMAGE_URL+data?.employee?.employee_detail?.photo?: "" })
//
//                    btnProfileImage.visibility = View.GONE
//                    ivProfileImage.singleClick {
//                        viewer = StfalconImageViewer.Builder<String>(requireActivity(), arrayListOf(IMAGE_URL+data?.employee?.employee_detail?.photo?: "")) { view, image ->
//                            Picasso.get().load(image).into(view)
//                        }.withImageChangeListener {
//                            viewer.updateTransitionImage(ivProfileImage)
//                        }
//                            .withBackgroundColor(
//                                ContextCompat.getColor(
//                                    requireActivity(),
//                                    R.color._D9000000
//                                )
//                            )
//                            .show()
//                    }
//                }
//
//
//
//
////                includeProfileInfo.apply {
////                    textStartDateValue.isEnabled = it
////                    textEndDateValue.isEnabled = it
////
////                    editDepartment.isEnabled = it
////                    editPosition.isEnabled = it
////                    editGender.isEnabled = it
////                    editDateOfBirth.isEnabled = it
////
////                    editFatherName.isEnabled = it
////                    editMotherName.isEnabled = it
////                    editSpouseName.isEnabled = it
////                    editMarriageAnniversary.isEnabled = it
////
////                    editIdentityNumber.isEnabled = it
////                    editPanNumber.isEnabled = it
////                    editUanNumber.isEnabled = it
////                    editPermanentaddress.isEnabled = it
////
////                    editCountryofbirth.isEnabled = it
////                    editBirthplace.isEnabled = it
////                    editNationality.isEnabled = it
////                    editPersonalemail.isEnabled = it
////
////                    editBloodgroup.isEnabled = it
////                    editEmergencymobile.isEnabled = it
////                    editLastEducation.isEnabled = it
////                    editGPA.isEnabled = it
////                    editWorkExperience.isEnabled = it
////                }
//
//            })
//        }
//    }






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




    //pincode
    //city
    // state
    //country

}