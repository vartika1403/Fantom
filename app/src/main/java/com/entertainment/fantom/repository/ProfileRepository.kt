package com.entertainment.fantom.repository

import android.content.Context
import com.bumptech.glide.load.engine.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ProfileRepository {
    suspend open fun loadData(context: Context): Flow<Any> {
        return flow {


        }
    }


}