package com.example.zajil.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.zajil.fragments.ShipmentDetailFragment
import com.example.zajil.util.App
import com.example.zajil.util.CommonResponse
import com.example.zajil.util.Commons.dismissProgress
import com.example.zajil.util.Commons.isSuccessful
import com.example.zajil.util.HelpSupportResponse
import com.example.zajil.util.LoginResponse
import com.example.zajil.util.NotificationResponse
import com.example.zajil.util.RequestListResponse
import com.example.zajil.util.StatusUpdateResponse
import com.example.zajil.util.UserResponse
import com.example.zajil.util.WalletResponse
import com.example.zajil.webservices.ApiResponse
import com.example.zajil.webservices.Failure
import com.example.zajil.webservices.RestClient
import com.example.zajil.webservices.Success
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class NetworkViewModel : ViewModel() {

    val loginResponse = MutableLiveData<ApiResponse<LoginResponse>>()
    val setPasswordResponse = MutableLiveData<ApiResponse<CommonResponse>>()
    val checkResponse = MutableLiveData<ApiResponse<CommonResponse>>()
    val setStatusResponse = MutableLiveData<ApiResponse<StatusUpdateResponse>>()
    val changePasswordResponse = MutableLiveData<ApiResponse<CommonResponse>>()
    val pushNotificationStatusResponse = MutableLiveData<ApiResponse<CommonResponse>>()
    val helpSupportResponse = MutableLiveData<ApiResponse<HelpSupportResponse>>()
    val requestListResponse = MutableLiveData<ApiResponse<RequestListResponse>>()
    val requestAcceptRejectResponse = MutableLiveData<ApiResponse<CommonResponse>>()

    val getActiveOrderResponse = MutableLiveData<ApiResponse<RequestListResponse>>()
    val getCompletedOrderResponse = MutableLiveData<ApiResponse<RequestListResponse>>()
    val orderStatusResponse = MutableLiveData<ApiResponse<CommonResponse>>()

    val shipmentDetailResponse = MutableLiveData<ApiResponse<RequestListResponse>>()

    val walletTransactionResponse = MutableLiveData<ApiResponse<WalletResponse>>()

    val userDetailResponse = MutableLiveData<ApiResponse<UserResponse>>()

    val notificationListResponse = MutableLiveData<ApiResponse<NotificationResponse>>()

    val withdrawMoneyResponse = MutableLiveData<ApiResponse<CommonResponse>>()


    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.d("ApiException", throwable.message ?: "")
        dismissProgress()
    }

    fun login(phoneNumber: String, password: String) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["phoneNumber"] = phoneNumber
        hashMap["password"] = password
        hashMap["deviceToken"] = App.preferenceManager.deviceToken
        hashMap["deviceType"] = "ANDROID"
        viewModelScope.launch(exceptionHandler) {
            val response = RestClient.getApi().login(hashMap)
            if (response.isSuccessful) loginResponse.postValue(Success(response.body()))
            else loginResponse.postValue(Failure(response.errorBody()))
        }
    }

    fun checkUser(phone: String) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["phoneNumber"] = phone
        viewModelScope.launch(exceptionHandler) {
            val response = RestClient.getApi().checkUser(hashMap)
            if (response.isSuccessful) checkResponse.postValue(Success(response.body()))
            else checkResponse.postValue(Failure(response.errorBody()))
        }
    }

    fun setPassword(phoneNumber: String, password: String) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["phoneNumber"] = phoneNumber
        hashMap["password"] = password
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().setPassword(hashMap)
            if (res.isSuccessful) setPasswordResponse.postValue(Success(res.body()))
            else {
                setPasswordResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun setStatus(isOnline: Boolean, lat: Double, long: Double) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["is_online"] = isOnline
//        hashMap["lat"] = lat
//        hashMap["long"] = long
        hashMap["lat"] = 18.209678
        hashMap["long"] = 42.514173
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().statusUpdated(hashMap)
            if (res.isSuccessful) setStatusResponse.postValue(Success(res.body()))
            else {
                setStatusResponse.postValue(Failure(res.errorBody()))
            }
        }
    }


    fun changePassword(oldPassword: String, newPassword: String) {
        val hashMap = hashMapOf<String, Any>()
        hashMap["oldPassword"] = oldPassword
        hashMap["newPassword"] = newPassword
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().changePassword(hashMap)
            if (res.isSuccessful) changePasswordResponse.postValue(Success(res.body()))
            else {
                changePasswordResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun getHelpSupportContact() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getHelpSupport()
            if (res.isSuccessful) helpSupportResponse.postValue(Success(res.body()))
            else {
                helpSupportResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun pushNotificationStatus(isNotification: Boolean) {
        val hashMap = hashMapOf<String, Any>("is_notification" to isNotification)
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().pushNotificationStatus(hashMap)
            if (res.isSuccessful) pushNotificationStatusResponse.postValue(Success(res.body()))
            else {
                pushNotificationStatusResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun getRequestList() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getRequestList()
            if (res.isSuccessful) requestListResponse.postValue(Success(res.body()))
            else {
                requestListResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun acceptRejectRequest(id: String, accept: Boolean) {
        viewModelScope.launch(exceptionHandler) {
            val hashMap = hashMapOf<String, Any>()
            hashMap["_id"] = id
            hashMap["status"] = if (accept) "ACCEPT" else "REJECT"
            val res = RestClient.getApi().requestAcceptReject(hashMap)
            if (res.isSuccessful) requestAcceptRejectResponse.postValue(Success(res.body()))
            else {
                requestAcceptRejectResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun acceptRejectRequest(
        id: String,
        accept: Boolean,
        rejectReason: String = "",
        handle: () -> Unit = {}
    ) {
        viewModelScope.launch(exceptionHandler) {
            val hashMap = hashMapOf<String, Any>()
            hashMap["_id"] = id
            hashMap["status"] = if (accept) "ACCEPT" else "REJECT"
            if (rejectReason.isNotEmpty()) hashMap["rejectReason"] = rejectReason
            val res = RestClient.getApi().requestAcceptReject(hashMap)
            if (res.isSuccessful) handle.invoke()
            else {
                dismissProgress()
            }
        }
    }


    fun getActiveOrders() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getActiveOrders()
            if (res.isSuccessful) getActiveOrderResponse.postValue(Success(res.body()))
            else {
                getActiveOrderResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun getCompletedOrders() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getCompletedOrders()
            if (res.isSuccessful) getCompletedOrderResponse.postValue(Success(res.body()))
            else {
                getCompletedOrderResponse.postValue(Failure(res.errorBody()))
            }
        }
    }


    fun getShipmentDetail(orderId: String) {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getShipmentDetail(orderId)
            if (res.isSuccessful)
                shipmentDetailResponse.postValue(Success(res.body()))
            else {
                shipmentDetailResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun changeOrderStatus(orderId: String, status: String) {
        viewModelScope.launch(exceptionHandler) {
            val hashMap = hashMapOf<String, Any>()
            hashMap["_id"] = orderId
            hashMap["status"] = status
            val res = RestClient.getApi().orderStatus(hashMap)
            if (res.isSuccessful)
                orderStatusResponse.postValue(Success(res.body()))
            else orderStatusResponse.postValue(Failure(res.errorBody()))
            /*if (res.isSuccessful) handle.invoke()
            else {
                dismissProgress()
            }*/
        }
    }

    fun getWalletTransaction() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().walletTransactions()
            if (res.isSuccessful)
                walletTransactionResponse.postValue(Success(res.body()))
            else {
                walletTransactionResponse.postValue(Failure(res.errorBody()))
            }
        }
    }

    fun getUserDetail() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getUserDetail()
            if (res.isSuccessful) {
                if (res.body()?.code?.isSuccessful() == true)
                    App.preferenceManager.user = res.body()?.activeUser
                userDetailResponse.postValue(Success(res.body()))
            } else userDetailResponse.postValue(Failure(res.errorBody()))
        }
    }

    fun logout(handle: () -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().logout()
            if (res.isSuccessful)
                handle()
            else dismissProgress()
        }
    }

    fun getNotification() {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().getNotifications()
            if (res.isSuccessful)
                notificationListResponse.postValue(Success(res.body()))
            else notificationListResponse.postValue(Failure(res.errorBody()))
        }
    }

    fun withdrawWallet(walletMoney: Number) {
        viewModelScope.launch(exceptionHandler) {
            val res = RestClient.getApi().withdrawMoney(hashMapOf("amount" to walletMoney))
            if (res.isSuccessful)
                withdrawMoneyResponse.postValue(Success(res.body()))
            else withdrawMoneyResponse.postValue(Failure(res.errorBody()))
        }
    }
}