package com.example.android.subcontractor.util

import com.example.android.subcontractor.data.User
import com.google.firebase.auth.FirebaseAuth

/**
 * Created by samsung on 2018-01-18.
 */
class AuthUtil {
    val auth = FirebaseAuth.getInstance()
    val cur = auth.currentUser

    fun getUser() =
            User(cur?.uid!!, cur?.displayName!!, cur?.email!!, cur?.photoUrl.toString()!!, mutableMapOf(), mutableMapOf())

    fun getUserId()
            = cur?.uid.toString()

    fun getUserName()
            = cur?.displayName.toString()

    fun getUserEmail()
            = cur?.email.toString()

    fun getUserPhotoUrl() = cur?.photoUrl
}