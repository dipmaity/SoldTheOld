package com.quantumwebgarden.soldold.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var id:String = "",
    val name:String = "",
    val email: String = "",
    val image: String = "",
    val mobile:Long = 0
):Parcelable
