package com.example.zajil.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.zajil.BaseAdapter
import com.example.zajil.R
import com.example.zajil.adapters.ShipmentActionAdapter
import com.example.zajil.databinding.FragmentShipmentDetailsBinding
import com.example.zajil.databinding.ItemDropDownBinding
import com.example.zajil.databinding.PopupRecyclerBinding
import com.example.zajil.util.App
import com.example.zajil.util.Commons
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.getAddress
import com.example.zajil.util.Commons.getError
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.Commons.prettyCount
import com.example.zajil.util.Commons.showProgress
import com.example.zajil.util.Commons.showReasonDialog
import com.example.zajil.util.Commons.showToast
import com.example.zajil.util.Constants
import com.example.zajil.util.RideRequests
import com.example.zajil.util.RideStatus
import com.example.zajil.viewModel.NetworkViewModel
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.Success
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson


class ShipmentDetailFragment : BaseFragment<FragmentShipmentDetailsBinding>(), OnMapReadyCallback {


    private val viewModel: NetworkViewModel by lazy {
        ViewModelProvider(this)[NetworkViewModel::class.java]
    }

    private var googleMap: GoogleMap? = null
    private lateinit var adapter: ShipmentActionAdapter

    private var statusList = mutableListOf<StatusAction>()
    private var data: RideRequests? = null

