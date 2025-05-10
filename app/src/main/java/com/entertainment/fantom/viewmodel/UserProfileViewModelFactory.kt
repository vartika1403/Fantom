package com.entertainment.fantom.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.entertainment.fantom.data.repository.FetchUserDetailsRepository

class UserProfileViewModelFactory(
    private var context: Activity?,
    private val fetchUserDetailsRepository: FetchUserDetailsRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserProfileViewModel(context, fetchUserDetailsRepository) as T
    }
}