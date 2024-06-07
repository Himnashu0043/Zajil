package com.example.zajil.util

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class RadioOption(val text: String, var isSelected: Boolean)

class MyMarker(
    lat: Double,
    lng: Double,
    title: String,
    snippet: String
) : ClusterItem {

    private val position: LatLng
    private val title: String
    private val snippet: String

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return title
    }

    override fun getSnippet(): String {
        return snippet
    }

    override fun getZIndex(): Float {
        return 0f
    }

    init {
        position = LatLng(lat, lng)
        this.title = title
        this.snippet = snippet
    }
}

data class ErrorModel(val code: Int, val message: String)


// LoginResponse of login apis

data class LoginResponse(
    val activeUser: UserData,
    val code: Int,
    val message: String,
    val token: String
)

data class CommonResponse(
    val code: Int,
    val message: String
)


// response of status updation api


data class StatusUpdateResponse(
    val code: Int,
    val message: String,
    val userData: UserData
)

// help support response

data class HelpSupportResponse(
    val askedUser: AskedUser,
    val code: Int,
    val message: String
)

data class AskedUser(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val email: String,
    val phoneNumber: String,
    val updatedAt: String
)

data class RequestListResponse(
    val code: Int,
    val message: String,
    val result1: List<RideRequests>
)

data class RideRequests(
    val Status: String,
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val is_assign: Boolean,
    val location: RideLocation,
    val droplocation: RideLocation,
    val order: Order,
    val orderId: String,
    val quickServiceAddress: String,
    val riderArr: List<Any>,
    val updatedAt: String,
    val branch: String,
    val trackNumber: String,
    val weight: String,
    val user: User,
    val userId: String,
    val acceptedAt:String,
    val pickupAt:String,
    val wayToPickupAt:String,
    val outForDeliverAt:String,
    val deliverAt:String,
)

data class RideLocation(
    val coordinates: List<Double>,
    val type: String
)

data class Order(
    val _id: String,
    val awbnumber: String,
    val createdAt: String,
    val declaredvalue: String,
    val description: String,
    val device_id: String,
    val discountnumber: String,
    val district: String,
    val doctype: String,
    val invoicelink: String,
    val nobox: String,
    val oldprice: String,
    val payfortid: String,
    val paymenttype: String,
    val pdflink: String,
    val price: String,
    val promocode: String,
    val quickServiceStatus: Boolean,
    val rawsendercity: String,
    val recivercity: String,
    val recivername: String,
    val reciverphone: String,
    val sdk_token: String,
    val selecteddate: String,
    val selecteddaystring: String,
    val selectedtime: String,
    val sendercity: String,
    val senderemail: String,
    val sendername: String,
    val senderphone: String,
    val status: Int,
    val updatedAt: String,
    val user: String,
    val withextrabox: Boolean
)

data class User(
    val activationcode: Int,
    val company: String,
    val createdAt: String,
    val dialcode: String,
    val full_name: String,
    val idtxt: String,
    val lang: String,
    val lastlogin: String,
    val phone: String,
    val push: Push,
    val status: Int,
    val updatedAt: String
)

data class Push(
    val enabled: Boolean,
    val silent: Boolean
)

enum class RideStatus {
    PENDING,
    ACCEPT,
    WAY_TO_PICKUP,
    PICKUP,
    OUT_FOR_DELIVER,
    DELIVERED,
    CANCELLED
}

data class WalletResponse(
    val code: Int,
    val message: String,
    val result1: List<Transactions>,
    val walletMoney: Number,
    val totalRide: Number,
    val remainingBallance: Number
)

data class Transactions(
    val __v: Int,
    val _id: String,
    val amount: String?,
    val createdAt: String,
    val is_delete: Boolean,
    val order: List<Any>,
    val orderId: String,
    val status: String,
    val type: String,
    val updatedAt: String,
    val user: List<Any>,
    val userId: String,
    val checkoutStatus:String
)

data class UserResponse(
    val activeUser: UserData,
    val code: Int,
    val message: String
)

data class UserData(
    val __v: Int,
    val _id: String,
    val accountHolderName: String,
    val address: String,
    val approvedStatus: Boolean,
    val bankName: String,
    val city: String,
    val companyName: String,
    val createdAt: String,
    val deviceToken: String,
    val deviceType: String,
    val email: String,
    val ibanNumber: String,
    val id: String,
    val idImage: String,
    val is_assign: Boolean,
    val is_notification: Boolean,
    val is_online: Boolean,
    val jwtToken: String,
    val location: Location,
    val name: String,
    val password: String,
    val phoneNumber: String,
    val remainingBallance: Int,
    val requestArr: List<String>,
    val riderType: String,
    val rider_number: String,
    val totalRides: Int,
    val updatedAt: String,
    val userStatus: String,
    val walletMoney: Int
)

data class Location(
    val coordinates: List<Double>,
    val type: String
)

data class NotificationResponse(
    val code: Int,
    val message: String,
    val result1: List<Notification>
)

data class Notification(
    val __v: Int,
    val _id: String,
    val content: String,
    val createdAt: String,
    val is_delete: Boolean,
    val notiBy: String,
    val notiFor: String,
    val notiTo: String,
    val seen_status: Boolean,
    val status: String,
    val title: String,
    val trackNumber: String,
    val updatedAt: String
)