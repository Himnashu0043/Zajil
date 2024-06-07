package com.example.zajil.util

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.example.zajil.R
import com.example.zajil.activities.SplashActivity
import com.example.zajil.adapters.RadioAdapter
import com.example.zajil.databinding.DialogChooseMediaOptionBinding
import com.example.zajil.databinding.DialogCongratulationBinding
import com.example.zajil.databinding.DialogLogoutBinding
import com.example.zajil.databinding.DialogRejectedReasonsBinding
import com.example.zajil.databinding.ProgressDialogBinding
import com.example.zajil.services.LocationService
import com.example.zajil.webservices.RestClient
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import okhttp3.ResponseBody
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.absoluteValue


object Commons {


    fun Map<String, Any>.printData() {
        for (key in keys)
            Log.d("BUNDLE_DATA", "key : $key , value : ${get(key)}")
    }

    fun Bundle.printData() {
        for (key in keySet())
            Log.d("BUNDLE_DATA", "key : $key , value : ${get(key)}")
    }

    /*fun ResponseBody.checkError() {
        getError()?.let {
            if (it.message.equals("Unauthorized access",true)) {
                App.preferenceManager.logout()
                App.instance.baseContext.startActivity(Intent(App.instance.baseContext,SplashActivity::class.java).apply {
                    putExtra("fromLogout",true)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK and Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }*/

    fun String.toDateFormat(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val date = sdf.parse(this)
//        date.time += ((5*60*60)+(30*60))*1000
        SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault()).let {
            return it.format(date) ?: ""
        }
        return ""
    }

    fun String.toDateFormatStatus(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val date = sdf.parse(this)
//        date.time += ((5*60*60)+(30*60))*1000
        SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault()).let {
            return it.format(date) ?: ""
        }
        return ""
    }


    fun String.toNotificationDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val date = sdf.parse(this)
