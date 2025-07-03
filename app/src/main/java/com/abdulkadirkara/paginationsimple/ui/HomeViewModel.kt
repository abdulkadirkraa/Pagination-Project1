package com.abdulkadirkara.paginationsimple.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.abdulkadirkara.paginationsimple.data.network.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    //1
    /*
    private val filterState = MutableStateFlow(FilterOptions())

    val users = filterState.flatMapLatest { filter ->
        repository.getUsers(
            gender = filter.gender,
            nat = filter.nat
        )
    }.cachedIn(viewModelScope)

    fun applyFilter(gender: String?, nat: String?) {
        filterState.value = FilterOptions(gender, nat)
    }
     */
    //2
    // Filtre parametreleri için StateFlow'lar
    // 1) savedStateHandle'den doğru generics ile alıyoruz:
    private val _gender = savedStateHandle.getStateFlow<String?>("gender", null)
    private val _nationalities =
        savedStateHandle.getStateFlow<List<String>>("nat", emptyList())

    /** Public olarak okuma için: */
    val gender: String? get() = _gender.value
    val nationalities: List<String> get() = _nationalities.value

    /** Paging 3 + combine ile gender/nat parametrelerini izle ve repo çağır */
    @OptIn(ExperimentalCoroutinesApi::class)
    val users = combine(_gender, _nationalities) { gender, natList ->
        // API'ye "nat" parametresini virgülle ayrılmış string olarak yolluyoruz
        val nat = if (natList.isNotEmpty()) natList.joinToString(",") else null
        gender to nat
    }.flatMapLatest { (gender, nat) ->
        repository.getUsers(gender, nat)
    }.cachedIn(viewModelScope)

    /** Filtre uygula: SavedStateHandle üzerine yazar, combine akışını tetikler */
    fun applyFilter(gender: String?, natList: List<String>) {
        savedStateHandle["gender"] = gender
        savedStateHandle["nat"] = natList
    }

    fun resetFilter() {
        savedStateHandle["gender"] = null
        savedStateHandle["nat"] = emptyList<String>()
    }
}

data class FilterOptions(
    val gender: String? = null,
    val nat: String? = null
)
