package com.vegasega.hrms.screens.onboarding.loginPassword

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.LoginPasswordBinding
import com.vegasega.hrms.networking.*
import com.vegasega.hrms.screens.mainActivity.MainActivity
import com.vegasega.hrms.screens.mainActivity.MainActivity.Companion.networkFailed
import com.vegasega.hrms.utils.callNetworkDialog
import com.vegasega.hrms.utils.isValidPassword
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONObject


@AndroidEntryPoint
class LoginPassword : Fragment() {
    private val viewModel: LoginPasswordVM by viewModels()
    private var _binding: LoginPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginPasswordBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.mainActivity.get()?.callFragment(0)

        binding.apply {
            textBack.singleClick {
                view.findNavController().navigateUp()
            }

            setSearchButtons()

            btEmployee.singleClick {
                viewModel.loginType = 1
                setSearchButtons()
            }

            btAdmin.singleClick {
                viewModel.loginType = 2
                setSearchButtons()
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

            textForgotPassword.singleClick {
                view.findNavController().navigate(
                    R.id.action_loginPassword_to_forgetPassword,
                    Bundle().apply {
                        putString("from", "login")
                    }
                )
            }

            editTextLoginWith.singleClick {
                view.findNavController().navigate(R.id.action_loginPassword_to_loginOtp)
            }


            binding.btSignIn.singleClick {
                if (binding.editTextMobileNumber.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.enterEmailAddr))
                } else if (binding.editTextPassword.text.toString().isEmpty()) {
                    showSnackBar(getString(R.string.EnterPassword))
                } else if (binding.editTextPassword.text.toString().length >= 0 && binding.editTextPassword.text.toString().length < 8) {
                    showSnackBar(getString(R.string.InvalidPassword))
//                    } else if(!isValidPassword(editTextPassword.text.toString().trim())){
//                        showSnackBar(getString(R.string.InvalidPassword))
                } else {
                    val obj: JSONObject = JSONObject().apply {
                        put(email, binding.editTextMobileNumber.text.toString())
                        put(password, binding.editTextPassword.text.toString())
                    }
                    if (networkFailed) {
                        viewModel.login(view = requireView(), obj)
                    } else {
                        requireContext().callNetworkDialog()
                    }
                }
            }


        }


        binding.editTextForgot.singleClick {
            view.findNavController().navigate(R.id.action_loginPassword_to_forgetPassword)
        }

    }



    private fun setSearchButtons() {
        if (viewModel.loginType == 1) {
            binding.btAdmin.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btEmployee.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btAdmin.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btEmployee.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E79D46))
            binding.btAdmin.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E79D46))
            binding.btEmployee.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E79D46))
        } else {
            binding.btAdmin.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.white
                )
            )
            binding.btEmployee.setTextColor(
                ContextCompat.getColor(
                    MainActivity.context.get()!!,
                    R.color.black
                )
            )
            binding.btAdmin.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E79D46))
            binding.btEmployee.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white))
            binding.btAdmin.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E79D46))
            binding.btEmployee.strokeColor =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color._E79D46))
        }

    }



    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}