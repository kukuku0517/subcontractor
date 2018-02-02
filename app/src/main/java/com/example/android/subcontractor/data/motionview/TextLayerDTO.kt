package com.example.android.subcontractor.data.motionview

import android.support.annotation.FloatRange
import com.example.android.subcontractor.motionview.viewmodel.Font
import com.example.android.subcontractor.motionview.viewmodel.TextLayer

/**
 * Created by samsung on 2018-01-21.
 */


data class TextLayerDTO(val text: String = "",
                        val font: FontDTO = FontDTO(0, "", 0f),
                        val rotationInDegrees: Float = 0f,
                        val scale: Float = 0f,
                        val x: Float = 0f,
                        val y: Float = 0f,
                        val isFlipped: Boolean = false,
                        val id:String =""
) {

    fun toTextLayer(): TextLayer {
        val layer = TextLayer()
        layer.text = text
        layer.font = font.toFont()
        layer.rotationInDegrees = rotationInDegrees
        layer.scale = scale
        layer.x = x
        layer.y = y
        layer.isFlipped = isFlipped
        layer.id=id
        return layer
    }

}

fun TextLayer.wrap(): TextLayerDTO {
    return TextLayerDTO(this.text, this.font.wrap(), this.rotationInDegrees, this.scale, this.x, this.y, this.isFlipped,this.id)
}