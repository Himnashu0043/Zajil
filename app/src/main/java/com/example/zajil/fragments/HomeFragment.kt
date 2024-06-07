package com.example.zajil.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.zajil.R
import com.example.zajil.activities.HomeActivity
import com.example.zajil.databinding.DialogCongratulationBinding
import com.example.zajil.databinding.FragmentHomeBinding
import com.example.zajil.services.LocationService
import com.example.zajil.util.App
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getAddress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isLocationEnabled
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.util.MapTracking
import com.example.zajil.util.MapTracking.animateView
import com.example.zajil.util.MapTracking.getCarMarker
import com.example.zajil.util.MyMarker
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer


class HomeFragment : BaseFragment<FragmentHomeBinding>(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private var dialog: Dialog? = null
    private var gpsDialog: Dialog? = null

    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    private val locationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent != null) {
                val latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
                App.preferenceManager.LATITUDE = latitude
                App.preferenceManager.LONGITUDE = longitude
                App.LAT = latitude
                App.LONG = longitude
                val myPos = LatLng(latitude, longitude)
                googleMap?.animateView(myPos)
                MapTracking.movingMarker?.remove()
                MapTracking.movingMarker = googleMap?.getCarMarker(myPos)
                try {
                    requireContext().getAddress(latitude, longitude) {
                        requireActivity().runOnUiThread {
                            (requireActivity() as HomeActivity).binding.includedLayout.tvAddress.text =
                                it
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getLayout() = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        binding.includedOffline.switchOnlineOffline.setOnCheckedChangeListener { _, b ->
            viewModel.setStatus(b, App.LAT, App.LONG)
        }

        checkLocationPermission()

        viewModel.userDetailResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    if (it.response?.code?.isSuccessful() == true) {
                        App.preferenceManager.user = it.response.activeUser
                        binding.includedOffline.switchOnlineOffline.isChecked =
                            App.preferenceManager.user?.is_online ?: false
                    } else it.response?.message?.showToast(binding)
                }

                is Failure -> {
                    dismissProgress()
                }
            }
        }

        viewModel.setStatusResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    dismissProgress()
                    it.response?.userData?.let { user ->
                        App.preferenceManager.user = user
                        setStatus(user.is_online)
                    }
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        binding.cvMyLocation.setOnClickListener {
            jumpToCurrentPosition()
        }

        binding.includedOffline.switchOnlineOffline.isChecked =
            App.preferenceManager.user?.is_online ?: false

    }

    override fun onResume() {
        super.onResume()
        viewModel.getUserDetail()
    }


    private fun setStatus(status: Boolean) {
        if (status) {
            binding.includedOffline.tvOnlineOffline.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.switchStrokeSelected
                )
            )
            binding.includedOffline.tvOnlineOffline.text = getString(R.string.you_are_online)
            binding.includedOffline.tvDescription.text =
                getString(R.string.you_are_ready_to_start_accepting_jobs)
        } else {
            binding.includedOffline.tvOnlineOffline.setTextColor(
                ContextCompat.getColor(
                    requireContext(), R.color.textBlack
                )
            )
            binding.includedOffline.tvOnlineOffline.text = getString(R.string.you_are_offline)
            binding.includedOffline.tvDescription.text =
                getString(R.string.go_online_to_start_accepting_jobs)
        }
    }

    @SuppressLint("MissingPermission", "UnspecifiedRegisterReceiverFlag")
    private fun requestLocationUpdates() {
        requireActivity().registerReceiver(
            locationBroadcast, IntentFilter(Constants.LOCATION_BROADCAST)
        )
        val serviceIntent = Intent(
            requireActivity(), LocationService::class.java
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) requireActivity().startForegroundService(
            serviceIntent
        )
        else requireActivity().startService(serviceIntent)

        binding.includedOffline.switchOnlineOffline.isChecked =
            App.preferenceManager.user?.is_online ?: false
    }

    private fun requestPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.POST_NOTIFICATIONS
            )
        )
    }

    private fun showPermissionRationaleDialog() {
        dialog?.dismiss()
        dialog = Dialog(requireContext(), R.style.dialog_style).apply {
            DialogCongratulationBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setDimAmount(0.8F)
                it.tvCongratulation.text = "Permissions are required"
                it.tvDescription.text = "This app needs location permission enabled."
                it.tvOkay.text = "Allow"
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                it.tvOkay.setOnClickListener {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", requireActivity().packageName, null)
                    )
                    permissionSettingLauncher.launch(intent)
                }
                show()
            }
        }
    }

    private fun showGPSRationaleDialog() {
        gpsDialog?.dismiss()
        gpsDialog = Dialog(requireContext(), R.style.dialog_style).apply {
            DialogCongratulationBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setDimAmount(0.8F)
                it.tvCongratulation.text = "GPS required"
                it.tvDescription.text = "This app needs GPS to be enabled."
                it.tvOkay.text = "Enable GPS"
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                it.tvOkay.setOnClickListener {
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    permissionSettingLauncher.launch(intent)
                }
                show()
            }
        }
    }

    private fun checkLocationPermission() {
        val coarsePermissionStatus = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
        )
        val finePermissionStatus = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        )

        val rationalePermission = ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
        )

        when {
            // if permissions are granted
            coarsePermissionStatus == PackageManager.PERMISSION_GRANTED && finePermissionStatus == PackageManager.PERMISSION_GRANTED ->
                if (isLocationEnabled(requireContext()))
                    requestLocationUpdates()
                else
                    showGPSRationaleDialog()
            // if need to show permission info then user will manually grant the permissions
            rationalePermission -> showPermissionRationaleDialog()
            // request for permissions
            else -> requestPermissions()
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_COARSE_LOCATION] == true && it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                requestLocationUpdates()
            } else checkLocationPermission()
        }

    private val permissionSettingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            checkLocationPermission()
        }


    private fun jumpToCurrentPosition() {
        if (App.LAT != 0.0 && App.LONG != 0.0) {
            val myPos = LatLng(App.LAT, App.LONG)
            googleMap?.animateView(myPos)
            MapTracking.movingMarker?.remove()
            MapTracking.movingMarker = googleMap?.getCarMarker(myPos)

        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        jumpToCurrentPosition()
//        setUpClusterer()
    }

    // Declare a variable for the cluster manager.
    private lateinit var clusterManager: ClusterManager<MyMarker>

    @SuppressLint("PotentialBehaviorOverride")
    private fun setUpClusterer() {
        clusterManager = ClusterManager(context, googleMap)
        val customRenderer = CustomRenderer(requireContext(), googleMap, clusterManager)
        clusterManager.renderer = customRenderer
        clusterManager.setAnimation(false)
        googleMap?.setOnCameraIdleListener(clusterManager)
        googleMap?.setOnMarkerClickListener(clusterManager)
        addItems()
    }

    private fun addItems() {

        // Set some lat/lng coordinates to start with.
        var lat = 51.5145160
        var lng = -0.1270060

        // Add ten cluster items in close proximity, for purposes of this example.
        for (i in 0..1200) {
            val offset = i / 100000.0
            lat += offset
            lng += offset
            val offsetItem =
                MyMarker(lat, lng, "Title $i", "Snippet $i")
            clusterManager.addItem(offsetItem)
        }
    }


    class CustomRenderer(context: Context, map: GoogleMap?, manager: ClusterManager<MyMarker>) :
        DefaultClusterRenderer<MyMarker>(context, map, manager) {

        var shouldCluster = false

        companion object {
            private const val MIN_CLUSTER_SIZE = 1
        }


        override fun shouldRenderAsCluster(cluster: Cluster<MyMarker>): Boolean {
            /*return if (shouldCluster) {
                cluster.size > MIN_CLUSTER_SIZE;
            } else {
                shouldCluster;
            }*/
            return shouldCluster
        }

    }


}