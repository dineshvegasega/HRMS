package com.vegasega.hrms.screens.main.NBPA.addForms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.github.gcacace.signaturepad.views.SignaturePad
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.Form5Binding
import com.vegasega.hrms.networking.*
import com.vegasega.hrms.screens.interfaces.CallBackListener
import com.vegasega.hrms.screens.main.NBPA.NBPAViewModel
import com.vegasega.hrms.screens.main.NBPA.addForms.NBPA_Form3.Companion.formFill3
import com.vegasega.hrms.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.getImageName
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.showDropDownDialog
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class NBPA_Form5 : Fragment(), CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form5Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Form5Binding.inflate(inflater, container, false)
        return binding.root
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this
        binding.apply {
            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1003)
            }

            editTextHealthDate.singleClick {
                requireActivity().showDropDownDialog(type = 20) {
                    editTextHealthDate.setText(title)
                    viewModel.assistanceDBTDate = title
                }
            }
            editTextmultiVitaminDate.singleClick {
                requireActivity().showDropDownDialog(type = 20) {
                    editTextmultiVitaminDate.setText(title)
                    viewModel.assistanceMultiVitaminDate = title
                }
            }




            signatureProjectCoordinatorPad.setOnSignedListener(object :
                SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                    //Event triggered when the pad is touched
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onSigned() {
                    //Event triggered when the pad is signed
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onClear() {
                    //Event triggered when the pad is cleared
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }
            })

            btCompleteProjectCoordinator.singleClick {
                ivSignatureProjectCoordinator.setImageBitmap(signatureProjectCoordinatorPad.signatureBitmap)
                ivSignatureProjectCoordinator.visibility = View.VISIBLE
                signatureProjectCoordinatorPad.visibility = View.GONE
                btTryAgainProjectCoordinator.visibility = View.VISIBLE
                btCompleteProjectCoordinator.visibility = View.GONE
                btClearProjectCoordinator.visibility = View.INVISIBLE

                callMediaPermissionsCoordinator()
            }

            btTryAgainProjectCoordinator.singleClick {
                signatureProjectCoordinatorPad.clear()
                ivSignatureProjectCoordinator.visibility = View.GONE
                signatureProjectCoordinatorPad.visibility = View.VISIBLE
                btTryAgainProjectCoordinator.visibility = View.GONE
                btCompleteProjectCoordinator.visibility = View.VISIBLE
                btClearProjectCoordinator.visibility = View.VISIBLE
                viewModel.assistanceProjectCoordinatorSignature = ""
            }

            btClearProjectCoordinator.singleClick {
                signatureProjectCoordinatorPad.clear()
                viewModel.assistanceProjectCoordinatorSignature = ""
            }





            signatureProjectManagerPad.setOnSignedListener(object : SignaturePad.OnSignedListener {
                override fun onStartSigning() {
                    //Event triggered when the pad is touched
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onSigned() {
                    //Event triggered when the pad is signed
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }

                override fun onClear() {
                    //Event triggered when the pad is cleared
//                    Toast.makeText(requireActivity(), "onStartSigning() triggered!", Toast.LENGTH_SHORT)
//                        .show()
                }
            })

            btCompleteProjectManager.singleClick {
                ivSignatureProjectManager.setImageBitmap(signatureProjectManagerPad.signatureBitmap)
                ivSignatureProjectManager.visibility = View.VISIBLE
                signatureProjectManagerPad.visibility = View.GONE
                btTryAgainProjectManager.visibility = View.VISIBLE
                btCompleteProjectManager.visibility = View.GONE
                btClearProjectManager.visibility = View.INVISIBLE

                callMediaPermissionsManager()
            }

            btTryAgainProjectManager.singleClick {
                signatureProjectManagerPad.clear()
                ivSignatureProjectManager.visibility = View.GONE
                signatureProjectManagerPad.visibility = View.VISIBLE
                btTryAgainProjectManager.visibility = View.GONE
                btCompleteProjectManager.visibility = View.VISIBLE
                btClearProjectManager.visibility = View.VISIBLE
                viewModel.assistanceProjectManagerSignature = ""
            }

            btClearProjectManager.singleClick {
                signatureProjectManagerPad.clear()
                viewModel.assistanceProjectManagerSignature = ""
            }




            btSignIn.singleClick {
                Log.e("TAG", "formFill3 " + formFill3)
                if (formFill3 == true) {
                    Log.e("TAG", "formFillAAAAA " + formFill3)
                    getData(true)
                } else {
                    Log.e("TAG", "formFillBBBBB " + formFill3)
                    showSnackBar(getString(R.string.please_fill_required_entries))
                }
            }
        }
    }


    private fun callMediaPermissionsCoordinator() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncherCoordinator.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncherCoordinator.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            activityResultLauncherCoordinator.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val activityResultLauncherCoordinator =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                mainThread {
                    dispatchTakePictureIntent(binding.ivSignatureProjectCoordinator) {
                        viewModel.assistanceProjectCoordinatorSignature = this
                        Log.e(
                            "TAG",
                            "viewModel.assistanceProjectCoordinatorSignature " + viewModel.assistanceProjectCoordinatorSignature
                        )
                    }
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncherCoordinator.launch(this)
                }
            }
        }


    var someActivityResultLauncherCoordinator = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissionsCoordinator()
    }


    private fun callMediaPermissionsManager() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncherManager.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncherManager.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            )
        } else {
            activityResultLauncherManager.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private val activityResultLauncherManager =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        )
        { permissions ->
            if (!permissions.entries.toString().contains("false")) {
                mainThread {
                    dispatchTakePictureIntent(binding.ivSignatureProjectManager) {
                        viewModel.assistanceProjectManagerSignature = this
                        Log.e(
                            "TAG",
                            "viewModel.assistanceProjectManagerSignature " + viewModel.assistanceProjectManagerSignature
                        )
                    }
                }
            } else {
                requireActivity().callPermissionDialog {
                    someActivityResultLauncherManager.launch(this)
                }
            }
        }


    var someActivityResultLauncherManager = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissionsManager()
    }


    private fun dispatchTakePictureIntent(imageView: View, callBack: String.() -> Unit) {
        val bitmap: Bitmap = getBitmapFromView(imageView)
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // val filename = System.currentTimeMillis().toString() + "." + "png" // change png/pdf
        val file = File(path, getImageName())
        try {
            if (!path.exists()) path.mkdirs()
            if (!file.exists()) file.createNewFile()
            val ostream: FileOutputStream = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 10, ostream)
            ostream.close()
            val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getApplicationContext().getPackageName() + ".provider",
                    file
                )
            } else {
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getApplicationContext().getPackageName() + ".provider",
                    imagePath
                )
            }
