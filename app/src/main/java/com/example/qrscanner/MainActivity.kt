package com.example.qrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.example.qrscanner.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.Result
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var codeScanner:CodeScanner

    companion object{
        const val CAMERA_PERMISSION_CODE=143
    }
    private var cameraPermission=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar=supportActionBar
        val colorDrawable=ColorDrawable(getColor(R.color.all))
        actionBar?.setBackgroundDrawable(colorDrawable)

        codeScanner = CodeScanner(this,binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        //codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)

        codeScanner.decodeCallback= DecodeCallback {result->
            runOnUiThread{
              val qrCode=result.text.toString()

                if(qrCode.isNotEmpty()){
                    startActivity(Intent(this,ResultActivity::class.java).apply {
                        putExtra("qrcode",qrCode)
                    })

                }
            }
        }

        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Timber.e("Camera initialization error: " + it.message)
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

    }


    fun requestCameraPermission(){

        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            val snack=Snackbar.make(binding.scannerView,
                "We need your permission to access camera."+"Please give the permission"
            ,Snackbar.LENGTH_INDEFINITE)

            snack.setAction("OK",View.OnClickListener {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE)
            })
            snack.show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CAMERA_PERMISSION_CODE->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    cameraPermission=true
                }
                else{
                    requestCameraPermission()
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()

        if(cameraPermission)
        codeScanner.startPreview()
    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
    }
}


