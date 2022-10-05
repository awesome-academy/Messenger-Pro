package com.example.finalapplication.data.repository.resource.remote

import androidx.core.net.toUri
import com.example.finalapplication.data.model.Account
import com.example.finalapplication.data.model.User
import com.example.finalapplication.data.repository.resource.Listenner
import com.example.finalapplication.data.repository.resource.UserDataSource
import com.example.finalapplication.utils.Constant
import com.example.finalapplication.utils.NumberConstant
import com.example.finalapplication.utils.getNewid
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage

class RemoteUser : UserDataSource.Remote {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore
    private val storage = FirebaseStorage.getInstance().reference
    private val fcm = FirebaseMessaging.getInstance()

    override suspend fun getUser(id: String?, listen: Listenner<User>) {
        val currentUser = auth.currentUser
        if (currentUser == null && id.isNullOrEmpty()) listen.onError(Constant.ERROR_USER)
        else {
            val uid = if (id.isNullOrEmpty()) currentUser?.uid
            else id
            database.collection(User.users)
                .document(uid.toString())
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        listen.onError(e.toString())
                        return@addSnapshotListener
                    }
                    snapshot?.let {
                        val user = it.toObject(User::class.java)
                        if (user != null) {
                            listen.onSuccess(user)
                        } else listen.onError(Constant.ERROR_USER)
                    }
                }
        }
    }

    override suspend fun loginUser(account: Account, listen: Listenner<Boolean>) {
        auth.signInWithEmailAndPassword(account.email, account.password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    database.collection(User.users)
                        .document(auth.uid.toString())
                        .update(mapOf("${User.account}.${Account.password}" to account.password))
                    listen.onSuccess(true)
                } else listen.onError(task.exception.toString())
            }
    }

    override suspend fun registerUser(user: User, listen: Listenner<Boolean>) {
        user.userAccount?.also {
            auth.createUserWithEmailAndPassword(it.email, it.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        auth.signInWithEmailAndPassword(it.email, it.password)
                        user.id = auth.uid
                        database.collection(User.users)
                            .document(auth.uid.toString())
                            .set(user)
                            .addOnCompleteListener { tasks ->
                                if (tasks.isSuccessful) listen.onSuccess(true)
                                else listen.onSuccess(false)
                            }
                    } else listen.onError(task.exception.toString())
                }
        }
    }

    override suspend fun updateProfile(user: User, listen: Listenner<Boolean>) {
        database.collection(User.users)
            .document(auth.uid.toString())
            .set(user)
            .addOnCompleteListener { tasks ->
                if (tasks.isSuccessful) listen.onSuccess(true)
                else listen.onSuccess(false)
            }
    }

    override suspend fun updatePassword(user: User, listen: Listenner<Boolean>) {
        user.userAccount?.let {
            auth.currentUser?.updatePassword(it.password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        database.collection(User.users)
                            .document(auth.uid.toString())
                            .set(user)
                            .addOnCompleteListener { tasks ->
                                if (tasks.isSuccessful) listen.onSuccess(true)
                                else listen.onSuccess(false)
                            }
                    } else listen.onError(task.exception.toString())
                }
        }
    }

    override suspend fun updateAvatar(user: User, listen: Listenner<Boolean>) {
        val newid = getNewid()
        user.avatar?.let {
            storage
                .child("image/$newid")
                .putFile(it.toUri())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storage.child("image/$newid")
                            .downloadUrl
                            .addOnSuccessListener { uri ->
                                user.avatar = uri.toString()
                                database.collection(User.users)
                                    .document(auth.uid.toString())
                                    .set(user)
                                    .addOnCompleteListener { tasks ->
                                        if (tasks.isSuccessful) listen.onSuccess(true)
                                        else listen.onSuccess(false)
                                    }
                            }

                    } else listen.onError(task.exception.toString())
                }
        }


    }

    override suspend fun forgotPassword(email: String, listen: Listenner<Boolean>) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) listen.onSuccess(true)
                else listen.onError(task.exception.toString())
            }
    }

    override fun updateStatus(status: Boolean) {
        if (status) {
            fcm.token.addOnSuccessListener { data ->
                database.collection(User.users)
                    .document(auth.uid.toString())
                    .update(mapOf(User.isOnline to status, User.fcmToken to data))
            }
        } else {
            database.collection(User.users)
                .document(auth.uid.toString())
                .update(mapOf(User.isOnline to status))
        }
    }

    override suspend fun getUserByName(
        name: String,
        lastIndex: String,
        listen: Listenner<List<User>>
    ) {
        database.collection(User.users)
            .whereEqualTo(User.name, name)
            .orderBy(User.id)
            .whereGreaterThan(User.id, lastIndex)
            .limit(NumberConstant.ITEM_PER_PAGE.toLong())
            .addSnapshotListener { value, error ->
                if (error != null) {
                    listen.onError(error.toString())
                    return@addSnapshotListener
                }
                val users = mutableListOf<User>()
                if (value != null) {
                    for (doc in value.documents) {
                        val user = doc.toObject(User::class.java)
                        if (user != null) users.add(user)
                    }
                }
                if (users.size > 0) listen.onSuccess(users)
                else listen.onError(Constant.MSG_NO_DATA)
            }
    }

    override fun logout(listen: Listenner<Boolean>) {
        database.collection(User.users)
            .document(auth.uid.toString())
            .update(mapOf(User.fcmToken to null, User.isOnline to false))
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    auth.signOut()
                    listen.onSuccess(true)
                }
            }
    }
}
