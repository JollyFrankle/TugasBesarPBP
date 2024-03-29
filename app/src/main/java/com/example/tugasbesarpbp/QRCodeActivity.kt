package com.example.tugasbesarpbp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.tugasbesarpbp.databinding.ActivityQrcodeBinding
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.shashank.sony.fancytoastlib.FancyToast
import timber.log.Timber

class QRCodeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQrcodeBinding
    private lateinit var loader: ConstraintLayout

    companion object{
        private const val CAMERA_REQUEST_CODE = 100
        private const val STORAGE_REQUEST_CODE = 101

        private const val TAG = "MAIN_TAG"
    }
    //btnScan.setOnClickListener
    // di klik langsung buka kamera ke activity baru -> foto -> tampilin gambarnya sekalian bawahnya ada link nya

    private lateinit var cameraPermissions: Array<String>
    private lateinit var storagePermissions: Array<String>

    private var imageUri: Uri? = null

    private var barcodeScannerOption: BarcodeScannerOptions? = null
    private var barcodeScanner: BarcodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.plant(Timber.DebugTree())

//        binding.cameraBtn.setOnClickListener(this)
        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        barcodeScannerOption = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
        barcodeScanner = BarcodeScanning.getClient(barcodeScannerOption!!)

        loader = binding.layoutLoader.layoutLoader

        binding.btnBukaKamera.setOnClickListener {
            if (!checkCameraPermissions()){
                requestCameraPermission()
            }else{
                pickImageCamera()
            }
        }

        if(checkCameraPermissions()){
            pickImageCamera()
        } else{
            requestCameraPermission()
        }
    }

//    override fun onClick(v: View?){
//        when(v?.id){
//            R.id.cameraBtn -> {
//                if(checkCameraPermissions()){
//                    pickImageCamera()
//                } else{
//                    requestCameraPermission()
//                }
//            }
//            R.id.galleryBtn -> {
//                if(checkStoragePermission()){
//                    pickImageGallery()
//                } else{
//                    requestStoragePermission()
//                }
//            }
//            R.id.scanBtn -> {
//                if(imageUri == null){
//                    showToast("Pick image first")
//                } else{
//                    detectResultFromImage()
//                }
//            }
//        }
//    }

    private fun checkCameraPermissions() :Boolean{
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
//        val resultstorage = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//
//        return resultcamera && resultstorage
    }

    private fun requestCameraPermission(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE)
    }

//    private fun checkStoragePermission(): Boolean{
//        val result = (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
//
//        return result
//    }

//    private fun requestStoragePermission(){
//        ActivityCompat.requestPermissions(this, storagePermissions, CAMERA_REQUEST_CODE)
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if(grantResults.isNotEmpty()){
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if(cameraAccepted && storageAccepted){
                        pickImageCamera()
                    } else{
                        showToast("Camera dan storage permission are required")
                    }
                }
            }

//            STORAGE_REQUEST_CODE -> {
//                if (grantResults.isNotEmpty()){
//                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
//
//                    if(storageAccepted){
//                        pickImageGallery()
//                    } else{
//                        showToast("Storage are required...")
//                    }
//                }
//            }
        }
    }

    private fun showToast(message: String){
        FancyToast.makeText(this, message, FancyToast.LENGTH_SHORT, FancyToast.INFO, false).show()
    }

    private fun pickImageCamera(){
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Foto Sample")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Deskripsi Foto Sample")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        cameraActivityResultLauncher.launch(intent)
    }

    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
            result ->
        if(result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            Log.d(TAG, "cameraActivityResultLauncher: imageUri: $imageUri")

            binding.imageTv.setImageURI(imageUri)
            Timber.tag("Ini scanning image masuk detect").d("Scanning image")
            detectResultFromImage()
        } else {
            setLoadingScreen(false)
        }
    }

//    private fun pickImageGallery(){
//        val intent = Intent(Intent.ACTION_PICK)
//
//        intent.type = "image/*"
//        galleryActivityResultLauncher.launch(intent)
//    }

