package com.entertainment.fantom.data.repository

import android.util.Log
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.zip

class ProfileRepository {

    val saveUserDataRepository = SaveUserDataRepository()
    val saveCategoryRepository = SaveCategoryRepository()

    fun saveData(detailObject: DetailObject): Flow<Resource<String>> {
        val profileResultFlow = saveUserDataRepository.saveUserData(detailObject)
        val categoryResultFlow = saveCategoryRepository.saveCategoryDetail(detailObject)

        val combinedResult: Flow<Resource<String>> =
            profileResultFlow.zip(categoryResultFlow) { result1, result2 ->
                if (result1 is Resource.Success && result2 is Resource.Success) {
                    Resource.Success(data = "success")
                } else if (result1 is Resource.Error) {
                    Resource.Error(error = result1.error)
                } else if (result2 is Resource.Error) {
                    Resource.Error(error = result2.error)
                } else {
                    Resource.Loading
                }
            }

        Log.d(
            "vartika",
            "combined datasnapshot " + combinedResult
        )
        return combinedResult
    }
}


