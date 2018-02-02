package com.example.android.subcontractor.view.group

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide.init
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Card

/**
 * Created by samsung on 2018-01-18.
 */
class CardAdapter(val listener:(card:Card)->Unit) : RecyclerView.Adapter<CardViewHolder>() {

    var data: MutableList<Card>

    init {
        data = mutableListOf()
    }

    fun updateData(data: MutableList<Card>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardViewHolder =
            CardViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.item_card, parent, false))


    override fun onBindViewHolder(holder: CardViewHolder?, position: Int) {
        holder?.bind(data.get(position),listener)
    }

    override fun getItemCount() = data.size

}