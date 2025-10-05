package com.entertainment.fraternity.data.repository

import com.entertainment.fraternity.Conf
import com.entertainment.fraternity.DetailObject
import com.entertainment.fraternity.data.Resource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class FetchUserDetailsRepository {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }
    val userDatabase by lazy {
        firebaseDatabase.getReferenceFromUrl(Conf.FIREBASE_USER_URI)
    }

    fun fetchUserDetails(userId: String): Flow<Resource<DetailObject>> {
        return flow {
            try {
                val snapshot = userDatabase.child(userId).get().await()
                if (snapshot.exists()) {
                    val detailObject = snapshot.getValue(DetailObject::class.java)
                    detailObject?.let {
                        emit(Resource.Success(data = it))
                    } ?: throw Exception("User data is invalid")
                } else {
                    emit(Resource.Error(error = "User data not found"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(error = e.message ?: "Some error occurred , please try again"))
            }

        }.flowOn(Dispatchers.IO).catch { e ->
            emit(
                Resource.Error(
                    error = e.message ?: "Some error occurred , please try again"
                )
            )
        }
    }

}