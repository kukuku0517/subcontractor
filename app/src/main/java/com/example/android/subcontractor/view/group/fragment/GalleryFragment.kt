package com.example.android.subcontractor.view.group

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.Glide.init

import com.example.android.subcontractor.R
import com.example.android.subcontractor.data.Photo
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.util.ImageUtil
import com.example.android.subcontractor.view.group.photo.CustomAdapter
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.fragment_photo.*
import kotlinx.android.synthetic.main.item_photo.view.*
import java.util.*
import android.R.attr.action
import android.content.pm.ResolveInfo
import com.facebook.FacebookSdk.getApplicationContext
import android.content.pm.PackageManager
import android.content.ClipData
import android.R.attr.data
import android.os.Build
import android.support.v4.app.NotificationCompat.getExtras
import android.R.attr.action
import com.example.android.subcontractor.view.CardActivity


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GalleryFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {

    // TODO: Rename and change types of parameters
    lateinit var groupId: String


    override fun onResume() {
        super.onResume()
        initData()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            groupId = arguments!!.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        initData()
    }

    val GALLERY_REQUEST = 1
    lateinit var adapter: CustomAdapter<Photo>
    var action: String = "android.intent.action.MULTIPLE_PICK"
    fun init() {
        adapter = CustomAdapter(R.layout.item_photo) { holder, data ->
            val view = holder?.itemView
            Glide.with(this).load(data.url).into(view?.image_photo_item)
            view?.setOnClickListener {
                Toast.makeText(context, data.url, Toast.LENGTH_SHORT).show()
                val intent = Intent(context,CardActivity::class.java)
                intent.putExtra("photoUrl",data.url)
                intent.putExtra("groupId",groupId)
                intent.putExtra("edit",true)
                startActivity(intent)
            }
        }
        recycler_gallery.adapter = adapter
        recycler_gallery.layoutManager = GridLayoutManager(context, 5)

        btn_gallery_new.setOnClickListener {

            // Undocumented way to get multiple photo selections from Android Gallery ( on Samsung )
            val intent = Intent(action)//("Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"

            val infos = getApplicationContext().packageManager.queryIntentActivities(intent, 0)

            if (infos.size == 0) {
                action = Intent.ACTION_GET_CONTENT
                intent.action = Intent.ACTION_GET_CONTENT
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Note: only supported after Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT, harmless if used below 19, but no mutliple selection supported
            }
            startActivityForResult(intent, GALLERY_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val list = ImageUtil(context).getMultipleImagePath(action, data)

        DataUtil(context).uploadMultiplePhoto(groupId,list){
            Toast.makeText(context,"suc",Toast.LENGTH_SHORT).show()
        }
//        val photo = Photo(UUID.randomUUID().toString(), ImageUtil(context).getRealPathFromURI(data?.data), System.currentTimeMillis())
//        DataUtil(context).uploadPhoto(photo, groupId) {
//            Toast.makeText(context, "upload complete", Toast.LENGTH_SHORT).show()
//        }

    }

    fun initData() {
        DataUtil(context).getPhotoByGroup(groupId) { photos ->
            adapter.updateData(photos)
        }
    }


    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param groupId Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GalleryFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(groupId: String): GalleryFragment {
            val fragment = GalleryFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, groupId)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
