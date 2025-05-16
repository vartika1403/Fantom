package com.entertainment.fantom.data.repository

import com.entertainment.fantom.Conf
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.Resource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class SaveUserDataRepository {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val userDatabase by lazy {
        firebaseDatabase.getReferenceFromUrl(Conf.FIREBASE_USER_URI)
    }

    fun saveUserData(detailObject: DetailObject): Flow<Resource<String>> {
        return flow {
            var result = ""
            var exception = ""
            val userId = detailObject.userId
            try {
                val userRef = userDatabase.child(userId)
                val snapshot = userRef.get().await()
                if (snapshot.exists()) {
                    userRef.setValue(detailObject).await()
                    emit(Resource.Success(data = "success"))
                } else {
                    userRef.setValue(detailObject).await()
                    emit(Resource.Error(error = "Can not submit data"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(error = e.toString()))
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(Resource.Error(error = e.message ?: "")) }

    }
}