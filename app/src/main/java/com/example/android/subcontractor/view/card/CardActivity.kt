package com.example.android.subcontractor.view

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.android.subcontractor.R
import kotlinx.android.synthetic.main.activity_card.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.android.subcontractor.BuildConfig
import com.example.android.subcontractor.data.Card
import com.example.android.subcontractor.data.motionview.LayerDTO
import com.example.android.subcontractor.data.motionview.TextLayerDTO
import com.example.android.subcontractor.data.motionview.wrap
import com.example.android.subcontractor.motionview.ui.StickerSelectActivity
import com.example.android.subcontractor.motionview.ui.TextEditorDialogFragment
import com.example.android.subcontractor.motionview.ui.adapter.FontsAdapter
import com.example.android.subcontractor.motionview.utils.FontProvider
import com.example.android.subcontractor.motionview.viewmodel.Font
import com.example.android.subcontractor.motionview.viewmodel.Layer
import com.example.android.subcontractor.motionview.viewmodel.TextLayer
import com.example.android.subcontractor.motionview.widgets.MotionView
import com.example.android.subcontractor.motionview.widgets.entity.ImageEntity
import com.example.android.subcontractor.motionview.widgets.entity.MotionEntity
import com.example.android.subcontractor.motionview.widgets.entity.TextEntity
import com.example.android.subcontractor.util.AuthUtil
import com.example.android.subcontractor.util.DataUtil
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.io.File
import java.util.*

class CardActivity : AppCompatActivity(), TextEditorDialogFragment.OnTextLayerCallback {
    val GALLERY_REQUEST = 12
    lateinit var groupId: String
    var cardId: String? = null
    var photoUrl: String? = null
    var edit: Boolean = false

    val textLayers = mutableMapOf<String, TextLayerDTO>()
    val imageLayers = mutableMapOf<String, LayerDTO>()

