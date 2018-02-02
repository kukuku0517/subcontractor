package com.example.android.subcontractor.view.main

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Card
import com.example.android.subcontractor.data.Group
import com.example.android.subcontractor.util.DataUtil

import kotlinx.android.synthetic.main.item_group.*
import kotlinx.android.synthetic.main.item_group.view.*

/**
 * Created by samsung on 2018-01-18.
 */

class GroupViewHolder(v: View,adapter:GroupAdapter) : RecyclerView.ViewHolder(v) {

    val v = v
    val adapter = adapter
    @SuppressLint("SetTextI18n")
    fun bind(group: Group, listener: (group: Group) -> Unit) {

        itemView.text_group_item_title.text = group.name
        itemView.text_group_item_members.text = "${group.member?.size.toString()}"
        DataUtil(itemView.context).getLikeToGroup(group.id) { isLike, count ->
            itemView.text_group_item_likes.text = "$count"
            when (isLike) {
                true -> {
                    itemView.btn_group_like.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(itemView.context, R.color.mainTheme), PorterDuff.Mode.MULTIPLY)
                }
                false -> {
                    itemView.btn_group_like.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(itemView.context, R.color.gray), PorterDuff.Mode.MULTIPLY)
                }
            }
        }

        Glide.with(itemView)
                .load(group.photoUrl)
                .apply(RequestOptions().error(R.drawable.ic_launcher_background))
                .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
                .into(itemView.image_group_item)

        itemView.btn_group_like.setOnClickListener {
            Log.d("kkjh","clicked")
            DataUtil(itemView.context).setLikeToGroup(group.id) { isLike, count ->
                Log.d("kkjh","called")
                group.likes=count
                adapter.notifyDataSetChanged()
            }
        }

        itemView.setOnClickListener {
            listener.invoke(group)
        }
    }
}