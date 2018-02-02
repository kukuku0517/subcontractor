package com.example.android.subcontractor

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.text.format.DateUtils
import android.util.Log
import android.view.*
import android.widget.Button
import com.example.android.subcontractor.R.id.*
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.view.CreateActivity
import com.example.android.subcontractor.view.GroupActivity
import com.example.android.subcontractor.view.LoginActivity
import com.example.android.subcontractor.view.main.GroupAdapter
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.support.v4.app.ActivityCompat
import android.content.pm.PackageManager
import android.support.v7.widget.GridLayoutManager
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init
import com.bumptech.glide.request.RequestOptions
import com.example.android.subcontractor.util.AuthUtil
import kotlinx.android.synthetic.main.layout_header.view.*


class MainActivity : AppCompatActivity() {

    val auth = FirebaseAuth.getInstance()
    var flag = 0
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf<String>(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)

    lateinit var adapter: GroupAdapter

    override fun onResume() {
        super.onResume()
        initData(flag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (requestPermission()) {
            init()
            initData(flag)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    requestPermission()
                } else {
                    init()
                    initData(flag)
                }
            }
        }
    }


    fun requestPermission(): Boolean {
        val permission = ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE)
        return if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            )
            false
        } else {
            true
        }
    }

    private fun init() {
        setSupportActionBar(toolbar_main)
        nav_main.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.sign_out -> onClickSignOut()
            }
            true
        }

        btn_main_home.setOnClickListener { drawer_main.openDrawer(Gravity.START) }
        fab_main_new.setOnClickListener { onClickCreate() }
        refresh_layout_main.setOnRefreshListener { initData(flag) }

        adapter = GroupAdapter { group ->
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("groupId", group.id)
            startActivity(intent)
        }
        adapter.setHasStableIds(true)
        recycler_main_posts.adapter = adapter
        recycler_main_posts.layoutManager = GridLayoutManager(this, 2)


        switch_main_sort.setOnClickListener { view ->
            flag = (flag + 1) % 3
            adapter.clearData()
            when (flag) {
                0 -> {
                    (view as TextView).text = "인기순"
                    initData(flag)
                }
                1 -> {
                    (view as TextView).text = "최신순"
                    initData(flag)
                }
                2 -> {
                    (view as TextView).text = "MY"
                    DataUtil(this).getMyGroup { group ->
                        adapter.addData(group)
                    }
                }
            }
        }
        val header = nav_main.getHeaderView(0)
        header.text_nav_name.text=AuthUtil().getUserName()
        header.text_nav_email.text=AuthUtil().getUserEmail()
        Glide.with(this)
                .load(AuthUtil().getUserPhotoUrl())
                .apply(RequestOptions().circleCrop())
                .into(header.image_nav_profile)
    }

    private fun initData(flag: Int) {
        DataUtil(this).getGroupsByFlag(flag) { groups ->
            adapter.updateData(groups)
            refresh_layout_main.isRefreshing = false
        }
    }


    private fun onClickCreate() {
        startActivity(Intent(this, CreateActivity::class.java))
    }

    private fun onClickSignOut() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
