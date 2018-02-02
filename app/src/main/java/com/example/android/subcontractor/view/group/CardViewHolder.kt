package com.example.android.subcontractor.view.group

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Card
import com.example.android.subcontractor.util.DateUtil
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.item_card.view.*
import java.text.SimpleDateFormat

/**
 * Created by samsung on 2018-01-18.
 */
class CardViewHolder(v: View) : RecyclerView.ViewHolder(v) {

    fun bind(card: Card, listener: (card: Card) -> Unit) {
//        itemView.text_card_item_sub.text = card.text
        itemView.text_card_item_uploader.text = card.uploader
        itemView.text_card_item_traveltime.text = DateUtil.toString(card.travelTime)
//        itemView.text_card_item_uploadtime.text = DateUtil.toString(card.travelTime,"yy년 MM월 dd일")

        if(card.photoUrl!=""){
            Glide.with(itemView)
                    .load(card.photoUrl)
                    .apply(RequestOptions()
                            .placeholder(R.drawable.material_background_default)
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop())
                    .into(itemView.image_card_item)
        }else{
            itemView.image_card_item.visibility=View.GONE
        }
        itemView.setOnClickListener {
            listener.invoke(card)
        }
    }
}