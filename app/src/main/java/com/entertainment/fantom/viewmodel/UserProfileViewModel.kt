package com.entertainment.fantom.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.Resource
import com.entertainment.fantom.data.repository.FetchUserDetailsRepository
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val context: Context?,
    private val fetchUserDetailsRepository:
    FetchUserDetailsRepository
) : ViewModel() {

    private val sharedPreferences: SharedPreferences? by lazy {
        context?.getSharedPreferences("app", Context.MODE_PRIVATE)
    }

    private val _userDetailObjectLiveData = MutableLiveData<Resource<DetailObject>>()
    val userDetailObjectLiveData: LiveData<Resource<DetailObject>> = _userDetailObjectLiveData

    fun fetchUserDetails() {
        viewModelScope.launch {
            val userId = sharedPreferences?.getString("userId", "") ?: ""
            fetchUserDetailsRepository.fetchUserDetails(userId).collect { resource ->
                _userDetailObjectLiveData.postValue(resource)

            }
        }
    }
}