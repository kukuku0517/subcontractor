package com.example.android.subcontractor.view

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.LinearLayoutManager

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.request.RequestOptions
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.User
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.view.group.CardFragment
import com.example.android.subcontractor.view.group.GalleryFragment
import com.example.android.subcontractor.view.group.manage.ManageActivity
import com.example.android.subcontractor.view.group.member.UserAdapter
import com.firebase.ui.auth.AuthUI
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.activity_group.*
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.item_user.view.*
import org.parceler.Parcels

class GroupActivity : AppCompatActivity() {
    val TAG = "kjh"

    lateinit var groupId: String
    lateinit var adapter: UserAdapter<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group)
        init()
        initData()
    }

    override fun onResume() {
        super.onResume()
        initData()
    }

    private fun onClickSignOut() {
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun init() {
        setSupportActionBar(toolbar_group)
        groupId = intent.getStringExtra("groupId")
        view_pager_group.adapter = GroupFragmentPagerAdapter(groupId, supportFragmentManager)
        tab_layout_group.setupWithViewPager(view_pager_group)
        adapter = UserAdapter { v, data ->
            val view = v?.itemView
            Glide.with(this).load(data.photoUrl).apply(RequestOptions().circleCrop()).into(view?.image_user_profile)
            view?.text_user_name?.text = data.name
            view?.text_user_email?.text = data.email
        }
        recycler_group_members.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recycler_group_members.adapter = adapter

        nav_group.setNavigationItemSelectedListener { menu ->
            when (menu.itemId) {
                R.id.sign_out -> onClickSignOut()
            }
            true
        }

        fab_group_create.setOnClickListener {
            val intent = Intent(this, CardActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }
        btn_group_manage.setOnClickListener {
            val intent = Intent(this, ManageActivity::class.java)
            intent.putExtra("groupId", groupId)
            startActivity(intent)
        }


    }

    val list: MutableMap<String, User> = mutableMapOf()
    fun initData() {
        list.clear()
        DataUtil(this).getGroup(groupId, { group ->
            text_group_title.text = group.name
            Glide.with(this)
                    .load(group.photoUrl)
                    .apply(RequestOptions().transform(BlurTransformation(50)))
                    .into(image_group_background)
        }, { member ->
            list.put(member.id, member)
            adapter.updateData(list.mapTo(mutableListOf()) { it.value })
        })
    }


    private class GroupFragmentPagerAdapter(groupId: String, fm: FragmentManager) : FragmentPagerAdapter(fm) {
        val groupId: String = groupId

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "카드 뉴스"
                1 -> "사진"
                2 -> "지도"
                else -> null
            }
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> CardFragment.newInstance(groupId)
                1 -> GalleryFragment.newInstance(groupId)
                else -> CardFragment.newInstance(groupId)
            }
        }

        override fun getCount() = 3


    }
}