    val SELECT_STICKER_REQUEST_CODE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        initSticker()
        init()
    }

    fun initSticker() {
        this.fontProvider = FontProvider(resources)
        motionView = findViewById<View>(R.id.main_motion_view) as MotionView
        textEntityEditPanel = findViewById(R.id.panel_text_edit_card)
        motionView.isDrawingCacheEnabled = true
        motionView.setMotionViewCallback(motionViewCallback)
        initTextEntitiesListeners()
    }

    fun init() {
        setSupportActionBar(toolbar_card)
        groupId = intent.getStringExtra("groupId")
        cardId = intent.getStringExtra("cardId")
        photoUrl = intent.getStringExtra("photoUrl")
        edit = intent.getBooleanExtra("edit", false)


        if (photoUrl != null && photoUrl != "") {
            setMotionViewBackground()

            for (e in motionView.entities) {
                if (e is ImageEntity) {
                    imageLayers.put(e.layer.id, e.layer.wrap())
                } else if (e is TextEntity) {
                    textLayers.put(e.layer.id, e.layer.wrap())
                }
            }


            DataUtil(this).getCard(groupId, cardId!!) { card ->
                Log.d("kjh", "delete")
                motionView.deleteAllEntities()

                card.textLayers.forEach {
                    textLayers.put(it.value.id, it.value)
                    addTextSticker(it.value.toTextLayer())
                }
                card.imageLayers.forEach {
                    imageLayers.put(it.value.id, it.value)
                    addSticker(it.value.toLayer())
                }
            }
        }

        btn_card_done.setOnClickListener {
            Log.d("kjh", "button click")
            onClickComplete()
        }
        image_card.setOnClickListener { onClickImage() }
        text_entity_delete.setOnClickListener { deleteSticker() }

    }

    private fun setMotionViewBackground() {
        Glide.with(this).asBitmap().load(photoUrl).into(object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>) {
                main_motion_view.background = BitmapDrawable(resources, resource)
            }
        })
    }

    fun onClickImage() {
        val intent = Intent()
        intent.action = android.content.Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST);
    }

    companion object {
        val SELECT_STICKER_REQUEST_CODE = 123
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_STICKER_REQUEST_CODE) {
            if (data != null) {

                @DrawableRes var stickerId: Int = data.getIntExtra(StickerSelectActivity.EXTRA_STICKER_ID, 0)
                if (stickerId != 0) {
                    addSticker(stickerId)
                }
            }
        } else if (requestCode == GALLERY_REQUEST) {
            Log.d("kjh", data?.data.toString())
            photoUrl = data?.data.toString()

            setMotionViewBackground()
        }

    }

    fun convertImageAsFile() {
        val bitmap = motionView.drawingCache
        val path = "${Environment.getExternalStorageDirectory().absolutePath}/DCIM/Camera/subcontractor"
        val pathFile = File(path)
        if (!pathFile.exists()) {
            pathFile.mkdir()
        }
        val name = "image_${System.currentTimeMillis()}.jpg"
//        val file = File(path, name)
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, FileOutputStream(file))
        MediaStore.Images.Media.insertImage(contentResolver, bitmap, name, "")
    }

    fun updateStickers(listener: () -> Unit) {

        motionView.entities.forEach {
            if (it is TextEntity) {
                textLayers.put(it.layer.id, it.layer.wrap())
            } else {
                imageLayers.put(it.layer.id, it.layer.wrap())
            }
        }

        if (!edit) {
            val card = Card(UUID.randomUUID().toString(), photoUrl!!, AuthUtil().getUserName(),
                    AuthUtil().getUserId(), System.currentTimeMillis(), System.currentTimeMillis(), textLayers, imageLayers)

            Log.d("kjh", "not edit ${textLayers.size}")
            DataUtil(this).createCard(groupId, card) {
                listener.invoke()
            }
        } else {
            val card = Card(cardId!!, photoUrl!!, AuthUtil().getUserName(),
                    AuthUtil().getUserId(), System.currentTimeMillis(), System.currentTimeMillis(), textLayers, imageLayers)

            Log.d("kjh", "edit ${imageLayers.size} ${textLayers.size}")
            DataUtil(this).updateCard(groupId, card) {
                listener.invoke()
            }
        }
    }

    fun onClickComplete() {
        updateStickers {
            convertImageAsFile()
            onBackPressed()
        }
    }

    /******************************************************************/


    lateinit var motionView: MotionView
    lateinit var textEntityEditPanel: View
    private val motionViewCallback = object : MotionView.MotionViewCallback {
        override fun onEntityUnselected(entity: MotionEntity?) {
            if (edit) {
//                updateStickers {
//
//                }
            }
        }

        override fun onEntitySelected(entity: MotionEntity?) {

            if (entity is TextEntity) {
                textEntityEditPanel.visibility = View.VISIBLE
            } else {
                textEntityEditPanel.visibility = View.GONE
            }

        }

        override fun onEntityDoubleTap(entity: MotionEntity) {
            startTextEntityEditing()
        }
    }
    private var fontProvider: FontProvider? = null


    private fun addSticker(@DrawableRes stickerResId: Int) {
        motionView.post {
            val layer = Layer()
            layer.resId = stickerResId
            val pica = ContextCompat.getDrawable(this, stickerResId)?.let { drawableToBitmap(it) }
            val entity = pica?.let { ImageEntity(layer, it, motionView.measuredWidth, motionView.height) }
            motionView.addEntityAndPosition(entity, true)
        }
    }


    private fun addSticker(layer: Layer) {
        motionView.post {
            val pica = ContextCompat.getDrawable(this, layer.resId)?.let { drawableToBitmap(it) }
            val entity = pica?.let { ImageEntity(layer, it, motionView.measuredWidth, motionView.height) }
            motionView.addEntityAndPosition(entity, false)
        }
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun initTextEntitiesListeners() {
        text_entity_font_size_increase.setOnClickListener { increaseTextEntitySize() }
        text_entity_font_size_decrease.setOnClickListener { decreaseTextEntitySize() }
        text_entity_color_change.setOnClickListener { changeTextEntityColor() }
        text_entity_font_change.setOnClickListener { changeTextEntityFont() }
        text_entity_edit.setOnClickListener { startTextEntityEditing() }
    }

    private fun increaseTextEntitySize() {
        val textEntity = currentTextEntity()
        if (textEntity != null) {
            textEntity.layer.font.increaseSize(TextLayer.Limits.FONT_SIZE_STEP)
            textEntity.updateEntity()
            motionView!!.invalidate()
        }
    }

    private fun decreaseTextEntitySize() {
        val textEntity = currentTextEntity()
        if (textEntity != null) {
            textEntity.layer.font.decreaseSize(TextLayer.Limits.FONT_SIZE_STEP)
            textEntity.updateEntity()
            motionView!!.invalidate()
        }
    }

    private fun changeTextEntityColor() {
        val textEntity = currentTextEntity() ?: return

        val initialColor = textEntity.layer.font.color

        ColorPickerDialogBuilder
                .with(this)
                .setTitle(R.string.select_color)
                .initialColor(initialColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(8) // magic number
                .setPositiveButton(R.string.ok) { dialog, selectedColor, allColors ->
                    val textEntity = currentTextEntity()
                    if (textEntity != null) {
                        textEntity.layer.font.color = selectedColor
                        textEntity.updateEntity()
                        motionView!!.invalidate()
                    }
                }
                .setNegativeButton(R.string.cancel) { dialog, which -> }
                .build()
                .show()
    }

    private fun changeTextEntityFont() {
        val fonts = fontProvider!!.fontNames
        val fontsAdapter = FontsAdapter(this, fonts, fontProvider)
        AlertDialog.Builder(this)
                .setTitle(R.string.select_font)
                .setAdapter(fontsAdapter) { dialogInterface, which ->
                    val textEntity = currentTextEntity()
                    if (textEntity != null) {
                        textEntity.layer.font.typeface = fonts[which]
                        textEntity.updateEntity()
                        motionView!!.invalidate()
                    }
                }
                .show()
    }

    private fun startTextEntityEditing() {
        val textEntity = currentTextEntity()
        if (textEntity != null) {
            val fragment = TextEditorDialogFragment.getInstance(textEntity.layer.text)
            fragment.show(fragmentManager, TextEditorDialogFragment::class.java.name)
        }
    }

    private fun currentTextEntity(): TextEntity? {
        return if (motionView.selectedEntity is TextEntity) {
            motionView.selectedEntity as TextEntity
        } else {
            null
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.card_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.main_add_sticker) {
            val intent = Intent(this, StickerSelectActivity::class.java)
            startActivityForResult(intent, SELECT_STICKER_REQUEST_CODE)
            return true
        } else if (item.itemId == R.id.main_add_text) {
            addTextSticker()
        }
        return super.onOptionsItemSelected(item)
    }


    protected fun addTextSticker() {
        val textLayer = createTextLayer()
        val textEntity = TextEntity(textLayer, motionView.width,
                motionView.height, fontProvider!!)
        motionView.addEntityAndPosition(textEntity, true)

        // move text sticker up so that its not hidden under keyboard
        val center = textEntity.absoluteCenter()
        center.y = center.y * 0.5f
        textEntity.moveCenterTo(center)

        // redraw
        motionView.invalidate()

//        startTextEntityEditing()
    }

    fun deleteSticker() {
        val id = motionView.deleteSelectedEntity()

        if (!textLayers.containsKey(id)) {
            textLayers.remove(id)
        } else if (imageLayers.containsKey(id)) {
            imageLayers.remove(id)
        }

    }

    protected fun addTextSticker(textLayer: TextLayer) {

        val textEntity = TextEntity(textLayer, motionView.width,
                motionView.height, fontProvider!!)
        motionView.addEntityAndPosition(textEntity, false)

        // move text sticker up so that its not hidden under keyboard
        val center = textEntity.absoluteCenter()
        center.y = center.y * 0.5f
//        textEntity.moveCenterTo(center)

        // redraw
        motionView.invalidate()

//        startTextEntityEditing()
    }


    private fun createTextLayer(): TextLayer {
        val textLayer = TextLayer()
        val font = Font()

        font.color = TextLayer.Limits.INITIAL_FONT_COLOR
        font.size = TextLayer.Limits.INITIAL_FONT_SIZE
        font.typeface = fontProvider!!.defaultFontName

        textLayer.font = font

        if (BuildConfig.DEBUG) {
            textLayer.text = "Hello, world :))"
        }
        return textLayer
    }


    override fun textChanged(text: String) {
        val textEntity = currentTextEntity()
        if (textEntity != null) {
            val textLayer = textEntity.layer
            if (text != textLayer.text) {
                textLayer.text = text
                textEntity.updateEntity()
                motionView!!.invalidate()
            }
        }
    }

}
