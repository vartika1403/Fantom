package com.entertainment.fantom.viewmodel

import android.location.Address
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class ProfileViewModel : ViewModel() {
    private val _userName = MutableStateFlow("")
    private val _emailAddress = MutableStateFlow("")
    private val _webLink = MutableStateFlow("")
    private val _instagramLink = MutableStateFlow("")

    val detailsEntered: Flow<Boolean> = combine(_userName,
        _emailAddress, _webLink,_instagramLink) { userName, emailAddress, webLink, instagramLink ->
        val isUserNameEntered = userName.isNotEmpty()
        val isEmailAddressEntered = emailAddress.isNotEmpty()
        val isWebLink = webLink.isNotEmpty()
        val isInstagramLink = instagramLink.isNotEmpty()
        return@combine (isUserNameEntered or isEmailAddressEntered) and (isWebLink or isInstagramLink)
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

    fun setInstagramLink(instagramLink: String) {
        _instagramLink.value = instagramLink
    }
}