package com.example.android.subcontractor.view.group.member

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Card
import com.example.android.subcontractor.view.group.CardViewHolder

/**
 * Created by samsung on 2018-01-18.
 */
class UserAdapter<T>(val binder: (holder: UserViewHolder<T>?,data:T) -> Unit) : RecyclerView.Adapter<UserViewHolder<T>>() {

    var data: MutableList<T>

    init {
        data = mutableListOf()
    }

    fun updateData(data: MutableList<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): UserViewHolder<T> =
            UserViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_user, parent, false))


    override fun onBindViewHolder(holder: UserViewHolder<T>?, position: Int) {
        binder.invoke(holder, data[position])
    }

    override fun getItemCount() = data.size

}