    private val locationBroadcast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            /* if (intent != null) {
                 val latitude = intent.getDoubleExtra(Constants.LATITUDE, 0.0)
                 val longitude = intent.getDoubleExtra(Constants.LONGITUDE, 0.0)
                 val myPos = LatLng(latitude, longitude)
                 googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17F))
             }*/
        }
    }

    override fun getLayout(): FragmentShipmentDetailsBinding {
        return FragmentShipmentDetailsBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val dataString = it.getString(Constants.DATA, "") ?: ""
            Log.d("PreetiTesting", dataString)
            if (dataString.isNotEmpty()) {
                data = Gson().fromJson(dataString, RideRequests::class.java)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.shipmentDetailResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    if (it.response?.code?.isSuccessful() == true) {
                        if (it.response.result1.isNotEmpty()) {
                            data = it.response.result1[0]
                            setData()
                        }
                    } else it.response?.message?.showToast(binding)
                    dismissProgress()
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        viewModel.orderStatusResponse.observe(viewLifecycleOwner) {
            when (it) {
                is Success -> {
                    if (it.response?.code?.isSuccessful() == true) {
                        viewModel.getShipmentDetail(data?._id ?: "")
                    } else it.response?.message?.showToast(binding)
                    dismissProgress()
                }

                is Failure -> {
                    dismissProgress()
                    it.error?.getError(requireContext())?.message?.showToast(binding)
                }
            }
        }

        (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        adapter = ShipmentActionAdapter()
        binding.rvShipingActions.adapter = adapter

        binding.etEditText.setOnClickListener {
            showPopup()
        }


        binding.btnReject.setOnClickListener {
            requireContext().showReasonDialog()
        }

        /*binding.btnNavigate.setOnClickListener {
            startActivity(Intent(requireActivity(), TrackingActivity::class.java))
        }*/

        setData()

        binding.btnUpdate.setOnClickListener {
            showProgress(requireContext())
            viewModel.changeOrderStatus(data?._id ?: "", selectedStatus?.status?.name ?: "")
        }

        binding.btnNavigate.setOnClickListener {
            data?.let { order ->
                if (order.Status == RideStatus.ACCEPT.name || order.Status == RideStatus.WAY_TO_PICKUP.name) {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=${order.location.coordinates[1]},${order.location.coordinates[0]}")
                    )
                    startActivity(intent)
                } else {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?saddr=${order.location.coordinates[1]},${order.location.coordinates[0]}&daddr=${order.droplocation.coordinates[1]},${order.droplocation.coordinates[0]}")
                    )
                    startActivity(intent)
                }
            }
        }
    }


    enum class OrderStatus(val text: String) {
        ACCEPT("Accepted"),
        WAY_TO_PICKUP("On the way to pickup")
    }

    private fun getStatusData() {
        statusList.clear()
        statusList.add(StatusAction("Accepted", RideStatus.ACCEPT, time = data?.acceptedAt?:""))
        statusList.add(StatusAction("On the way to pickup", RideStatus.WAY_TO_PICKUP,time = data?.wayToPickupAt?:""))
        statusList.add(StatusAction("Pickup completed", RideStatus.PICKUP,time = data?.pickupAt?:""))
        statusList.add(StatusAction("Shipment out for delivery", RideStatus.OUT_FOR_DELIVER,time = data?.outForDeliverAt?:""))
        statusList.add(StatusAction("Delivered", RideStatus.DELIVERED,time = data?.deliverAt?:""))

        binding.btnNavigate.visibility = View.VISIBLE
        binding.llUpdateSection.visibility = View.VISIBLE

        when (status) {
            RideStatus.PENDING.name -> {
                binding.etEditText.text = ""
                statusList[0].isEnabled = true
                selectedStatus = statusList[0]
            }

            RideStatus.ACCEPT.name -> {
                binding.etEditText.text = "On the way to pickup"
                statusList[1].isEnabled = true
                selectedStatus = statusList[1]
                statusList[1].time = data?.acceptedAt?:""
            }

            RideStatus.WAY_TO_PICKUP.name -> {
                binding.etEditText.text = "Pickup complete"
                statusList[2].isEnabled = true
                selectedStatus = statusList[2]
                statusList[2].time = data?.wayToPickupAt?:""
            }

            RideStatus.PICKUP.name -> {
                binding.etEditText.text = "Shipment out for delivery"
                statusList[3].isEnabled = true
                selectedStatus = statusList[3]
                statusList[3].time = data?.pickupAt?:""
            }

            RideStatus.OUT_FOR_DELIVER.name -> {
                binding.etEditText.text = "Delivered"
                statusList[4].isEnabled = true
                selectedStatus = statusList[4]
                statusList[4].time = data?.outForDeliverAt?:""
            }

            RideStatus.DELIVERED.name -> {
                binding.btnNavigate.visibility = View.GONE
                binding.llUpdateSection.visibility = View.GONE
//                statusList[4].time = data?.outForDeliverAt?:""
            }

        }

        adapter.clear()
        for (item in statusList) {
            if (item.isEnabled) {
                break
            } else {
                adapter.add(item)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setData() {
        data?.let {

            if (it.Status == RideStatus.ACCEPT.name || it.Status == RideStatus.WAY_TO_PICKUP.name) {
                binding.btnNavigate.text =
                    getString(
                        R.string.navigate_to_branch
                    )
            } else {
                binding.btnNavigate.text =
                    getString(
                        R.string.navigate_to_receiver
                    )
            }


//            binding.receiverDetails.ivReceiverProfile.loadCircleCrop
            binding.receiverDetails.tvReceiverName.text = it.user.full_name
            binding.receiverDetails.tvPhone.text = it.user.phone
            binding.packageDetail.tvWeightValue.text = "${it.weight} kg"

            binding.rideDetail.tvRideId.text = "#${it.trackNumber}"

            binding.rideDetail.tvRideToAddress.context.getAddress(
                it.droplocation.coordinates[1],
                it.droplocation.coordinates[0]
            ) {
                Log.d("AddressDataAdapter", "rideToAddress:1 $it")
                binding.rideDetail.tvRideToAddress.text = it
            }

            binding.rideDetail.tvRideFromAddress.context.getAddress(
                it.location.coordinates[1],
                it.location.coordinates[0]
            ) {
                Log.d("AddressDataAdapter", "rideFromAddress:1 $it")
                binding.rideDetail.tvRideFromAddress.text = it
            }
            binding.rideDetail.tvRideFromDistance.text =
                Commons.getDistance(
                    LatLng(App.LAT, App.LONG),
                    LatLng(it.location.coordinates[1], it.location.coordinates[0])
                ).prettyCount() + " away"
            binding.rideDetail.tvRideFrom.text = it.branch
            binding.rideDetail.tvRideTo.text = it.quickServiceAddress

            binding.rideDetail.tvRideToDistance.text =
                Commons.getDistance(
                    LatLng(it.location.coordinates[1], it.location.coordinates[0]),
                    LatLng(it.droplocation.coordinates[1], it.droplocation.coordinates[0])
                ).prettyCount() + " away"
            status = it.Status
            getStatusData()
        }
    }

    private var status: String = ""
    private var dropDownPopup: PopupWindow? = null
    private lateinit var popupAdapter: StatusAdapter
    private var selectedStatus: StatusAction? = null

    private fun showPopup() {
        if (dropDownPopup == null) {
            val popupBinding = PopupRecyclerBinding.inflate(layoutInflater)
            popupAdapter = StatusAdapter {
                if (it?.isEnabled == true) {
                    binding.etEditText.text = it.text
                    selectedStatus = it
                }
                dropDownPopup?.dismiss()
            }
            popupBinding.rvRecycler.adapter = popupAdapter
            dropDownPopup = PopupWindow(
                popupBinding.root,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                380
            )
            dropDownPopup?.isOutsideTouchable = true
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                dropDownPopup?.isTouchModal = true
//            }
        }
        getStatusData()
        popupAdapter.set(statusList)
        dropDownPopup?.showAsDropDown(binding.etEditText)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

//        googleMap?.uiSettings?.isZoomGesturesEnabled = false
//        googleMap?.uiSettings?.isScrollGesturesEnabled = false
//        googleMap?.uiSettings?.isMyLocationButtonEnabled = false

        data?.let {
            val myPos = LatLng(it.location.coordinates[1], it.location.coordinates[0])
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(myPos, 17F))
        }
    }

    class StatusAction(
        val text: String,
        val status: RideStatus,
        var isEnabled: Boolean = false,
        var time: String = ""
    ) {
        override fun toString(): String {
            return text
        }
    }

    class StatusAdapter(private val handle: (StatusAction?) -> Unit) : BaseAdapter<StatusAction>() {

        inner class MyViewHolder(val binding: ItemDropDownBinding) :
            RecyclerView.ViewHolder(binding.root) {
            init {
                binding.root.setOnClickListener {
                    handle.invoke(get(adapterPosition))
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return MyViewHolder(
                ItemDropDownBinding.inflate(
                    LayoutInflater.from(
                        parent.context
                    ), parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as MyViewHolder).apply {
                get(position)?.let {
                    if (it.isEnabled) {
                        binding.tvItem.alpha = 1F
                    } else {
                        binding.tvItem.alpha = 0.5F
                    }
                    binding.tvItem.isEnabled = it.isEnabled
                    binding.tvItem.text = it.text
                }
            }
        }


    }

}