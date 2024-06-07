package com.example.zajil.webservices

import com.example.zajil.util.CommonResponse
import com.example.zajil.util.HelpSupportResponse
import com.example.zajil.util.LoginResponse
import com.example.zajil.util.NotificationResponse
import com.example.zajil.util.RequestListResponse
import com.example.zajil.util.StatusUpdateResponse
import com.example.zajil.util.UserResponse
import com.example.zajil.util.WalletResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface Api {

    @POST("user/rider/login")
    suspend fun login(@Body hashMap: HashMap<String, Any>): Response<LoginResponse>

    @PUT("user/rider/setPassword")
    suspend fun setPassword(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

    @POST("user/rider/checkUser")
    suspend fun checkUser(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

    @PUT("user/rider/statusUpdate")
    suspend fun statusUpdated(@Body hashMap: HashMap<String, Any>): Response<StatusUpdateResponse>

    @PUT("user/rider/driverNotification")
    suspend fun pushNotificationStatus(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

    @PUT("user/rider/changePassword")
    suspend fun changePassword(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

    @GET("user/rider/HelpSupportContact")
    suspend fun getHelpSupport(): Response<HelpSupportResponse>

    @GET("user/rider/requestList")
    suspend fun getRequestList(): Response<RequestListResponse>

    @PUT("user/rider/action")
    suspend fun requestAcceptReject(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

    @GET("user/rider/myRequestList")
    suspend fun getActiveOrders(@Query("status") status: String = "In_progress"): Response<RequestListResponse>

    @GET("user/rider/myRequestList")
    suspend fun getCompletedOrders(@Query("status") status: String = "Delivered"): Response<RequestListResponse>

    @GET("user/rider/myRequestDetail")
    suspend fun getShipmentDetail(@Query("_id") orderId: String): Response<RequestListResponse>

    @PUT("user/rider/orderStatus")
    suspend fun orderStatus(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

    @GET("user/rider/myWalletTransaction")
    suspend fun walletTransactions(): Response<WalletResponse>

    @GET("user/rider/viewProfile")
    suspend fun getUserDetail(): Response<UserResponse>

    @GET("user/rider/logout")
    suspend fun logout(): Response<CommonResponse>

    @GET("user/rider/notilist")
    suspend fun getNotifications(): Response<NotificationResponse>

    @POST("user/rider/cashout")
    suspend fun withdrawMoney(@Body hashMap: HashMap<String, Any>): Response<CommonResponse>

}
