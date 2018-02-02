package com.example.android.subcontractor.data.motionview

import com.example.android.subcontractor.motionview.viewmodel.Font

/**
 * Created by samsung on 2018-01-21.
 */

data class FontDTO (
        val color:Int=0,
        val typeface:String="",
        val size:Float=0f
){
    fun toFont(): Font {
        val font = Font()
        font.color=color
        font.typeface=typeface
        font.size=size
        return font
    }

}

fun Font.wrap(): FontDTO {
    return FontDTO(this.color,this.typeface,this.size)
}