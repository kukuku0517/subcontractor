package com.example.android.subcontractor.util

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import android.provider.MediaStore
import android.content.ClipData
import android.R.attr.data
import android.os.Build
import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle
import android.R.attr.action
import android.content.Intent
import android.util.Log


/**
 * Created by samsung on 2018-01-19.
 */
class ImageUtil(context: Context?) {
    val context = context
    val fs = FirebaseStorage.getInstance().reference

    fun uploadImage(path: String, listener: (path: String) -> Unit) {
        val ref = fs.child(AuthUtil().getUserId()).child("${System.currentTimeMillis()}")
        val uploadTask = ref.putFile(Uri.fromFile(File(getRealPathFromURI(Uri.parse(path)))))
        uploadTask.addOnSuccessListener { snapshot ->
            val uri = snapshot.downloadUrl.toString()
            listener.invoke(uri)
        }.addOnFailureListener {

        }
    }

    fun getRealPathFromURI(uri: Uri?): String {
        val cursor = context!!.contentResolver.query(uri, null, null, null, null)
        cursor.moveToFirst()
        val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
        return cursor.getString(idx)
    }

    fun getMultipleImagePath(action: String, data: Intent?): MutableList<String> {
        var list: MutableList<String> = mutableListOf()
        if (action == "android.intent.action.MULTIPLE_PICK") {
            val extras = data?.extras
//            val count = extras?.getInt("selectedCount")

            extras?.getParcelableArrayList<Uri>("selectedItems")?.mapTo(list){it.toString()}


            // do somthing
        } else {
            if (data?.data != null) {
                val uri = data.data
                // do somthing
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    val clipData = data?.clipData
                    if (clipData != null) {
                        (0 until clipData.itemCount)
                                .mapTo(list) { clipData!!.getItemAt(it).uri.toString() }
                        // Do someting
                    }
                }
            }
        }
        return list
    }
}