//            Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignature)
            callBack(file.toString())

        } catch (e: IOException) {
            Log.w("ExternalStorage", "Error writing $file", e)
        }
    }

    fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }


    override fun onCallBack(pos: Int) {
        Log.e("TAG", "onCallBackNo5 " + pos)
        getData(false)
    }


    private fun getData(isButton: Boolean) {
        binding.apply {
            viewModel.assistanceDBTTotalAmount = editTextTotalAmount.text.toString()
            viewModel.assistanceDBTDetails = editTextDetailsHealth.text.toString()
            viewModel.assistanceDBTRemark = editTextCommentHealth.text.toString()
            viewModel.assistanceExtraGroceryPDS =
                editTextAdditionalRationReceivedFromPds.text.toString()
            viewModel.assistanceExtraGroceryPDSDetails = editTextDetailsPDS.text.toString()
            viewModel.assistanceExtraGroceryPDSRemark = editTextCommentPDS.text.toString()

            viewModel.assistanceMultiVitaminTotalNumber =
                editTextTotalnumberobtained.text.toString()
            viewModel.assistanceMultiVitaminDetails = editTextDetailsVitamin.text.toString()
            viewModel.assistanceMultiVitaminRemark = editTextCommentVitamin.text.toString()
            viewModel.assistanceOtherHelp = editTextotherReceivedHelp.text.toString()
            viewModel.assistanceHelpDetails = editTextDetailsHelp.text.toString()
            viewModel.assistanceHelpRemark = editTextCommentHelp.text.toString()


            isProductLoad = true


            val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)


            if (viewModel.scheme_id != "") {
                requestBody.addFormDataPart("schemeId", viewModel.scheme_id)
            }
            if (viewModel.foodMonth != null) {
                requestBody.addFormDataPart(foodMonth, viewModel.foodMonth)
            }
            if (viewModel.foodDate != null) {
                requestBody.addFormDataPart(foodDate, viewModel.foodDate)
            }
            if (viewModel.foodHeight != null) {
                requestBody.addFormDataPart(foodHeight, viewModel.foodHeight)
            }

            if (viewModel.foodSignatureImage.isNotEmpty()) {
                requestBody.addFormDataPart(
                    foodSignatureImage,
                    File(viewModel.foodSignatureImage).name,
                    File(viewModel.foodSignatureImage).asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            if (viewModel.foodItemImage.isNotEmpty()) {
                requestBody.addFormDataPart(
                    foodItemImage,
                    File(viewModel.foodItemImage).name,
                    File(viewModel.foodItemImage).asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            if (viewModel.foodIdentityImage.isNotEmpty()) {
                requestBody.addFormDataPart(
                    foodIdentityImage,
                    File(viewModel.foodIdentityImage).name,
                    File(viewModel.foodIdentityImage).asRequestBody("image/*".toMediaTypeOrNull())
                )
            }


            if (viewModel.dietChartDate != null) {
                requestBody.addFormDataPart(dietChartDate, viewModel.dietChartDate)
            }
            if (viewModel.dietChartEvaluation != null) {
                requestBody.addFormDataPart(dietChartEvaluation, viewModel.dietChartEvaluation)
            }
            if (viewModel.dietChartSuggestion != null) {
                requestBody.addFormDataPart(dietChartSuggestion, viewModel.dietChartSuggestion)
            }
            if (viewModel.dietChartServiceProvider != null) {
                requestBody.addFormDataPart(
                    dietChartServiceProvider,
                    viewModel.dietChartServiceProvider
                )
            }
            if (viewModel.homeVisitDate != null) {
                requestBody.addFormDataPart(homeVisitDate, viewModel.homeVisitDate)
            }
            if (viewModel.homeVisitWeight != null) {
                requestBody.addFormDataPart(homeVisitWeight, viewModel.homeVisitWeight)
            }
            if (viewModel.homeVisitSignature.isNotEmpty()) {
                requestBody.addFormDataPart(
                    homeVisitSignature,
                    File(viewModel.homeVisitSignature).name,
                    File(viewModel.homeVisitSignature).asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            if (viewModel.homeVisitRemark != null) {
                requestBody.addFormDataPart(homeVisitRemark, viewModel.homeVisitRemark)
            }

            if (viewModel.assistanceDBTDate != null) {
                requestBody.addFormDataPart(assistanceDBTDate, viewModel.assistanceDBTDate)
            }
            if (viewModel.assistanceDBTTotalAmount != null) {
                requestBody.addFormDataPart(
                    assistanceDBTTotalAmount,
                    viewModel.assistanceDBTTotalAmount
                )
            }
            if (viewModel.assistanceDBTDetails != null) {
                requestBody.addFormDataPart(assistanceDBTDetails, viewModel.assistanceDBTDetails)
            }
            if (viewModel.assistanceDBTRemark != null) {
                requestBody.addFormDataPart(assistanceDBTRemark, viewModel.assistanceDBTRemark)
            }

            if (viewModel.assistanceExtraGroceryPDS != null) {
                requestBody.addFormDataPart(
                    assistanceExtraGroceryPDS,
                    viewModel.assistanceExtraGroceryPDS
                )
            }
            if (viewModel.assistanceExtraGroceryPDSDetails != null) {
                requestBody.addFormDataPart(
                    assistanceExtraGroceryPDSDetails,
                    viewModel.assistanceExtraGroceryPDSDetails
                )
            }
            if (viewModel.assistanceExtraGroceryPDSRemark != null) {
                requestBody.addFormDataPart(
                    assistanceExtraGroceryPDSRemark,
                    viewModel.assistanceExtraGroceryPDSRemark
                )
            }
            if (viewModel.assistanceMultiVitaminDate != null) {
                requestBody.addFormDataPart(
                    assistanceMultiVitaminDate,
                    viewModel.assistanceMultiVitaminDate
                )
            }

            if (viewModel.assistanceMultiVitaminTotalNumber != null) {
                requestBody.addFormDataPart(
                    assistanceMultiVitaminTotalNumber,
                    viewModel.assistanceMultiVitaminTotalNumber
                )
            }
            if (viewModel.assistanceMultiVitaminDetails != null) {
                requestBody.addFormDataPart(
                    assistanceMultiVitaminDetails,
                    viewModel.assistanceMultiVitaminDetails
                )
            }
            if (viewModel.assistanceMultiVitaminRemark != null) {
                requestBody.addFormDataPart(
                    assistanceMultiVitaminRemark,
                    viewModel.assistanceMultiVitaminRemark
                )
            }
            if (viewModel.assistanceOtherHelp != null) {
                requestBody.addFormDataPart(assistanceOtherHelp, viewModel.assistanceOtherHelp)
            }

            if (viewModel.assistanceHelpDetails != null) {
                requestBody.addFormDataPart(assistanceHelpDetails, viewModel.assistanceHelpDetails)
            }
            if (viewModel.assistanceHelpRemark != null) {
                requestBody.addFormDataPart(assistanceHelpRemark, viewModel.assistanceHelpRemark)
            }
            if (viewModel.assistanceProjectCoordinatorSignature.isNotEmpty()) {
                requestBody.addFormDataPart(
                    projectCoordinatorSignature,
                    File(viewModel.assistanceProjectCoordinatorSignature).name,
                    File(viewModel.assistanceProjectCoordinatorSignature).asRequestBody("image/*".toMediaTypeOrNull())
                )
            }
            if (viewModel.assistanceProjectManagerSignature.isNotEmpty()) {
                requestBody.addFormDataPart(
                    projectManagerSignature,
                    File(viewModel.assistanceProjectManagerSignature).name,
                    File(viewModel.assistanceProjectManagerSignature).asRequestBody("image/*".toMediaTypeOrNull())
                )
            }

            Log.e("TAG", "formFill3: " + formFill3)

            viewModel.registerWithFilesFoodItems(
                view = requireView(),
                requestBody.build()
            ) {
                if (isButton) {
                    showSnackBar(requireView().resources.getString(R.string.forms_added_successfully))
                    requireView().findNavController().navigate(R.id.action_nbpa_to_nbpaList)
                } else {

                }

            }

        }
    }
}