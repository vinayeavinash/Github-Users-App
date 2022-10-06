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

@HiltViewModel // this is the different between other class
class HomeViewModel @Inject constructor( // @ViewModelInject is deprecated for latest version
    private val repository: MainRepository,
    private val dispachers: DispacherProvider
) : ViewModel() {

    private val _usersLiveData = MutableLiveData<List<User>>()
    val usersLiveData: LiveData<List<User>> = _usersLiveData

    private val _usersEvent = MutableStateFlow<ResourceEvent>(ResourceEvent.Empty)
    val usersStateFlow: StateFlow<ResourceEvent> = _usersEvent

    // get  users  data
    fun getUsers() {
        viewModelScope.launch(dispachers.default) {
            _usersEvent.value = ResourceEvent.Loading

            when (val usersResponse = repository.getUsers()) {
                is Resource.Error -> _usersEvent.value =
                    ResourceEvent.Failure(usersResponse.message!!)
                is Resource.Success -> {
                    _usersEvent.value = ResourceEvent.Success(usersResponse.data!!, null)

                    withContext(dispachers.main) {
                        _usersLiveData.value = usersResponse.data
                    }
                }
            }
        }
    }
}