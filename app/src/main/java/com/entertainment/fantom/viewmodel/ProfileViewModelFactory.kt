package com.entertainment.fantom.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.entertainment.fantom.data.repository.ProfileRepository

class ProfileViewModelFactory(
    private var application: Activity?,
    private var profileRepository: ProfileRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(application, profileRepository) as T
    }
}