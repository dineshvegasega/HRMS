package com.vegasega.hrms.screens.main.NBPA.addForms

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.vegasega.hrms.R
import com.vegasega.hrms.databinding.Form3Binding
import com.vegasega.hrms.datastore.DataStoreKeys.LOGIN_DATA
import com.vegasega.hrms.datastore.DataStoreUtil.readData
import com.vegasega.hrms.models.Login
import com.vegasega.hrms.screens.interfaces.CallBackListener
import com.vegasega.hrms.screens.main.NBPA.NBPAViewModel
import com.vegasega.hrms.screens.mainActivity.MainActivityVM.Companion.isProductLoad
import com.vegasega.hrms.screens.onboarding.register.Register.Companion.imagePath
import com.vegasega.hrms.utils.callPermissionDialog
import com.vegasega.hrms.utils.getAddress
import com.vegasega.hrms.utils.getCameraPath
import com.vegasega.hrms.utils.getImageName
import com.vegasega.hrms.utils.getMediaFilePathFor
import com.vegasega.hrms.utils.getMonthFromHindi
import com.vegasega.hrms.utils.loadImage
import com.vegasega.hrms.utils.mainThread
import com.vegasega.hrms.utils.showDropDownDialog
import com.vegasega.hrms.utils.showOptions
import com.vegasega.hrms.utils.showSnackBar
import com.vegasega.hrms.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

@AndroidEntryPoint
class NBPA_Form3 : Fragment() , CallBackListener {
    private lateinit var viewModel: NBPAViewModel
    private var _binding: Form3Binding? = null
    private val binding get() = _binding!!

    companion object {
        var callBackListener: CallBackListener? = null
        var tabPosition = 0
        var formFill3 = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = Form3Binding.inflate(inflater, container, false)
        return binding.root
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(NBPAViewModel::class.java)
        callBackListener = this
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())


