package com.example.zajil.util

import java.text.SimpleDateFormat
import java.util.Locale

fun main(){
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date = dateFormat.format(System.currentTimeMillis())
    println("------ date here ------")
    println(date)
}