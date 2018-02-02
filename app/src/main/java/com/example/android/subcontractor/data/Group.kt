package com.example.android.subcontractor.data

/**
 * Created by samsung on 2018-01-18.
 */
data class Group(
        val id:String ="",
        val name: String = "",
        val member: MutableMap<String, String>?=null,
        val open: Boolean = false,
        val timestamp: Long= 0,
        var likes: Int=0,
        val photoUrl: String? =""
//        val cards: List<Card>?=null,
//        val photos:List<Photo>?=null,
//        val places:List<Place>?=null
        )