        binding.apply {

            ivMenu.singleClick {
                NBPA.callBackListener!!.onCallBack(1001)
            }

            signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
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

            btComplete.singleClick {
                ivSignature.setImageBitmap(signaturePad.signatureBitmap)
                ivSignature.visibility = View.VISIBLE
                signaturePad.visibility = View.GONE
                btTryAgain.visibility = View.VISIBLE
                btComplete.visibility = View.GONE
                btClear.visibility = View.INVISIBLE

                callMediaPermissions()
            }

            btTryAgain.singleClick {
                signaturePad.clear()
                ivSignature.visibility = View.GONE
                signaturePad.visibility = View.VISIBLE
                btTryAgain.visibility = View.GONE
                btComplete.visibility = View.VISIBLE
                btClear.visibility = View.VISIBLE
                viewModel.foodSignatureImage = ""
                formFill3 = false
            }

            btClear.singleClick {
                signaturePad.clear()
                viewModel.foodSignatureImage = ""
                formFill3 = false
            }

            editTextMonth.singleClick {
                requireActivity().showDropDownDialog(type = 21){
                    Log.e("TAG", "getMonthFromHindi "+title)
                    editTextMonth.setText(title.getMonthFromHindi())
                    viewModel.foodMonth = title.getMonthFromHindi()
                }
            }

            editTextDate.singleClick {
                requireActivity().showDropDownDialog(type = 22){
                    editTextDate.setText(title)
                    viewModel.foodDate = title
                }
            }



            ivImagePassportsizeImage.singleClick {
                imagePosition = 1
                callMediaPermissionsWithLocation()
            }

            ivImageIdentityImage.singleClick {
                imagePosition = 2
                callMediaPermissionsWithLocation()
            }


            btnImagePassportsize.singleClick {
                imagePosition = 1
                callMediaPermissionsWithLocation()
            }

            btnIdentityImage.singleClick {
                imagePosition = 2
                callMediaPermissionsWithLocation()
            }





            btSignIn.singleClick {
                isProductLoad = true
                getData(true)
            }

        }


    }




    private fun callMediaPermissionsWithLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            activityResultLauncherWithLocation.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activityResultLauncherWithLocation.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            activityResultLauncherWithLocation.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }


    private val activityResultLauncherWithLocation =
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
                    someActivityResultLauncherWithLocation.launch(this)
                }
            }
        }


    var someActivityResultLauncherWithLocation = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissionsWithLocation()
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




    var imagePosition = 0
    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    private var pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
                if (uri != null) {
                    when (imagePosition) {
                        1 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
//                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                Log.e("TAG", "addOnSuccessListenerRegisterAA " + location.toString())
                                imagePath = compressedImageFile.path
                                var latLong = LatLng(location!!.latitude, location.longitude)

                                readData(LOGIN_DATA) { loginUser ->
                                    if (loginUser != null) {
                                        val data = Gson().fromJson(loginUser, Login::class.java)
                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())

                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                        readData(GEO_LOCATION) { geoLocation ->
//                                            if (geoLocation == "true") {
//                                                readData(GEO_LAT_LONG) { latLong ->
//                                                    if (latLong!!.isNotEmpty()) {
//                                                        val latLong = latLong!!.split(",")
//                                                        mainThread {
//                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                                viewModel.foodItemImage = this
//                                                                Log.e("TAG", "viewModel.foodItemImage " + this)
//                                                                binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            } else {
                                                mainThread {
                                                    binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                    dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                        viewModel.foodItemImage = this
                                                        Log.e("TAG", "viewModel.foodItemImage " + this)
                                                        binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
                                                    }
                                                }
//                                            }
//                                        }
                                    }
                                }
                            }
                        }

                        2 -> {
                            val compressedImageFile = Compressor.compress(
                                requireContext(),
                                File(requireContext().getMediaFilePathFor(uri))
                            )
//                            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                Log.e("TAG", "addOnSuccessListenerRegisterBB " + location.toString())
                                imagePath = compressedImageFile.path
                                var latLong = LatLng(location!!.latitude, location.longitude)

                                readData(LOGIN_DATA) { loginUser ->
                                    if (loginUser != null) {
                                        val data = Gson().fromJson(loginUser, Login::class.java)
                                        binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                        val currentDate = sdf.format(Date())

                                        binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                        binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                        binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                        readData(GEO_LOCATION) { geoLocation ->
//                                            if (geoLocation == "true") {
//                                                readData(GEO_LAT_LONG) { latLong ->
//                                                    if (latLong!!.isNotEmpty()) {
//                                                        val latLong = latLong!!.split(",")
//                                                        mainThread {
//                                                            binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                            dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                                viewModel.foodIdentityImage = this
//                                                                Log.e("TAG", "viewModel.foodIdentityImage " + this)
//                                                                binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                                            }
//                                                        }
//                                                    }
//                                                }
//                                            } else {
                                                mainThread {
                                                    binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                    dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                        viewModel.foodIdentityImage = this
                                                        Log.e("TAG", "viewModel.foodIdentityImage " + this)
                                                        binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
                                                    }
                                                }
//                                            }
//                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


    var uriReal: Uri? = null
    @SuppressLint("MissingPermission")
    val captureMedia = registerForActivityResult(ActivityResultContracts.TakePicture()) { uri ->
        lifecycleScope.launch @androidx.annotation.RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION]) {
            if (uri == true) {

                when (imagePosition) {
                    1 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
//                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            Log.e("TAG", "addOnSuccessListenerRegisterAA " + location.toString())
                            imagePath = compressedImageFile.path
                            var latLong = LatLng(location!!.latitude, location.longitude)

                            readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val data = Gson().fromJson(loginUser, Login::class.java)
                                    binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                    val currentDate = sdf.format(Date())

                                    binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                    binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                    binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                    readData(GEO_LOCATION) { geoLocation ->
//                                        if (geoLocation == "true") {
//                                            readData(GEO_LAT_LONG) { latLong ->
//                                                if (latLong!!.isNotEmpty()) {
//                                                    val latLong = latLong!!.split(",")
//                                                    mainThread {
//                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                            viewModel.foodItemImage = this
//                                                            Log.e("TAG", "viewModel.foodItemImage " + this)
//                                                            binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        } else {
                                            mainThread {
                                                binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                    viewModel.foodItemImage = this
                                                    Log.e("TAG", "viewModel.foodItemImage " + this)
                                                    binding.ivImagePassportsizeImage.loadImage(type = 1, url = { this })
                                                }
                                            }
//                                        }
//                                    }
                                }
                            }
                        }
                    }

                    2 -> {
                        val compressedImageFile = Compressor.compress(
                            requireContext(),
                            File(requireContext().getMediaFilePathFor(uriReal!!))
                        )
//                        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            Log.e("TAG", "addOnSuccessListenerRegisterBB " + location.toString())
                            imagePath = compressedImageFile.path
                            var latLong = LatLng(location!!.latitude, location.longitude)

                            readData(LOGIN_DATA) { loginUser ->
                                if (loginUser != null) {
                                    val data = Gson().fromJson(loginUser, Login::class.java)
                                    binding.ivIcon.setImageURI(Uri.fromFile(File(imagePath)))
                                    val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
                                    val currentDate = sdf.format(Date())

                                    binding.textClickByTxt.text = getString(R.string.geoClickby) + " "+ data.vendor_first_name +" "+data.vendor_last_name

                                    binding.textTimeTxt.text = getString(R.string.geoDateTime) + " "+ currentDate
                                    binding.textLatLongTxt.text = getString(R.string.geoLatLng) + " "+ latLong.latitude+","+latLong.longitude

//                                    readData(GEO_LOCATION) { geoLocation ->
//                                        if (geoLocation == "true") {
//                                            readData(GEO_LAT_LONG) { latLong ->
//                                                if (latLong!!.isNotEmpty()) {
//                                                    val latLong = latLong!!.split(",")
//                                                    mainThread {
//                                                        binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(LatLng(latLong[0].toDouble(), latLong[1].toDouble()))
//                                                        dispatchTakePictureIntent(binding.layoutMainCapture) {
//                                                            viewModel.foodIdentityImage = this
//                                                            Log.e("TAG", "viewModel.foodIdentityImage " + this)
//                                                            binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        } else {
                                            mainThread {
                                                binding.textAddressTxt.text = getString(R.string.geAddress) + " "+ requireActivity().getAddress(latLong)
                                                dispatchTakePictureIntent(binding.layoutMainCapture) {
                                                    viewModel.foodIdentityImage = this
                                                    Log.e("TAG", "viewModel.foodIdentityImage " + this)
                                                    binding.ivImageIdentityImage.loadImage(type = 1, url = { this })
                                                }
                                            }
//                                        }
//                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }







    private fun callMediaPermissions() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)
            )
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES)
            )
        } else{
            activityResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            )
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            if(!permissions.entries.toString().contains("false")){
                mainThread {
                    dispatchTakePictureIntent(binding.ivSignature){
                        viewModel.foodSignatureImage = this
                        Log.e("TAG", "viewModel.foodSignature "+viewModel.foodSignatureImage)
                    }
                }
            } else {
                requireActivity().callPermissionDialog{
                    someActivityResultLauncher.launch(this)
                }
            }
        }


    var someActivityResultLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        callMediaPermissions()
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
            val data = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file)
            }else{
                val imagePath: File = File(file.absolutePath)
                FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", imagePath)
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
        Log.e("TAG", "onCallBackNo3 "+pos)
        getData(false)
    }




    private fun getData(isButton: Boolean) {
//        var bool: Boolean = false
//        if (viewModel.editDataNew != null){
//            var list = viewModel.editDataNew!!.schemeDetail
//            var isArray = list.filter { it.foodMonth == viewModel.foodMonth }
//            bool = if (isArray.size == 0) false else true
//
//            Log.e("TAG", "getDatabool "+isArray.size)
//        }


        binding.apply {
            if (editTextMonth.text.toString() == "") {
                showSnackBar(getString(R.string.select_month))
                formFill3 = false
            } else if (editTextDate.text.toString() == "") {
                showSnackBar(getString(R.string.select_date))
                formFill3 = false
            } else if (editTextHeight.text.toString() == "") {
                showSnackBar(getString(R.string.enterWeight))
                formFill3 = false
            } else if (viewModel.foodSignatureImage == "") {
                showSnackBar(getString(R.string.add_signature))
                formFill3 = false
            } else if (viewModel.foodItemImage == "") {
                showSnackBar(getString(R.string.food_item_image))
                formFill3 = false
            } else if (viewModel.foodIdentityImage == "") {
                showSnackBar(getString(R.string.identity_imageStar))
                formFill3 = false
            }  else {
//                if (viewModel.start == "no"){
                    viewModel.foodHeight = editTextHeight.text.toString()
                    formFill3 = true
                    if (isButton){
                        NBPA.callBackListener!!.onCallBack(1003)
                    } else {
                    }
//                }
//                if (viewModel.start == "yes"){
//                    if (bool == true) {
//                        showSnackBar(requireView().resources.getString(R.string.food_already_taken))
//                        formFill3 = false
//                    } else {
//                        viewModel.foodHeight = editTextHeight.text.toString()
//                        formFill3 = true
//                        if (isButton){
//                            NBPA.callBackListener!!.onCallBack(1003)
//                        } else {
//                        }
//                    }
//                }
            }
        }
    }

//    override fun onStop() {
//        super.onStop()
//        isProductLoad = true
//        isProductLoadMember = true
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        isProductLoad = false
//        isProductLoadMember = false
//    }

}