//    private val galleryActivityResultLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ){result ->
//        if(result.resultCode == Activity.RESULT_OK){
//            val data = result.data
//
//            imageUri = data!!.data
//            Log.d(TAG, "galleryActivityResultLauncher: imageUri : $imageUri")
//
//            binding.imageTv.setImageURI(imageUri)
//
//        } else{
//            showToast("Dibatalkan...")
//        }
//    }

    private fun detectResultFromImage(){
        try {
            setLoadingScreen(true)
            val inputImage = InputImage.fromFilePath(this, imageUri!!)
            Timber.tag("OTW").d("OTW Scanning Image")
            val barcodeResult = barcodeScanner?.process(inputImage)
                ?.addOnSuccessListener { barcodes ->
                    extractbarcodeQrCodeInfo(barcodes)
                    Timber.tag("Masuk detect").d("Berhasil detect bois")
                }
                ?.addOnFailureListener { e ->
                    Timber.tag(TAG).d(e, "detectResultFromImage : ")
                    showToast("Failed scanning due to ${e.message}")
                }
        } catch(e: Exception) {
            Timber.tag(TAG).d(e, "detectResultFromImage: ")
            showToast("Failed due to ${e.message}")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun extractbarcodeQrCodeInfo(barcodes: List<Barcode>) {
        if(barcodes.isEmpty()) {
            showToast("No barcode found")
        } else for (barcode in barcodes) {
            val bound = barcode.boundingBox
            val corners = barcode.cornerPoints
            val rawValue = barcode.rawValue
            Log.d(TAG,"extractbarcodeQrCodeInfo: rawValue: $rawValue")
            val valueType = barcode.valueType
            when(valueType) {
                Barcode.TYPE_WIFI -> {
                    val typeWifi = barcode.wifi
                    val ssid = "${typeWifi?.ssid}"
                    val password = "${typeWifi?.password}"
                    var encryptionType = "${typeWifi?.encryptionType}"
                    if (encryptionType == "1"){
                        encryptionType = "OPEN"
                    } else if (encryptionType == "2"){
                        encryptionType = "WPA"
                    } else if (encryptionType == "3"){
                        encryptionType = "WEP"
                    }
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_WIFI")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: ssid: $ssid")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: password:$password")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: encryptionType:$encryptionType")
                    binding.resultView.text = "TYPE_WIFI \n ssid: $ssid \npassword: $password\nencryptionType: $encryptionType" + "\n\nrawValue : $rawValue"
                }
                Barcode.TYPE_URL -> {
                    val typeUrl = barcode.url
                    val title = "${typeUrl?.title}"
                    val url = "${typeUrl?.url}"
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_URL")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: title: $title")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: url: $url")
                    binding.resultView.text = "TYPE_URL \ntitle: $title \nurl: $url \n\nrawValue: $rawValue"
                }
                Barcode.TYPE_EMAIL -> {
                    val typeEmail = barcode.email
                    val address = "${typeEmail?.address}"
                    val body = "${typeEmail?.body}"
                    val subject = "${typeEmail?.subject}"
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_EMAIL")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: address:$address")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: body: $body")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: subject:$subject")
                    binding.resultView.text = "TYPE_EMAIL \ntitle: $address \nurl: $body\nsubject: $subject \n\nrawValue : $rawValue"
                }
                Barcode.TYPE_CONTACT_INFO -> {
                    val typeContact = barcode.contactInfo
                    val title = "${typeContact?.title}"
                    val organisasi = "${typeContact?.organization}"
                    val name = "${typeContact?.name}"
                    val phone = "${typeContact?.phones}"
                    Log.d(TAG,"extractbarcodeQrCodeInfo: TYPE_CONTACT_INFO")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: Title: $title")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: Organization: $organisasi")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: nama: $name")
                    Log.d(TAG,"extractbarcodeQrCodeInfo: Phone: $phone")
                    binding.resultView.text = "TYPE_CONTACT_INFO " +
                            "\ntitle: $title " +
                            "\nnama: $name " +
                            "\norganization: $organisasi " +
                            "\nPhone : $phone" + "\n\nrawValue :$rawValue"
                }
                else -> {
                    if(barcode.rawValue!!.startsWith("app.jogjakost.qr.kost")) {
                        // get 2nd line from rawValue
                        val id: String = barcode.rawValue!!.split("\n")[1].trim()
                        val idLong: Long = id.toLong()

                        Timber.d("extractbarcodeQrCodeInfo: id: $id")
                        FancyToast.makeText(this, "Membuka kost dengan ID #$id", FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show()
                        val intent = Intent(this, ViewKostActivity::class.java)
                        intent.putExtra("id", idLong)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
        setLoadingScreen(false)
    }

    private fun setLoadingScreen(state: Boolean){
        if (state) {
            // fade in
            loader.alpha = 0f
            loader.visibility = View.VISIBLE
            loader.animate().alpha(1f).duration = 250

            // set flag to disable click
            loader.isClickable = true
        } else {
            // fade out
            loader.animate().alpha(0f).setDuration(250).withEndAction {
                loader.visibility = View.GONE
            }
            // set flag to enable click
            loader.isClickable = false
        }
    }
}