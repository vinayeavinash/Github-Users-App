package com.vinaye.githubuserapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vinaye.githubuserapplication.data.model.User
import com.vinaye.githubuserapplication.repository.MainRepository
import com.vinaye.githubuserapplication.util.DispacherProvider
import com.vinaye.githubuserapplication.util.Resource
import com.vinaye.githubuserapplication.util.ResourceEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispachers: DispacherProvider
) : ViewModel() {

    private var _state: MutableStateFlow<ResourceEvent> = MutableStateFlow(ResourceEvent.Empty)
    val state: StateFlow<ResourceEvent> = _state

    private var _searchResults: MutableLiveData<List<User>> = MutableLiveData()
    val searchResults: LiveData<List<User>> = _searchResults

    private var _lastQuery: MutableLiveData<String> = MutableLiveData()
    val lastQuery: LiveData<String> = _lastQuery

    // fun last  search query
    fun setLastQuery(newQuery: String) {
        _lastQuery.value = newQuery
    }

    // fun search  users query
    fun searchUsers(query: String) {
        viewModelScope.launch(dispachers.default) {
            _state.value = ResourceEvent.Loading

            when (val response = repository.searchUsers(query)) {
                is Resource.Error -> _state.value = ResourceEvent.Failure(response.message!!)
                is Resource.Success -> {
                    _state.value = ResourceEvent.Success(null, null)

                    withContext(dispachers.main) {
                        _searchResults.value = response.data!!
                    }
                }
            }
        }
    }
}