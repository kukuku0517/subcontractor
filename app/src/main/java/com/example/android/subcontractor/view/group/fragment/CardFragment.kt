package com.example.android.subcontractor.view.group

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide.init

import com.example.android.subcontractor.R
import com.example.android.subcontractor.R.id.recycler_group_cards
import com.example.android.subcontractor.util.DataUtil
import com.example.android.subcontractor.view.CardActivity
import kotlinx.android.synthetic.main.fragment_card.*
import kotlinx.android.synthetic.main.fragment_card.view.*

class CardFragment : Fragment() {

    lateinit var groupId: String
    lateinit var adapter: CardAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            groupId = arguments!!.getString(ARG_PARAM1)
        }
    }

    override fun onResume() {
        super.onResume()
        initData()
    }


    fun initData() {
        DataUtil(context).getCardsFromGroup(groupId, { list ->

            refresh_layout_group?.let {
                adapter.updateData(list)
                it.isRefreshing = false
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.fragment_card, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()

        initData()
    }

    private fun init() {
        adapter = CardAdapter { card ->
            val intent = Intent(context, CardActivity::class.java)
            intent.putExtra("cardId", card.id)
            intent.putExtra("groupId", groupId)
            intent.putExtra("edit", true)
            intent.putExtra("photoUrl", card.photoUrl)
            startActivity(intent)
        }
        recycler_group_cards.adapter = adapter
        recycler_group_cards.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        refresh_layout_group.setOnRefreshListener {
            initData()
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
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CardFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String): CardFragment {
            val fragment = CardFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
