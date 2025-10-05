package com.entertainment.fraternity.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entertainment.fraternity.DetailObject
import com.entertainment.fraternity.data.Resource
import com.entertainment.fraternity.data.repository.ProfileRepository
import com.entertainment.fraternity.utils.isValidEmail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val context: Context?,
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _userName = MutableStateFlow("")
    private val _emailAddress = MutableStateFlow("")
    private val _webLink = MutableStateFlow("")
    private val _instagramLink = MutableStateFlow("")
    private val _categoryName = MutableStateFlow("")
    private val _detailObjectLiveData = MutableLiveData<Resource<String>>()
    val detailObjectLiveData: LiveData<Resource<String>> get() = _detailObjectLiveData

    private val sharedPreferences: SharedPreferences? by lazy {
        context?.getSharedPreferences("app", Context.MODE_PRIVATE)
    }

    val detailsEnteredAreCorrect: Flow<Boolean> = combine(
        _userName,
        _emailAddress, _webLink, _instagramLink
    ) { userName, emailAddress, webLink, instagramLink ->
        val isUserNameEntered = userName.isNotEmpty()
        val isValidEmailAddressEntered = emailAddress.isValidEmail()
        val isWebLink = webLink.isNotEmpty()
        val isInstagramLink = instagramLink.isNotEmpty()
        return@combine (isUserNameEntered and isValidEmailAddressEntered) and (isWebLink or isInstagramLink)
    }

    fun setUserName(userName: String) {
        _userName.value = userName
    }

    fun setEmailAddress(emailAddress: String) {
        _emailAddress.value = emailAddress
    }

    fun setWebLink(webLink: String) {
        _webLink.value = webLink
    }

    fun setCategory(category: String) {
        _categoryName.value = category
    }

    fun setInstagramLink(instagramLink: String) {
        _instagramLink.value = instagramLink
    }

    fun saveData() {
        viewModelScope.launch {
            if (!_emailAddress.value.isValidEmail()) {
                _detailObjectLiveData.postValue(
                    Resource.Error(
                        "Please enter correct email address",
                        emailError = true
                    )
                )
            } else {
                val phoneNumber = sharedPreferences?.getString("phoneNumber", "") ?: ""
                val userId = sharedPreferences?.getString("userId", "") ?: ""
                val detailsObject = DetailObject()
                detailsObject.userId = userId
                detailsObject.phoneNum = phoneNumber
                detailsObject.name = _userName.value
                detailsObject.email = _emailAddress.value
                detailsObject.category = _categoryName.value
                if (_webLink.value.isNotEmpty()) {
                    detailsObject.webLink = _webLink.value
                }
                _detailObjectLiveData.postValue(Resource.Loading)
                profileRepository.saveData(detailsObject).collect { resource ->
                    _detailObjectLiveData.postValue(resource)
                }
            }
        }
    }
}