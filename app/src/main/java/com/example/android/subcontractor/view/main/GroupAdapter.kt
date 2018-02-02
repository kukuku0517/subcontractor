package com.example.android.subcontractor.view.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide.init
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Card
import com.example.android.subcontractor.data.Group
import com.example.android.subcontractor.view.group.CardViewHolder

/**
 * Created by samsung on 2018-01-18.
 */
class GroupAdapter(val listener:(group:Group)->Unit) : RecyclerView.Adapter<GroupViewHolder>() {

    var data: MutableList<Group>

    init{
        data= mutableListOf()
    }
    fun updateData(data: MutableList<Group>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun clearData(){
        data.clear()
    }
    fun addData(data : Group){
        this.data.add(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GroupViewHolder =
            GroupViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_group
                    , parent, false),this)


    override fun onBindViewHolder(holder: GroupViewHolder?, position: Int) {
        holder?.bind(data.get(position),listener)
    }

    override fun getItemCount() = data.size

}