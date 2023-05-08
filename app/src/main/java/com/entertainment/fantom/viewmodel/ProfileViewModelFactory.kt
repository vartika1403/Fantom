package com.entertainment.fantom.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.entertainment.fantom.repository.ProfileRepository

class ProfileViewModelFactory : ViewModelProvider.Factory {
    private var application: Activity?
    private var profileRepository: ProfileRepository

    constructor(application: Activity?, profileRepository: ProfileRepository){
        this.application = application
        this.profileRepository = profileRepository
    }

    override  fun <T : ViewModel> create(modelClass :Class<T>): T {
        return ProfileViewModel(application,profileRepository) as T
    }
}