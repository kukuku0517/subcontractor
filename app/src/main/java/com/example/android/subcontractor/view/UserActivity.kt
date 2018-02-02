package com.example.android.subcontractor.view

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.User
import com.example.android.subcontractor.util.AuthUtil
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.view.group.member.UserAdapter
import com.example.android.subcontractor.view.group.photo.CustomAdapter
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.item_user_result.view.*
import kotlinx.android.synthetic.main.item_user.view.*

class UserActivity : AppCompatActivity() {

    companion object {
        val USER_INITIAL_INVITE_REQUEST = 0
    }

    var flag: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        flag = intent.getIntExtra("userFlag", 0)
        init()
        initData()
    }

    lateinit var selectedAdapter: UserAdapter<User>
    lateinit var resultAdapter: CustomAdapter<User>
    val selected = mutableMapOf<String, User>()
    val result = mutableMapOf<String, User>()

    fun init() {
        selectedAdapter = UserAdapter({ holder, data ->
            holder?.itemView?.text_user_name?.text = data.email
            Glide.with(this).load(data.photoUrl).into(holder?.itemView?.image_user_profile)
            holder?.itemView?.setOnClickListener {
                selected.remove(data.id)
                result.put(data.id,data)
                selectedAdapter.updateData(selected.mapTo(mutableListOf()){it.value})
                resultAdapter.updateData(result.mapTo(mutableListOf()){it.value})
            }
        })
        resultAdapter = CustomAdapter(R.layout.item_user_result) { holder, data ->
            holder?.itemView?.text_user_result_name?.text = data.name
            holder?.itemView?.text_user_result_email?.text = data.email
            Glide.with(this).load(data.photoUrl).into(holder?.itemView?.image_user_result)

            holder?.itemView?.btn_user_result_add?.setOnClickListener {
                selected.put(data.id, data)
                result.remove(data.id)

                selectedAdapter.updateData(selected.mapTo(mutableListOf()){it.value})
                resultAdapter.updateData(result.mapTo(mutableListOf()){it.value})
            }

        }
        recycler_user_selected.adapter = selectedAdapter
        recycler_user_selected.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL, false)
        recycler_user_result.adapter = resultAdapter
        recycler_user_result.layoutManager = LinearLayoutManager(this)

        btn_user_done.setOnClickListener {
            val intent = Intent()

            intent.putExtra("selectedUsers",selected.mapTo(arrayListOf()){it.value.id} as ArrayList<String>)
            setResult(UserActivity.USER_INITIAL_INVITE_REQUEST,intent)
            finish()
        }
    }

    /**
     * 0 : No members, All results
     *
     */
    fun initData() {
        when (flag) {
            0 -> {
                selected.put(AuthUtil().getUserId(), AuthUtil().getUser())
                selectedAdapter.updateData(selected.mapTo(mutableListOf()) { it.value })


                DataUtil(this).getAllUsersAsMap { result ->
                    result.putAll(result)

                    val list = mutableListOf<User>()
                    for(it in result){
                        if (!selected.containsKey(it.key)) {
                            list.add(it.value)
                        }
                    }
                    resultAdapter.updateData(list)
                }

            }
        }
    }

}
