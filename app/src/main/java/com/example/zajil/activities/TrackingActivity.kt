package com.example.zajil.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.zajil.R
import com.example.zajil.databinding.ActivityTrackingBinding
import com.example.zajil.fragments.NotificationsFragment
import com.example.zajil.fragments.SelectLanguageFragment
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Constants
import com.example.zajil.util.MapTracking
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TrackingActivity : BaseActivity<ActivityTrackingBinding>(), View.OnClickListener,
    OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    override fun getLayout(): ActivityTrackingBinding {
        return ActivityTrackingBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Commons.setTransparentStatusBarOnly(this)
        binding.includedToolbar.ivMenu.setImageResource(R.drawable.back_white)
        binding.includedToolbar.ivMenu.setOnClickListener(this)
        binding.includedToolbar.ivLanguage.setOnClickListener(this)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        window.statusBarColor = getColor(R.color.primary)
        binding.includedToolbar.ivNotification.setOnClickListener {
            startActivity(Intent(this, CommonActivity::class.java).apply {
                putExtra(Constants.FRAGMENT_NAME, NotificationsFragment::class.java.simpleName)
            })
        }
    }

    override fun onDestroy() {
        window.statusBarColor = getColor(R.color.white)
        super.onDestroy()
    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.includedToolbar.ivMenu -> finish()
            binding.includedToolbar.ivLanguage -> {
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra(Constants.FRAGMENT_NAME, SelectLanguageFragment::class.java.simpleName)
                })
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        if (App.LAT != 0.0 && App.LONG != 0.0) {
            val myPos = LatLng(App.LAT, App.LONG)
            googleMap?.addMarker(
                MarkerOptions().position(myPos).icon(MapTracking.bitmapFromVector(R.drawable.car))
            )
            val cameraPosition =
                CameraPosition.Builder().target(myPos).zoom(MapTracking.zoomLevel).build()
            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }

    }


}