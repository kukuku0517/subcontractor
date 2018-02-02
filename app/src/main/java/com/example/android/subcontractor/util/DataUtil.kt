package com.example.android.subcontractor.util


import android.content.Context
import android.util.Log
import com.example.android.subcontractor.data.*
import com.example.android.subcontractor.data.motionview.Member
import com.google.firebase.firestore.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by samsung on 2018-01-18.
 */
class DataUtil(context: Context?) {
    val context = context
    val fb = FirebaseFirestore.getInstance()

    val GROUP = "groups"
    val CARD = "cards"
    val MEMBER = "members"
    val USER = "users"
    val PHOTO = "photos"
    val LIKE = "likes"
    /************************ create ******************************/

    fun createGroup(group: Group, listener: () -> (Unit)) {
        fb.runTransaction { transaction ->
            transaction.set(fb.collection(GROUP).document(group.id), group)
            transaction.set(fb.collection(USER).document(AuthUtil().getUserId()).collection(GROUP).document(group.id), GroupId(group.id))
        }.addOnCompleteListener {
            val it = group.member?.iterator()
            while (it!!.hasNext()) {
                val id = it.next().value
                val member = Member(id, id == AuthUtil().getUserId())
                fb.collection(GROUP).document(group.id).collection(MEMBER).document(member.id).set(member).addOnSuccessListener {
                    listener.invoke()
                }

            }
        }
    }

    fun createCard(groupId: String, card: Card, listener: () -> Unit) {
        val groupRef = fb.collection(GROUP).document(groupId)
        val cardRef = fb.collection(GROUP).document(groupId).collection(CARD).document(card.id)
        if (card.photoUrl != "") {
        } else {
        }
        ImageUtil(context).uploadImage(card.photoUrl) { path ->
            card.photoUrl = path
            fb.runTransaction { transaction ->
                val groupUrl = transaction.get(groupRef).toObject(Group::class.java).photoUrl
                groupUrl?.let {
                    if (groupUrl == "") {
                        transaction.update(groupRef, "photoUrl", card.photoUrl)
                    }
                }
                transaction.update(groupRef, "photoUrl", path)//????????????????????????TODO
                transaction.set(cardRef, card)
            }.addOnSuccessListener {
                listener.invoke()
            }
        }
    }

    fun updateCard(groupId: String, card: Card, listener: () -> Unit) {
        val cardRef = fb.collection(GROUP).document(groupId).collection(CARD).document(card.id)
//        val map = mutableMapOf<String, Any>()
//        map.put("updateTime",card.updateTime)
//        map.put("travelTime",card.travelTime)
//        if(card.textLayers.isNotEmpty())map.put("textLayers",card.textLayers)
//        if(card.imageLayers.isNotEmpty()) map.put("imageLayers",card.imageLayers)

        cardRef.set(card, SetOptions.merge()).addOnSuccessListener {
            listener.invoke()
        }

    }

    fun createNewUser(user: User, listener: () -> Unit) {
        fb.collection(USER).document(user.id).set(user, SetOptions.merge()).addOnCompleteListener {
            listener.invoke()
        }
    }

    fun uploadPhoto(photo: Photo, groupId: String, listener: () -> Unit) {
        ImageUtil(context).uploadImage(photo.url) { path ->
            photo.url = path
            fb.collection(GROUP).document(groupId).collection(PHOTO).document(photo.id).set(photo).addOnCompleteListener {
                listener.invoke()
            }
        }
    }

    fun uploadMultiplePhoto(groupId: String, list: MutableList<String>, listener: () -> Unit) {
        for (path in list) {
            ImageUtil(context).uploadImage(path) { path ->
                val photo = Photo(UUID.randomUUID().toString(), path, System.currentTimeMillis())
                fb.collection(GROUP).document(groupId).collection(PHOTO).document(photo.id).set(photo).addOnSuccessListener {
                    listener.invoke()
                }.addOnFailureListener {

                }
            }
        }

    }

    /************************ read ******************************/

    fun getGroup(groupId: String, gListener: (data: Group) -> (Unit), mListener: (members: User) -> Unit) {
        fb.collection(GROUP).document(groupId).get().addOnCompleteListener { task ->
            val groupData = task.result.toObject(Group::class.java)
            gListener.invoke(groupData)
            getMembersByGroup(groupId) { user, manager ->
                mListener.invoke(user)
            }
        }
    }


    fun getMyGroup(listener: (group: Group) -> Unit) {
        fb.collection(USER).document(AuthUtil().getUserId()).collection(GROUP).get().addOnCompleteListener { task ->
            val groupIds = task.result.documents.mapTo(mutableListOf()) { it.toObject(GroupId::class.java) }
            for (g in groupIds) {
                fb.collection(GROUP).whereEqualTo("id", g.id).get().addOnCompleteListener { task ->
                    if (!task.result.isEmpty) {
                        val group = task.result.documents.get(0).toObject(Group::class.java)
                        listener.invoke(group)
                    }
                }
            }
        }
    }

    fun getMembersByGroup(groupId: String, listener: (user: User, isManager: Boolean) -> Unit) {
        fb.collection(GROUP).document(groupId).collection(MEMBER).get().addOnCompleteListener { task ->
            val ids = task.result.documents.mapTo(mutableListOf()) { it.toObject(Member::class.java) }
            for (id in ids) {
                fb.collection(USER).document(id.id).get().addOnSuccessListener { task ->
                    listener.invoke(task.toObject(User::class.java), id.manager)
                }
            }
        }
    }

