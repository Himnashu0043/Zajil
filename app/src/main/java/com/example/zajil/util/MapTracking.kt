package com.example.zajil.util

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import com.example.zajil.R
import com.example.zajil.util.MapTracking.updateCarLoc
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object MapTracking {

    var currentLL: LatLng? = null
    var previousLL: LatLng? = null
    var movingMarker: Marker? = null
//    var zoomLevel:Float = 22F
    var zoomLevel:Float = 15F
    var movingZoomLevel:Float = 17F

    fun GoogleMap.updateCarLoc(ll: LatLng) {
            if (movingMarker == null) {
                movingMarker = getCarMarker(ll)
            }
            if (previousLL == null) {
                currentLL = ll
                previousLL = currentLL
                movingMarker?.position = currentLL!!
                movingMarker?.setAnchor(0.5f, 0.5f)
                CoroutineScope(Dispatchers.Main).launch {
                    val cameraPosition =
                        CameraPosition.Builder().target(currentLL!!).zoom(movingZoomLevel).build()
                    animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                }
            } else {
                previousLL = currentLL
                currentLL = ll
                val valueAnimator = carAnimator()
                valueAnimator.addUpdateListener { va ->
                    if (currentLL != null && previousLL != null) {
                        val multiplier = va.animatedFraction
                        val nxtLoc = LatLng(
                            multiplier * currentLL!!.latitude + (1 - multiplier) * previousLL!!.latitude,
                            multiplier * currentLL!!.longitude + (1 - multiplier) * previousLL!!.longitude
                        )
                        movingMarker?.position = nxtLoc
                        val rotation = getCarRotation(previousLL!!, nxtLoc)
                        if (!rotation.isNaN()) {
                            movingMarker?.rotation = rotation
                        }
                        movingMarker?.setAnchor(0.5f, 0.5f)
                        CoroutineScope(Dispatchers.Main).launch {
                            val cameraPosition =
                                CameraPosition.Builder().target(ll).zoom(movingZoomLevel).build()
                            animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                        }
                    }
                }
                valueAnimator.start()
            }

    }


    /**
     * Moves camera
     * */
    fun GoogleMap.moveView(ll: LatLng) {

        moveCamera(CameraUpdateFactory.newLatLng(ll))
    }

    /**
     * Animates View for a specific location position
     * */
    fun GoogleMap.animateView(ll: LatLng) {
        CoroutineScope(Dispatchers.Main).launch {
            val cameraPosition = CameraPosition.Builder().target(ll).zoom(zoomLevel).build()
            animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }


    /**
     * This method is responsible for displaying path between origin and destination points.
     *
     * Display Ride-path using this method
     *
     * It takes list of LatLng which you can get from google maps direction api
     * */
    fun GoogleMap.displayPath(latLngList: ArrayList<LatLng>) {
        // add these lines in activity or fragment

        lateinit var greyLine: Polyline
        lateinit var blackLine: Polyline

        val builder = LatLngBounds.Builder()
        for (latLng in latLngList) {
            builder.include(latLng)
        }
        val boundBuilds = builder.build()
        animateCamera(CameraUpdateFactory.newLatLngBounds(boundBuilds, 2))

        val polyOptions = PolylineOptions()
        polyOptions.color(Color.GRAY)
        polyOptions.width(5f)
        polyOptions.addAll(latLngList)
        greyLine = addPolyline(polyOptions)
        
        val blackPolyOptions = PolylineOptions()
        blackPolyOptions.color(Color.BLACK)
        blackPolyOptions.width(5f)
        blackLine = addPolyline(blackPolyOptions)

        val oMarker: Marker? = getOriginMarker(latLngList.first())
        oMarker?.setAnchor(0.5f, 0.5f)
        val dMarker: Marker? = getDestinationMarker(latLngList.last())
        dMarker?.setAnchor(0.5f, 0.5f)

        val polyAnimator = polyAnimator()
        polyAnimator.addUpdateListener { valueAnimator ->
            val percent = (valueAnimator.animatedValue as Int)
            val indexNumber = (greyLine.points.size) * (percent / 100.0f).toInt()
            blackLine.points = greyLine.points.subList(0, indexNumber)
        }
        polyAnimator.start()
    }


    // Marker functions

    fun GoogleMap.getOriginMarker(position: LatLng): Marker? {
        return addMarker(MarkerOptions().position(position))
    }

    fun GoogleMap.getDestinationMarker(position: LatLng): Marker? {
        return addMarker(MarkerOptions().position(position))
    }

    fun GoogleMap.getCarMarker(position: LatLng): Marker? {
        return addMarker(MarkerOptions().position(position).icon(bitmapFromVector(R.drawable.car)))
    }

    fun bitmapFromVector(vectorResId: Int): BitmapDescriptor? {
        // below line is use to generate a drawable.
        val vectorDrawable = ContextCompat.getDrawable(
            App.instance, vectorResId
        )

        // below line is use to set bounds to our vector
        // drawable.
        vectorDrawable!!.setBounds(
            0, 0, vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )

        // below line is use to create a bitmap for our
        // drawable which we have added.
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // below line is use to add bitmap in our canvas.
        val canvas = Canvas(bitmap)

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas)

        // after generating our bitmap we are returning our
        // bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }



    /**
     * Calculates and returns a direction angle for a specific path
     * */
    fun getCarRotation(startLL: LatLng, endLL: LatLng): Float {
        val latDifference: Double = kotlin.math.abs(startLL.latitude - endLL.latitude)
        val lngDifference: Double = kotlin.math.abs(startLL.longitude - endLL.longitude)
        var rotation = -1F
        when {
            startLL.latitude < endLL.latitude && startLL.longitude < endLL.longitude -> {
                rotation = Math.toDegrees(kotlin.math.atan(lngDifference / latDifference)).toFloat()
            }

            startLL.latitude >= endLL.latitude && startLL.longitude < endLL.longitude -> {
                rotation =
                    (90 - Math.toDegrees(kotlin.math.atan(lngDifference / latDifference)) + 90).toFloat()
            }

            startLL.latitude >= endLL.latitude && startLL.longitude >= endLL.longitude -> {
                rotation =
                    (Math.toDegrees(kotlin.math.atan(lngDifference / latDifference)) + 180).toFloat()
            }

            startLL.latitude < endLL.latitude && startLL.longitude >= endLL.longitude -> {
                rotation =
                    (90 - Math.toDegrees(kotlin.math.atan(lngDifference / latDifference)) + 270).toFloat()
            }
        }
        return rotation
    }


    fun polyAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofInt(0, 100)
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.duration = 4000
        return valueAnimator
    }

    fun carAnimator(): ValueAnimator {
        val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 3000
        valueAnimator.interpolator = LinearInterpolator()
        return valueAnimator
    }
}