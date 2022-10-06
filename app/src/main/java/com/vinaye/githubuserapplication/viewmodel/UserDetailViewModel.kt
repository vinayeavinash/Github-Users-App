package com.vinaye.githubuserapplication.viewmodel

import android.util.Log
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
class UserDetailViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispachers: DispacherProvider
) : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val user: LiveData<User> = _currentUser

    private val _state = MutableStateFlow<ResourceEvent>(ResourceEvent.Empty)
    val state: StateFlow<ResourceEvent> = _state

    // get  user detail  data
    fun getUser(username: String) {
        viewModelScope.launch(dispachers.default) {
            _state.value = ResourceEvent.Loading

            when (val userDetailResponse = repository.getUserDetail(username)) {
                is Resource.Error -> _state.value =
                    ResourceEvent.Failure(userDetailResponse.message!!)
                is Resource.Success -> {
                    _state.value = ResourceEvent.Success(null, userDetailResponse.data!!)

                    Log.i("fgf", userDetailResponse.data.toString())


                    withContext(dispachers.main) {
                        _currentUser.value = userDetailResponse.data
                    }
                }
            }
        }
    }
}