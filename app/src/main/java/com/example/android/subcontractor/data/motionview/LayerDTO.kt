package com.example.android.subcontractor.data.motionview

import com.example.android.subcontractor.motionview.viewmodel.Layer
import com.example.android.subcontractor.motionview.viewmodel.TextLayer

/**
 * Created by samsung on 2018-01-22.
 */

data class LayerDTO(
        val rotationInDegrees: Float = 0f,
        val scale: Float = 0f,
        val x: Float = 0f,
        val y: Float = 0f,
        val isFlipped: Boolean = false,
        val resId:Int = -1,
        val id:String = ""
) {

    fun toLayer(): Layer {
        val layer = Layer()
        layer.rotationInDegrees = rotationInDegrees
        layer.scale = scale
        layer.x = x
        layer.y = y
        layer.isFlipped = isFlipped
        layer.resId=resId
        layer.id=id
        return layer
    }

}

fun Layer.wrap(): LayerDTO {
    return LayerDTO(this.rotationInDegrees, this.scale, this.x, this.y, this.isFlipped,this.resId,this.id)
}