//        date.time += ((5*60*60)+(30*60))*1000
        SimpleDateFormat("dd/MM/yyyy")?.let {
            return it.format(date) ?: ""
        }
        return ""
    }


    private fun Address.extractAddress(): String {
        val addressBuilder = StringBuilder()
        if (!subLocality.isNullOrBlank())
            addressBuilder.append(subLocality)
        if (!locality.isNullOrBlank()) {
            if (addressBuilder.isNotBlank())
                addressBuilder.append(",${locality}")
            else addressBuilder.append(locality)
        }
        if (!adminArea.isNullOrBlank()) {
            if (addressBuilder.isNotBlank())
                addressBuilder.append(",${adminArea}")
            else addressBuilder.append(adminArea)
        }
        if (!countryName.isNullOrBlank()) {
            if (addressBuilder.isNotBlank())
                addressBuilder.append(",${countryCode}")
            else addressBuilder.append(countryCode)
        }
        if (!postalCode.isNullOrBlank()) {
            if (addressBuilder.isNotBlank())
                addressBuilder.append(",$postalCode")
            else addressBuilder.append(postalCode)
        }

        return addressBuilder.toString()
    }


    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    private fun currentDate(): Date {
        val calendar: Calendar = Calendar.getInstance()
        return calendar.getTime()
    }

    fun String.getTimeAgo(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("GMT")
        val date = sdf.parse(this)
//        date.time += ((5*60*60)+(30*60))*1000
        var time = date.time
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000
        }
        val now: Long = currentDate().getTime()
        if (time > now || time <= 0) {
            return "in the future"
        }
        val diff = now - time
        return if (diff < MINUTE_MILLIS) {
            "Moments ago"
        } else if (diff < 2 * MINUTE_MILLIS) {
            "1 Min"
        } else if (diff < 50 * MINUTE_MILLIS) {
            (diff / MINUTE_MILLIS).toString() + " Min"
        } else if (diff < 90 * MINUTE_MILLIS) {
            "1 Hour"
        } else if (diff < 24 * HOUR_MILLIS) {
            (diff / HOUR_MILLIS).toString() + " Hours"
        } else if (diff < 48 * HOUR_MILLIS) {
            "Yesterday"
        } else {
            (diff / DAY_MILLIS).toString() + " Days ago"
        }
    }


    fun Number.prettyCount(): String {
        val suffix = arrayOf("m", "km")
        val numValue = toLong()
        return if (numValue > 1000) {
            DecimalFormat("#,##").format(numValue) + "km"
        } else {
            DecimalFormat("#,##").format(numValue) + "m"
        }
    }

    fun getDistance(pointA: LatLng, pointB: LatLng): Int {
        val location1 = Location("locationA")
        location1.latitude = pointA.latitude
        location1.longitude = pointA.longitude
        val location2 = Location("locationB")
        location2.latitude = pointB.latitude
        location2.longitude = pointB.longitude
        return (location1.distanceTo(location2) / 1000).toInt()
    }


    fun Context.getAddressLine(lat: Double, lng: Double, handle: (String) -> Unit) {
        var address = ""
        Geocoder(this).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(lat, lng, 1) {
                    address = it[0].getAddressLine(0)
                    handle.invoke(address)
                }
            } else {
                try {
                    val addresses = getFromLocation(lat, lng, 1)?.firstOrNull()
                    address = getFromLocation(lat, lng, 1)?.get(0)?.getAddressLine(0) ?: ""
                    handle.invoke(addresses?.getAddressLine(0) ?: "Address Not Found!")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun Context.getAddressSecond(lat: Double, lng: Double, handle: (String) -> Unit) {
        var address = ""
        Geocoder(this).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(lat, lng, 1) {
                    address = it[0]?.extractAddress() ?: ""
                    handle.invoke(address)
                }
            } else {
                try {
                    address = getFromLocation(lat, lng, 1)?.get(0)?.extractAddress() ?: ""
                    handle.invoke(address.ifEmpty { "Address Not Found !" })
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    fun Context.getAddress(lat: Double, lng: Double, handle: (String) -> Unit) {
        var address = ""
        Geocoder(this).apply {
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                getFromLocation(lat, lng, 1) {
                    address = it[0]?.extractAddress() ?: ""
                    handle.invoke(it[0]?.getAddressLine(0) ?: "Address Not Found!")
                }
            } else {*/
            try {
                val addresses = getFromLocation(lat, lng, 1)?.firstOrNull()
                address = addresses?.extractAddress() ?: ""
                handle.invoke(addresses?.getAddressLine(0) ?: "Address Not Found!")
            } catch (e: Exception) {
                e.printStackTrace()
            }
//            }
        }
    }

    fun FragmentActivity.composeEmailIntent(email: String, subject: String, body: String): Intent {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:") // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_TEXT, body)
        return intent
    }

    fun ImageView.loadCircleCrop(url: String) {
        Glide.with(context).load(url).circleCrop().placeholder(R.drawable.place_holde_profile)
            .into(this)
    }

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager: LocationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    private var dialog: Dialog? = null

    fun showProgress(context: Context) {
        if (dialog == null) {
            dialog = Dialog(context).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(ProgressDialogBinding.inflate(LayoutInflater.from(context)).root)
                window?.setDimAmount(0.8F)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
            }
        }
        dialog?.show()
    }

    fun dismissProgress() {
        dialog?.dismiss()
        dialog = null
    }

    fun Int.isSuccessful(): Boolean {
        return absoluteValue in 200..299
    }

    fun String.showToast(binding: ViewBinding) {
        Snackbar.make(
            binding.root, this,
            Snackbar.LENGTH_LONG
        ).show()
    }

    fun String.showToast() {
        Toast.makeText(App.instance, this, Toast.LENGTH_SHORT).show()
    }


    fun Context.openWhatsApp(phone: String, message: String) {
        val isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp")
        if (isWhatsappInstalled) {
            val sendIntent = Intent("android.intent.action.MAIN")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.Conversation")
            sendIntent.putExtra(
                "jid",
                PhoneNumberUtils.stripSeparators(phone) + "@s.whatsapp.net"
            ) //phone number without "+" prefix
            sendIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(sendIntent)
        } else {
            val uri = Uri.parse("market://details?id=com.whatsapp")
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            Toast.makeText(
                this, "WhatsApp not Installed",
                Toast.LENGTH_SHORT
            ).show()
            startActivity(goToMarket)
        }
    }

    private fun Context.whatsappInstalledOrNot(uri: String): Boolean {
        val pm: PackageManager = packageManager
        var app_installed = false
        app_installed = try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        return app_installed
    }

    fun Context.getWhatsappIntent(number: String, text: String) {
        try {
            val uri = Uri.parse("smsto:$number")
            val waIntent = Intent(Intent.ACTION_SENDTO, uri)
            waIntent.setPackage("com.whatsapp")
            waIntent.putExtra(Intent.EXTRA_TEXT, text)
            waIntent.type = "text/plain"
            if (waIntent.resolveActivity(this.packageManager) != null) {
                startActivity(waIntent)
            }/*else "Whatsapp not installed.".showToast()*/

        } catch (exception: Exception) {
            "Whatsapp not installed.".showToast()
        }
    }

    fun Context.redirectToWhatsapp() {
        try {
            val whatsappIntent = Intent(Intent.ACTION_SEND)
            whatsappIntent.type = "text/plain"
            whatsappIntent.setPackage("com.whatsapp")
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Text message from Zajel")
            try {
                startActivity(whatsappIntent)
            } catch (ex: ActivityNotFoundException) {
                "Whatsapp have not been installed.".showToast()
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    fun Context.showDialog(icon: Int, title: String, message: String, handle: () -> Unit) {
        Dialog(this, R.style.dialog_style).apply {
            DialogCongratulationBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setDimAmount(0.8F)
                it.ivSuccess.setImageResource(icon)
                it.tvCongratulation.text = title
                it.tvDescription.text = message
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                it.tvOkay.setOnClickListener {
                    dismiss()
                    handle.invoke()
                }
                show()
            }
        }
    }

    fun ResponseBody.getError(context: Context): ErrorModel? {
        val converter = RestClient.getInstance().responseBodyConverter<ErrorModel>(
            ErrorModel::class.java,
            arrayOfNulls<Annotation>(0)
        )
        val errorResponse: ErrorModel? = converter.convert(this)
        if (errorResponse?.message?.equals("Unauthorized access", true) == true) {
            App.preferenceManager.logout()
            context.startActivity(Intent(context, SplashActivity::class.java).apply {
                putExtra("fromLogout", true)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            return null
        }
        return errorResponse
    }


    fun Context.showMediaOptionDialog(camera: () -> Unit = {}, gallery: () -> Unit = {}) {
        Dialog(this, R.style.dialog_style).apply {
            DialogChooseMediaOptionBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setDimAmount(0.8F)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                it.tvCancel.setOnClickListener {
                    dismiss()
                }
                it.tvCamera.setOnClickListener {
                    camera.invoke()
                }
                it.tvGallery.setOnClickListener {
                    gallery.invoke()
                }
                show()
            }
        }
    }


    fun Context.showReasonDialog(handle: (String) -> Unit = {}) {
        Dialog(this, R.style.dialog_style).apply {
            DialogRejectedReasonsBinding.inflate(layoutInflater).let { binding ->
                val adapter = RadioAdapter { position ->
                    if (position == 4) {
                        binding.llEnterReason.visibility = View.VISIBLE
                    } else {
                        binding.llEnterReason.visibility = View.GONE
                    }
                }
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(binding.root)
                window?.setDimAmount(0.8F)
                binding.rvReasons.adapter = adapter
                binding.tvSubmit.setOnClickListener {
                    if (binding.llEnterReason.isVisible) {
                        val reason = binding.etNewPassword.text.trim().toString()
                        if (reason.isNotEmpty()) {
                            handle.invoke(reason)
                            dismiss()
                        } else "Please enter a reason".showToast()
                    } else {
                        if (adapter.selectedPosition != -1) {
                            handle.invoke(adapter.get(adapter.selectedPosition)?.text ?: "")
                            dismiss()
                        } else "Please select a reason".showToast()
                    }
                }
                binding.tvCancel.setOnClickListener {
                    dismiss()
                }
                adapter.add(RadioOption("User Denied", false))
                adapter.add(RadioOption("My vehicle is puncture", false))
                adapter.add(RadioOption("Unable to contact user", false))
                adapter.add(RadioOption("User Denied", false))
                adapter.add(RadioOption("My reason is not listed here", false))
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show()
            }
        }
    }

    fun FragmentActivity.showLogoutDialog(handle: () -> Unit = {}) {
        Dialog(this, R.style.dialog_style).apply {
            DialogLogoutBinding.inflate(layoutInflater).let {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setCanceledOnTouchOutside(false)
                setCancelable(false)
                setContentView(it.root)
                window?.setDimAmount(0.8F)
                it.tvYes.setOnClickListener {
                    dismiss()
                    handle.invoke()
                    App.preferenceManager.logout()
                    stopService(Intent(context, LocationService::class.java))
                    startActivity(Intent(context, SplashActivity::class.java).apply {
                        putExtra("fromLogout", true)
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    })
                    this@showLogoutDialog.finish()
                }
                it.tvNo.setOnClickListener {
                    dismiss()
                }
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                show()
            }
        }
    }


    fun setLightStatusBar(activity: Activity) {
        var flags = activity.window.decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        activity.window.decorView.systemUiVisibility = flags
    }

    fun clearLightStatusBar(activity: Activity) {
        var flags = activity.window.decorView.systemUiVisibility
        flags = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        activity.window.decorView.systemUiVisibility = flags
    }


    fun setTransparentStatusBarOnly(activity: Activity) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        activity.window.statusBarColor = getColor(R.color.colorPrimary)
        activity.window.statusBarColor = Color.TRANSPARENT
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun setTransparentStatusBarOnly(window: Window?) {
        window?.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        activity.window.statusBarColor = getColor(R.color.colorPrimary)
        window?.statusBarColor = Color.TRANSPARENT
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        window?.decorView?.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    fun statusBar(activity: FragmentActivity) {
        activity.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun transaction() {
        //supportFragmentManager.beginTransaction().add(R.id.franeLayout, SplashFragment()).commit()
    }


    fun String.span(spanText: String, onClick: () -> Unit): SpannableString {
        return SpannableString(this).apply {
            setSpan(
                object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        onClick.invoke()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                        ds.color = Color.parseColor("#0054A6")

                    }

                },
                indexOf(spanText),
                indexOf(spanText) + spanText.length,
                Spannable.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
    }

}