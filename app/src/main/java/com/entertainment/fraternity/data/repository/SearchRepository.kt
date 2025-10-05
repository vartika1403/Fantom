package com.entertainment.fraternity.data.repository

import android.util.Log
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

class SearchRepository {

    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val databaseReference: DatabaseReference by lazy {
        firebaseDatabase.getReferenceFromUrl(Conf.firebaseDomainUri())
    }

    fun loadSearchResult(query: String): Flow<Resource<List<DetailObject>>> {

        return flow<Resource<List<DetailObject>>> {
            val list: MutableList<DetailObject> = mutableListOf()
            try {
                val snapshot = databaseReference.child(query).get().await()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        Log.d(
                            SearchRepository::class.java.toString(),
                            "snapshot data 1," + ", key," + data.key
                        )
                        val detailObject = data.getValue(
                            DetailObject::class.java
                        )
                        //detailObject?.name = data.key
                        val fbLink = detailObject?.fbLink
                        val webLink = detailObject?.webLink
                        val imageUrl = detailObject?.image
                        Log.d(
                            SearchRepository::class.java.toString(),
                            "snapshot data 1 2," + fbLink + ", webLink," + webLink +
                                    " image url," + imageUrl
                        )

                        if (detailObject != null) {
                            list.add(detailObject)
                        }
                    }
                    emit(Resource.Success(data = list))
                }
            } catch (e: Exception) {
                emit(Resource.Error(error = e.message ?: "Sorry data not found"))
            }
        }.flowOn(Dispatchers.IO).catch { e ->
            emit(
                Resource.Error(
                    error = e.message ?: "Some error occurred , please try agai"
                )
            )
        }

    }
}