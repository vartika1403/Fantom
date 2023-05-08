package com.entertainment.fantom.repository

import android.content.Context
import com.entertainment.fantom.DetailObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import com.entertainment.fantom.utils.Resource
import com.google.firebase.database.*

class ProfileRepository {
    val firebaseDatabase : FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }


        suspend fun saveDate(detailObject: DetailObject): Flow<Resource<String>> {
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
    }


