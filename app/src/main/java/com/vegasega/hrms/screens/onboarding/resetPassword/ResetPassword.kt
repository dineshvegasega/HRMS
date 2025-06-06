package com.vegasega.hrms.screens.onboarding.resetPassword

import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.ResetPasswordBinding
import com.vegasega.hrms.models.Login
import com.vegasega.hrms.networking.Main
import com.vegasega.hrms.networking.mobile_number
import com.vegasega.hrms.networking.password
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.hrms.utils.callNetworkDialog
import com.vegasega.hrms.utils.isValidPassword
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MultipartBody
import kotlin.getValue

@AndroidEntryPoint
class ResetPassword : Fragment() {
    private var _binding: ResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResetPasswordVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ResetPasswordBinding.inflate(inflater)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            ivMenu.singleClick {
                findNavController().navigateUp()
            }
            btSignIn.setEnabled(true)


            var counterOld = 0
            var startOld: Int
            var endOld: Int
            imgCreateOldPassword.singleClick {
                if (counterOld == 0) {
                    counterOld = 1
                    imgCreateOldPassword.setImageResource(R.drawable.ic_eye_open)
                    startOld = editTextOldPassword.getSelectionStart()
                    endOld = editTextOldPassword.getSelectionEnd()
                    editTextOldPassword.setTransformationMethod(null)
                    editTextOldPassword.setSelection(startOld, endOld)
                } else {
                    counterOld = 0
                    imgCreateOldPassword.setImageResource(R.drawable.ic_eye_closed)
                    startOld = editTextOldPassword.getSelectionStart()
                    endOld = editTextOldPassword.getSelectionEnd()
                    editTextOldPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextOldPassword.setSelection(startOld, endOld)
                }
            }


            var counter = 0
            var start: Int
            var end: Int
            imgCreatePassword.singleClick {
                if (counter == 0) {
                    counter = 1
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_open)
                    start = editTextPassword.getSelectionStart()
                    end = editTextPassword.getSelectionEnd()
                    editTextPassword.setTransformationMethod(null)
                    editTextPassword.setSelection(start, end)
                } else {
                    counter = 0
                    imgCreatePassword.setImageResource(R.drawable.ic_eye_closed)
                    start = editTextPassword.getSelectionStart()
                    end = editTextPassword.getSelectionEnd()
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod())
                    editTextPassword.setSelection(start, end)
                }
            }


            var counter2 = 0
            var start2: Int
            var end2: Int
            imgCreatePasswordConfirm.singleClick {
                if (counter2 == 0) {
                    counter2 = 1
                    imgCreatePasswordConfirm.setImageResource(R.drawable.ic_eye_open)
                    start2 = editTextPasswordConfirm.getSelectionStart()
                    end2 = editTextPasswordConfirm.getSelectionEnd()
                    editTextPasswordConfirm.setTransformationMethod(null)
                    editTextPasswordConfirm.setSelection(start2, end2)
                } else {
                    counter2 = 0
                    imgCreatePasswordConfirm.setImageResource(R.drawable.ic_eye_closed)
                    start2 = editTextPasswordConfirm.getSelectionStart()
                    end2 = editTextPasswordConfirm.getSelectionEnd()
                    editTextPasswordConfirm.setTransformationMethod(PasswordTransformationMethod())
                    editTextPasswordConfirm.setSelection(start2, end2)
                }
            }



            btSignIn.singleClick {
//                showSnackBar(view.resources.getString(R.string.password_changed_successfully))
//                MainActivity.mainActivity.get()?.reloadActivity("en", Main)

                if (editTextOldPassword.text.toString().length >= 0 && editTextOldPassword.text.toString().length < 8) {
                    showSnackBar(view.resources.getString(R.string.InvalidPassword))
                } else if (editTextPassword.text.toString().length >= 0 && editTextPassword.text.toString().length < 8) {
                    showSnackBar(view.resources.getString(R.string.InvalidPassword))
                } else if (!isValidPassword(editTextPassword.text.toString().trim())) {
                    showSnackBar(view.resources.getString(R.string.InvalidPassword))
                } else if (editTextPasswordConfirm.text.toString().length >= 0 && editTextPasswordConfirm.text.toString().length < 8) {
                    showSnackBar(view.resources.getString(R.string.InvalidPassword))
                } else if (!isValidPassword(editTextPasswordConfirm.text.toString().trim())) {
                    showSnackBar(view.resources.getString(R.string.InvalidPassword))
                } else if (editTextPassword.text.toString().trim() != editTextPasswordConfirm.text.toString().trim()) {
                    showSnackBar(view.resources.getString(R.string.CreatePasswordReEnterPasswordisnotsame))
                } else {
                    val requestBody: MultipartBody.Builder = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                    requestBody.addFormDataPart(
                        "current_password",
                        editTextOldPassword.text.toString()
                    )
                    requestBody.addFormDataPart("new_password", editTextPassword.text.toString())
                    requestBody.addFormDataPart(
                        "new_password_confirmation",
                        editTextPasswordConfirm.text.toString()
                    )
                    if (networkFailed) {
                        viewModel.resetPassword(requestBody.build()){
                            viewModel.profile(view){
                                showSnackBar(view.resources.getString(R.string.logged_in_successfully))
                                MainActivity.mainActivity.get()?.reloadActivity("en", Main)
                            }
                        }
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }


            }


        }


    }
}