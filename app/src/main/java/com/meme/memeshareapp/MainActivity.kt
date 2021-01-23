package com.meme.memeshareapp

import android.content.Intent
import android.content.Intent.*
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.*
//import com.facebook.ads.Ad
//import com.facebook.ads.AudienceNetworkAds
//import com.facebook.ads.InterstitialAdListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

//import com.facebook.ads.*;
//import com.facebook.ads.AdError
//import com.facebook.ads.InterstitialAd

class MainActivity : AppCompatActivity() {

    var currentImageUrl: String? =null
    private lateinit var mInterstitialAd: InterstitialAd
    var nextcount=0
    var sharecount=0
    private val TAG="Nilesh"
    //private var interstitialAd: InterstitialAd? = null
    //private var interstitialAdListener: InterstitialAdListener?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Facebook Audience Network Code
        /*AudienceNetworkAds.initialize(this)
        interstitialAd = InterstitialAd(this, "151790683019490_151791473019411")
        interstitialAdListener = object : InterstitialAdListener {
            override fun onInterstitialDisplayed(ad: Ad?) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.")
            }

            override fun onInterstitialDismissed(ad: Ad?) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.")
            }

            override fun onError(ad: Ad?, adError: AdError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage())
            }

            override fun onAdLoaded(ad: Ad?) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!")
                // Show the ad
                interstitialAd!!.show()
            }

            override fun onAdClicked(ad: Ad?) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!")
            }

            override fun onLoggingImpression(ad: Ad?) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!")
            }
        }
        interstitialAd!!.loadAd(
            interstitialAd!!.buildLoadAdConfig()
                .withAdListener(interstitialAdListener)
                .build())*/


        // Admob Code
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("6CE7341ADF01E2F45DA3A77C31A2B13A")).build()
        MobileAds.setRequestConfiguration(configuration)
        MobileAds.initialize(this)
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-6611402119785086/6701291485"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.d(TAG,"onAdLoaded")
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Code to be executed when an ad request fails.
                Log.d(TAG,"onAdFailed"+ adError.toString())
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
                Log.d(TAG,"onAdOpened")
            }

            override fun onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                Log.d(TAG,"onAdClicked")
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
                Log.d(TAG,"onAdLeftApplication")
            }
            override fun onAdClosed() {
                //super.onAdClosed()
                Log.d(TAG,"onAdClosed")
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        loadMeme()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.shareBt -> appShare()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadMeme(){
        // Instantiate the RequestQueue.
        progressBar_id.visibility=View.VISIBLE
        val url = "https://meme-api.herokuapp.com/gimme"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                currentImageUrl= response.getString("url")
                Glide.with(this).load(currentImageUrl).listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar_id.visibility=View.GONE
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        progressBar_id.visibility=View.GONE
                        return false
                    }
                }).into(imageView)
            },
            Response.ErrorListener { })

// Add the request to the RequestQueue.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }
    fun shareMeme(view: View) {
        if(sharecount % 3==0) {
            if (mInterstitialAd.isLoaded){
                mInterstitialAd.show()
            }
        }
        var intent= Intent(ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(EXTRA_TEXT,"Hey!! Checkout this cool meme I got from MemeShare App $currentImageUrl")
        val chooser= createChooser(intent,"Share this meme using...")
        startActivity(chooser)
        sharecount++
    }
    fun nextMeme(view: View) {
        if(nextcount % 4==0) {
            if (mInterstitialAd.isLoaded){
                mInterstitialAd.show()
            }
        }
        loadMeme()
        nextcount++
    }

    fun appShare(){
        var intent= Intent(ACTION_SEND)
        intent.type="text/plain"
        val shareMessage="https://play.google.com/store/apps/details?id="+BuildConfig.APPLICATION_ID+"\n\n"
        intent.putExtra(EXTRA_TEXT,shareMessage)
        val chooser= createChooser(intent,"Share this App using...")
        startActivity(chooser)
    }

//    override fun onDestroy() {
//        interstitialAd?.destroy()
//        super.onDestroy()
//    }                                     for facebook

}