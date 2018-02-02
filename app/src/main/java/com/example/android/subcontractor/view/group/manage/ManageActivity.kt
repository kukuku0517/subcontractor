package com.example.android.subcontractor.view.group.manage

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.bumptech.glide.Glide
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.User
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.view.group.photo.CustomAdapter
import kotlinx.android.synthetic.main.activity_manage.*
import kotlinx.android.synthetic.main.item_user_result.view.*
import kotlinx.android.synthetic.main.item_user_manage.view.*


class ManageActivity : AppCompatActivity() {

    lateinit var groupId: String
    val memberList: MutableMap<String, User> = mutableMapOf()
    val managerList: MutableMap<String, User> = mutableMapOf()

    lateinit var managerAdapter: CustomAdapter<User>
    lateinit var memberAdapter: CustomAdapter<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage)
        init()
        updateData()
    }

    private fun init() {
        groupId = intent.getStringExtra("groupId")
        managerAdapter = CustomAdapter(R.layout.item_user_result) { holder, data ->
            holder?.itemView?.text_user_manage_name?.text = data.name
            holder?.itemView?.text_user_result_email?.text = data.email
            Glide.with(this).load(data.photoUrl).into(holder?.itemView?.image_user_result)

        }
        memberAdapter = CustomAdapter(R.layout.item_user_manage) { holder, data ->
            holder?.itemView?.text_user_manage_name?.text = data.name
            holder?.itemView?.text_user_manage_email?.text = data.email
            Glide.with(this).load(data.photoUrl).into(holder?.itemView?.image_user_manage)

            holder?.itemView?.btn_user_manage_add?.setOnClickListener {
                DataUtil(this).setMemberToManager(groupId, data.id) {
                    Log.d("kjh","suc")
                    updateData()
                }
            }

        }
        recycler_manage_manager.adapter = managerAdapter
        recycler_manage_member.adapter = memberAdapter
        recycler_manage_manager.layoutManager = LinearLayoutManager(this)
        recycler_manage_member.layoutManager = LinearLayoutManager(this)

    }

    private fun updateData() {
        memberList.clear()
        managerList.clear()

        DataUtil(this).getMembersByGroup(groupId) { user, manager ->
            when (manager) {
                true -> {
                    memberList.put(user.id, user)
                    memberAdapter.updateData(memberList.mapTo(mutableListOf()) { it.value })
                }
                false
                -> {
                    managerList.put(user.id, user)
                    managerAdapter.updateData(managerList.mapTo(mutableListOf()) { it.value })
                }
            }

        }
    }
}
