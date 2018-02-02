package com.example.android.subcontractor.view.group.photo

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.android.subcontractor.R
import com.example.android.subcontractor.view.group.member.UserViewHolder

/**
 * Created by samsung on 2018-01-18.
 */
class CustomAdapter<T>(resource:Int, val binder: (holder: CustomViewHolder<T>?,data: T) -> Unit) : RecyclerView.Adapter<CustomViewHolder<T>>() {

    lateinit var data: MutableList<T>
    val resource = resource

    init {
        this.data = mutableListOf()
    }

    fun updateData(data: MutableList<T>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CustomViewHolder<T> =
            CustomViewHolder(LayoutInflater.from(parent?.context).inflate(resource, parent, false))


    override fun onBindViewHolder(holder: CustomViewHolder<T>?, position: Int) {
        binder.invoke(holder, data[position])
    }

    override fun getItemCount() = data.size

}