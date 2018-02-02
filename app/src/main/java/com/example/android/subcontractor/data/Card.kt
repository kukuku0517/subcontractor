package com.example.android.subcontractor.data

import com.example.android.subcontractor.data.motionview.LayerDTO
import com.example.android.subcontractor.data.motionview.TextLayerDTO

/**
 * Created by samsung on 2018-01-18.
 */
data class Card (
    val id:String="",
    var photoUrl:String="",
    val uploader:String="",
    val uploaderId:String="",
    val updateTime:Long=0,
    val travelTime:Long=0,
    var textLayers:MutableMap<String, TextLayerDTO> = mutableMapOf(),
    var imageLayers:MutableMap<String, LayerDTO> = mutableMapOf()
)