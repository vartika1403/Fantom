package com.entertainment.fraternity.data.repository

import com.entertainment.fraternity.Conf
import com.entertainment.fraternity.DetailObject
import com.entertainment.fraternity.data.Resource
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class SaveCategoryRepository {
    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val categoryDatabaseReference: DatabaseReference by lazy {
        firebaseDatabase.getReferenceFromUrl(Conf.firebaseDomainUri())
    }

    fun saveCategoryDetail(detailObject: DetailObject): Flow<Resource<String>> {
        return flow {
            val category = detailObject.category
            var result = ""
            var exception = ""
            try {
                val databaseReference =
                    categoryDatabaseReference.child(category)
                val userRef =
                    databaseReference.orderByChild("userId").equalTo(detailObject.userId)
                val snapshot =
                    userRef.get()
                        .await()
                if (snapshot.exists()) {
                    databaseReference.child(detailObject.userId).setValue(detailObject).await()
                    emit(Resource.Success(data = "success"))
                } else {
                    databaseReference.child(detailObject.userId).setValue(detailObject).await()
                    emit(Resource.Error(error = "Can not submit data"))
                }
            } catch (e: Exception) {
                emit(Resource.Error(error = e.toString()))
            }
        }.flowOn(Dispatchers.IO).catch { e -> emit(Resource.Error(error = e.toString())) }
    }

}