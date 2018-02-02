package com.example.android.subcontractor.view

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.android.subcontractor.R
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import java.util.*
import java.util.Arrays.asList
import com.google.firebase.auth.FirebaseUser
import com.firebase.ui.auth.IdpResponse
import android.content.Intent
import android.widget.Toast
import com.example.android.subcontractor.MainActivity
import com.example.android.subcontractor.util.AuthUtil
import com.example.android.subcontractor.util.DataUtil


class LoginActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    val RC_SIGN_IN = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (auth.currentUser != null) {
//        if (false) {
            startMainActivity()
        } else {

            val providers = Arrays.asList(
                    AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
//                    AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build(),
                    AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
//                    AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build(),
                    AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()
            )
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {

                DataUtil(this).createNewUser(AuthUtil().getUser()) {
                    startMainActivity()
                }

            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
