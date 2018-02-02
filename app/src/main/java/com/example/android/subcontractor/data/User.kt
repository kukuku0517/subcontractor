package com.example.android.subcontractor.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.parceler.Parcel

/**
 * Created by samsung on 2018-01-18.
 */

data class User (
        val id: String = "",
        val name:String ="",
        val email:String="",
        val photoUrl:String="",
        val groups: MutableMap<String, String>?=null,
        val friends: MutableMap<String, String>?=null
)