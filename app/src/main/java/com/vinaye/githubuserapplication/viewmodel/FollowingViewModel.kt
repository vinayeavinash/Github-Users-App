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
class FollowingViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispachers: DispacherProvider
) : ViewModel() {

    private val _state: MutableStateFlow<ResourceEvent> = MutableStateFlow(ResourceEvent.Empty)
    val state: StateFlow<ResourceEvent> = _state

    private val _following: MutableLiveData<List<User>> = MutableLiveData()
    val followings: LiveData<List<User>> = _following

    fun getFollowingOfUser(username: String) {
        viewModelScope.launch(dispachers.default) {
            _state.value = ResourceEvent.Loading

            when (val usersResponse = repository.getFollowingOfUser(username)) {
                is Resource.Error -> _state.value =
                    ResourceEvent.Failure(usersResponse.message!!)
                is Resource.Success -> {
                    _state.value = ResourceEvent.Success(null, null)

                    withContext(dispachers.main) {
                        _following.value = usersResponse.data!!
                    }
                }
            }
        }
    }
}