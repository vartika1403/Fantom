package com.entertainment.fantom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.entertainment.fantom.DetailObject
import com.entertainment.fantom.data.Resource
import com.entertainment.fantom.data.repository.SearchRepository
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _searchResults = MutableLiveData<Resource<List<DetailObject>>>()
    val searchResults: LiveData<Resource<List<DetailObject>>> get() = _searchResults

    fun loadSearchResultsFromEntityType(entityType: String) {
        viewModelScope.launch {
            try {
                _searchResults.postValue(Resource.Loading)
                searchRepository.loadSearchResult(entityType).collect { resource ->
                    if (resource is Resource.Success<List<DetailObject>>) {
                        _searchResults.postValue(resource)
                    } else if (resource is Resource.Error) {
                        _searchResults.postValue(Resource.Error(resource.error))
                    }
                }
            } catch (e: Exception) {
                _searchResults.postValue(e.message?.let { Resource.Error(it) })
            }
        }
    }
}