    fun getGroupsByFlag(flag: Int, listener: (MutableList<Group>) -> Unit) {
        when (flag) {
            0 -> fb.collection(GROUP).whereEqualTo("open", true).orderBy("timestamp", Query.Direction.DESCENDING)
            1 -> fb.collection(GROUP).whereEqualTo("open", true).orderBy("likes", Query.Direction.DESCENDING)
            else -> return
        }.get().addOnCompleteListener { task ->
            val groups = task.result.documents.mapTo(mutableListOf()) { it.toObject(Group::class.java) }
            listener.invoke(groups)

        }
    }

    fun getCardsFromGroup(groupId: String, listener: (MutableList<Card>) -> Unit) {
        fb.collection(GROUP).document(groupId).collection(CARD).orderBy("travelTime",Query.Direction.DESCENDING).get().addOnCompleteListener { task ->
            val list: MutableList<Card> = mutableListOf()
            task.result.documents.mapTo(list) { it.toObject(Card::class.java) }
            listener.invoke(list)
        }
    }

    fun getCard(groupId: String, cardId: String, listener: (card: Card) -> Unit) {
        fb.collection(GROUP).document(groupId).collection(CARD).document(cardId).addSnapshotListener { task, e ->
            Log.d("kjh", "getcard snap shot called")
            val card = task.toObject(Card::class.java)
            listener.invoke(card)
        }
    }

    fun getAllUsers(listener: (MutableList<User>) -> Unit) {
        fb.collection(USER).get().addOnCompleteListener { task ->
            val users = task.result.documents.mapTo(mutableListOf()) { it.toObject(User::class.java) }
            listener.invoke(users)
        }
    }

    fun getAllUsersAsMap(listener: (MutableMap<String, User>) -> Unit) {
        fb.collection(USER).get().addOnCompleteListener { task ->

            val users = task.result.documents.associateTo(mutableMapOf()) { it.toObject(User::class.java).id to it.toObject(User::class.java) }
            listener.invoke(users)
        }
    }

    //TODO will be using parcelable...
    fun getUsersFrom(list: ArrayList<String>, listener: (user: User) -> Unit) {
        for (id in list) {
            fb.collection(USER).document(id).get().addOnSuccessListener { task ->
                listener.invoke(task.toObject(User::class.java))
            }
        }
    }

    fun getPhotoByGroup(groupId: String?, listener: (photos: MutableList<Photo>) -> Unit) {
        groupId?.let {
            fb.collection(GROUP).document(groupId).collection(PHOTO).get().addOnCompleteListener { task ->
                val photos = task.result.documents.mapTo(mutableListOf()) { it.toObject(Photo::class.java) }
                listener.invoke(photos)
            }
        }
    }

    /****************************update****************************/

    fun setMemberToManager(groupId: String, userId: String, listener: () -> Unit) {
        fb.collection(GROUP).document(groupId).collection(MEMBER).document(userId)
                .update("manager", true).addOnSuccessListener {
            Log.d("kjh", "suc")
            listener.invoke()

        }
    }

    //TODO refactor to transaction
    fun getLikeToGroup(groupId: String, listener: (isLike: Boolean, count: Int) -> Unit) {
        val groupRef = fb.collection(GROUP).document(groupId)
        val likeRef = fb.collection(GROUP).document(groupId).collection(LIKE).document(AuthUtil().getUserId())

//        var isLike = false
//        var count = 0
//        Log.d("kjh", "get")
//        fb.runTransaction { transaction ->
//            count = transaction.get(groupRef).toObject(Group::class.java).likes
//            Log.d("kjh", "get count $count")
//            val likes = transaction.get(likeRef)
//            Log.d("kjh", "get likes")
////            isLike = likes.exists()
//            Log.d("kjh", "like exist $isLike")
//
//        }.addOnSuccessListener {
//            Log.d("kjh", "get suc")
//
//            listener.invoke(isLike, count)
//        }.addOnFailureListener { e ->
//            Log.d("kjh", "get suc $e")
//        }
//
        likeRef.get().addOnSuccessListener { task ->
            var isLike= task.exists()
            groupRef.collection(LIKE).get().addOnSuccessListener { task ->
                listener.invoke(isLike,task.documents.size)
            }
        }
    }

    fun setLikeToGroup(groupId: String, listener: (isLike: Boolean, count: Int) -> Unit) {
        val groupRef = fb.collection(GROUP).document(groupId)
        val groupLikeRef = fb.collection(GROUP).document(groupId).collection(LIKE)
        val likeRef = fb.collection(GROUP).document(groupId).collection(LIKE).document(AuthUtil().getUserId())

//        var isLike = false
//        var count = 0
//        fb.runTransaction { transaction ->
//            count = transaction.get(groupRef).toObject(Group::class.java).likes
//            val like = transaction.get(likeRef)
//            isLike = if (like.exists()) {
//                Log.d("kkjh", "disliked")
//                transaction.update(groupRef, "likes", count - 1)
//                transaction.delete(likeRef)
//                false
//            } else {
//                Log.d("kkjh", "liked")
//                transaction.update(groupRef, "likes", count + 1)
//                transaction.set(likeRef, UserId(AuthUtil().getUserId()))
//                true
//            }
//        }.addOnSuccessListener {
//            Log.d("kkjh", "transaction suc")
//            if (isLike) count++
//            else count--
//            listener.invoke(isLike, count)
//        }

        likeRef.get().addOnSuccessListener { task ->
            val isLike = task.exists()
            if(isLike){
                likeRef.delete().addOnSuccessListener {
                    groupLikeRef.get().addOnSuccessListener { task ->
                        listener.invoke(isLike,task.documents.size)
                    }
                }
            }else{
                likeRef.set(UserId(AuthUtil().getUserId())).addOnSuccessListener {
                    groupLikeRef.get().addOnSuccessListener { task ->
                        listener.invoke(isLike,task.documents.size)
                    }
                }
            }

        }

    }
}