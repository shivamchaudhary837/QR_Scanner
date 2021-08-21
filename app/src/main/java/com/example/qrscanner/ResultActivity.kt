package com.example.qrscanner

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.qrscanner.databinding.ActivityResultBinding
import com.google.android.gms.ads.*
import timber.log.Timber

class ResultActivity : AppCompatActivity() , View.OnClickListener {
    private lateinit var binding:ActivityResultBinding
    private var qrCode:String?=null
    lateinit var mAdView : AdView
    lateinit var mAdView2:AdView
    lateinit var mAdView3:AdView

   private var mTryAgain=true
    private var mTryAgain2=true
    private var mTryAgain3=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
         val actionBar=supportActionBar
         val colorDrawable=ColorDrawable(getColor(R.color.all))
        actionBar?.setBackgroundDrawable(colorDrawable)
        try {
            MobileAds.initialize(this) {}
            mAdView = findViewById(R.id.adView)
            val adRequest = AdRequest.Builder().build()
            mAdView.loadAd(adRequest)

            mAdView2 = findViewById(R.id.adView2)
            val adRequest2 = AdRequest.Builder().build()
            mAdView2.loadAd(adRequest2)

            mAdView3 = findViewById(R.id.adView3)
            val adRequest3 = AdRequest.Builder().build()
            mAdView3.loadAd(adRequest3)

            callAdListener()

            qrCode = intent.getStringExtra("qrcode")
            if (qrCode != null) {
                binding.resultTv.text = qrCode
            }


            binding.shareBtn.setOnClickListener(this)
            binding.openBtn.setOnClickListener(this)
            binding.copyBtn.setOnClickListener(this)

            binding.resultTv.setOnLongClickListener { view ->
                val clipBoard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(null, qrCode)
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(this, getString(R.string.copied_to_clipboard), Toast.LENGTH_LONG)
                    .show()
                true
            }
        }
        catch (e:Exception){
            Timber.e("ResultActivity",e.message)
            Toast.makeText(this,"Some error Occur,Restart the app",Toast.LENGTH_LONG).show()
        }
    }

    private fun callAdListener() {

        mAdView.adListener = object: AdListener() {


            override fun onAdFailedToLoad(adError : LoadAdError) {
                // Code to be executed when an ad request fails.
                super.onAdFailedToLoad(adError)
                if(mTryAgain){
                    mAdView.loadAd(AdRequest.Builder().build())
                    mTryAgain=false
                }

            }
        }

        mAdView2.adListener= object :AdListener(){


            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)

                if(mTryAgain2){
                    mAdView2.loadAd(AdRequest.Builder().build())
                    mTryAgain2=false
                }
            }

        }

        mAdView3.adListener= object :AdListener(){


            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)

                if(mTryAgain3){
                    mAdView3.loadAd(AdRequest.Builder().build())
                    mTryAgain3=false
                }
            }

        }

    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onClick(view: View?) {

        when(view){
            binding.shareBtn->{
               val sendIntent= Intent().apply{
                   action=Intent.ACTION_SEND
                   putExtra(Intent.EXTRA_TEXT,qrCode)
                   type="text/plain"
               }
                val shareIntent=Intent.createChooser(sendIntent,"Qr Scanner App")
                startActivity(shareIntent)
            }
            binding.openBtn->{
               val sendIntent=Intent().apply {
                   action=Intent.ACTION_WEB_SEARCH
                   putExtra(SearchManager.QUERY,qrCode)
               }
                if(sendIntent.resolveActivity(packageManager)!=null)
                startActivity(sendIntent)
            }
            binding.copyBtn->{
                val clipBoard=getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip= ClipData.newPlainText(null,qrCode)
                clipBoard.setPrimaryClip(clip)
                Toast.makeText(this,getString(R.string.copied_to_clipboard),Toast.LENGTH_LONG).show()
            }
        }
    }

}