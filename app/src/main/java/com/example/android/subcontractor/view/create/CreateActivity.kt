package com.example.android.subcontractor.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Group
import com.example.android.subcontractor.data.User
import com.example.android.subcontractor.util.AuthUtil
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.view.group.member.UserAdapter
import kotlinx.android.synthetic.main.activity_create.*
import kotlinx.android.synthetic.main.item_user.view.*
import org.parceler.Parcels
import java.util.*

class CreateActivity : AppCompatActivity() {

    lateinit var adapter: UserAdapter<User>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        init()
//        initData()
    }

    fun init() {
        button_create_done.setOnClickListener {
            onClickCreate()
        }
        adapter = UserAdapter({ holder, data ->
            val view = holder?.itemView
            Glide.with(this).load(data.photoUrl).into(view?.image_user_profile)
            view?.text_user_name?.text = data.name
            view?.image_user_profile?.setOnClickListener {

            }
        })
        recycler_create_members.adapter = adapter
        recycler_create_members.layoutManager = GridLayoutManager(this, 5)
        btn_create_invite.setOnClickListener {
            onClickInviteFriends()
        }
    }


    fun onClickInviteFriends() {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra("userFlag", 0)
        startActivityForResult(intent, UserActivity.USER_INITIAL_INVITE_REQUEST)
    }

    val selectedUsers = mutableListOf<User>()
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            UserActivity.USER_INITIAL_INVITE_REQUEST -> {
                val list = data?.getStringArrayListExtra("selectedUsers")
                if (list != null) {
                    selectedUsers.clear()
                    DataUtil(this).getUsersFrom(list) { data ->
                        selectedUsers.add(data)
                        adapter.updateData(selectedUsers)
                    }
                }
            }

        }
    }

    fun initData() {
        DataUtil(this).getAllUsers { users ->
            adapter.updateData(users)
        }
    }

    fun onClickCreate() {
        val groupName = edit_text_create_title.text.toString()
        val uuid = UUID.randomUUID().toString()
        val members = selectedUsers.associateTo(mutableMapOf()) { it.id to it.id }
        val group = Group(uuid, groupName, members, true, System.currentTimeMillis(), 0, "")

        DataUtil(this).createGroup(group, {
            Toast.makeText(this, "group created", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, GroupActivity::class.java)
            intent.putExtra("groupId", group.id)
            startActivity(intent)
            finish()
        })

    }
}
