package com.abdulkadirkara.paginationsimple.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.abdulkadirkara.paginationsimple.data.model.Result
import com.abdulkadirkara.paginationsimple.data.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _userListFlow = MutableStateFlow<PagingData<Result>>(PagingData.empty())
    val userListFlow: Flow<PagingData<Result>> = _userListFlow

    init {
        viewModelScope.launch {
//            repository.getUsers().collect { pagingData ->
//                _userListFlow.value = pagingData
//            }
//            repository.getUsers().cachedIn(viewModelScope).collectLatest {
//                _userListFlow.value = it
//            }
        }
    }
    val users: Flow<PagingData<Result>> = repository
        .getUsers()
        .cachedIn(viewModelScope)

    fun fetchUsers() {
        viewModelScope.launch {
            repository.getUsers()
                .cachedIn(viewModelScope)
                .collectLatest { _userListFlow.value = it }
        }
    }
}