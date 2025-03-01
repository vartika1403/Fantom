package com.entertainment.fantom.data.repository

import android.util.Log
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ProfileRepository {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    private fun updateFlow(boolean: Boolean) {

    }

    suspend fun saveProfileData(detailObject: DetailObject): Flow<Resource<String>> {
        return flow<Resource<String>> {
            var result = false
            var exception = ""
            val userId = detailObject.userId
            val databaseReference = firebaseDatabase.getReference("users" + "/" + userId)
            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseReference.setValue(detailObject)
                    //  this@callbackFlow.trySendBlocking(Resource.Success("Data is saved successfully"))
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("loadPost:onCancelled ${databaseError.toException()}")
                    result = false
                    exception = "exception: ${databaseError.toException()}"
                    //   this@callbackFlow.trySendBlocking(Resource.Error(exception))

                }
            }

            databaseReference.orderByChild("phoneNumber").equalTo(detailObject.phoneNum)
                .addValueEventListener(menuListener)

            // awaitClose { databaseReference.removeEventListener(menuListener) }
        }.flowOn(Dispatchers.IO)
    }

    fun saveCategoryDataUtil(detailObject: DetailObject): Flow<Resource<String>> {
        return flow<Resource<String>> {
            val category = detailObject.category
            var result = false
            var exception = ""
            val databaseReference = firebaseDatabase.reference.child("categories").child(category)
            databaseReference.orderByChild("userId").equalTo(detailObject.userId)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            Log.d(
                                "vartika",
                                "category datasnapshot " + dataSnapshot + " database ref: " + databaseReference
                            )
                            databaseReference.child(detailObject.name).setValue(detailObject)
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
        }.flowOn(Dispatchers.IO).catch { e -> emit(Resource.Error(error = e.toString())) }
    }
}


