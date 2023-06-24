package com.entertainment.fantom.repository

import android.util.Log
import com.entertainment.fantom.DetailObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.entertainment.fantom.utils.Resource
import com.google.firebase.database.*

class ProfileRepository {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    suspend fun saveProfileData(detailObject: DetailObject): Flow<Resource<String>> {
        return flow {
            var result = false
            var exception = ""
            val userId = detailObject.userId
            val databaseReference = firebaseDatabase.getReference("users" + "/" + userId)
            val menuListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    databaseReference.setValue(detailObject)
                    result = true
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    println("loadPost:onCancelled ${databaseError.toException()}")
                    result = false
                    exception = "exception: ${databaseError.toException()}"
                }
            }

            databaseReference.orderByChild("phoneNumber").equalTo(detailObject.phoneNum)
                .addValueEventListener(menuListener)
            if (result) {
                emit(Resource.Success("Data is saved successfully"))
            } else {
                emit(Resource.Error(error = exception))
            }

        }.flowOn(Dispatchers.IO)
    }

    fun saveCategoryDataUtil(detailObject: DetailObject): Flow<Resource<String>> {
        return flow {
            val category = detailObject.category
            var result = false
            var exception = ""
            val databaseReference = firebaseDatabase.reference.child("categories").child(category)
            databaseReference.orderByChild("userId").equalTo(detailObject.userId).addListenerForSingleValueEvent(
                object :ValueEventListener{
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        Log.d("vartika", "category datasnapshot " + dataSnapshot + " database ref: " + databaseReference)
                            databaseReference.child(detailObject.name).setValue(detailObject)
                    }

                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }
    }
}


