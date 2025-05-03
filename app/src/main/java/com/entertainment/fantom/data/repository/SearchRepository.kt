package com.entertainment.fantom.data.repository

import android.util.Log
import com.entertainment.fantom.Conf
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class SearchRepository {

    val firebaseDatabase: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance()
    }

    val databaseReference: DatabaseReference by lazy {
        firebaseDatabase.getReferenceFromUrl(Conf.firebaseDomainUri())
    }

    fun loadSearchResult(query: String): Flow<Resource<List<DetailObject>>> {

        return flow<Resource<List<DetailObject>>> {
            val list: MutableList<DetailObject> = mutableListOf();
            emit(Resource.Loading)
            databaseReference.child(query).addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (data in dataSnapshot.children) {
                        Log.d(
                            SearchRepository::class.java.toString(),
                            "snapshot data 1," + ", key," + data.key
                        )
                        try {
                            val detailObject = data.getValue(
                                DetailObject::class.java
                            )

                            detailObject?.name = data.key
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        Log.d(SearchRepository::class.java.toString(), "snapshot data 2, $data")
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("Count ", "databaseError " + databaseError.message)
                }
            })
            /* if (list.isNotEmpty()) {
                 emit(Resource.Success(data = list))
             } else {
                 emit(Resource.Error("No data found"))
             }*/
            emit(Resource.Success(data = list))
        }.flowOn(Dispatchers.IO)

    }
}