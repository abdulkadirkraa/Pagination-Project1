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
    //a
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

    //b
    // Filtre parametreleri için StateFlow'lar
    // 1) savedStateHandle'den doğru generics ile alıyoruz:
    private val _gender = savedStateHandle.getStateFlow<String?>("gender", null)
    private val _nationalities = savedStateHandle.getStateFlow<List<String>>("nat", emptyList())

    /* Public olarak okuma için: */
    val gender: String? get() = _gender.value
    val nationalities: List<String> get() = _nationalities.value

    /* Paging 3 + combine ile gender/nat parametrelerini izle ve repo çağır */
    @OptIn(ExperimentalCoroutinesApi::class)
    val users = combine(_gender, _nationalities) { gender, natList ->
        gender to natList // iki filtre birleşip Pair olur, flatMapLatest içinde kolayca parçalanır
        //gender to natList → Pair<String?, List<String>> döner. Pair kullanmak burada kolay destructuring (parçalama) için kullanışlıdır.
    }.flatMapLatest { (gender, nat) ->
        //flatMapLatest → İç içe Flow’ların doğru çalışmasını sağlar. Yeni bir filtre uygulandığında önceki isteği iptal eder, yalnızca en sonuncusunu işler.
        //Bu sayede, kullanıcı üst üste filtre seçse bile gereksiz eski istekler gönderilmez.
        repository.getUsers(gender, nat.toNatParam())
    }.cachedIn(viewModelScope)
    /*
    combine(_gender, _nationalities)
    İki StateFlow’u birleştirir, her biri değiştiğinde tetiklenir.
    “Hem cinsiyet hem de milliyet filtresi güncellendiğinde” yeni sorgu atmak için.

    .flatMapLatest { (gender,nat) -> ... }
    İç akış (Flow<PagingData>) üretir; yeni filtre gelince önceki sorguyu iptal eder.
    “Önceki sayfalama akışını” iptal edip, yeni filtre ile baştan sayfalamak için.

    .cachedIn(viewModelScope)
    Akışı viewModelScope’a cache’ler, configuration değişikliğinde (örn. ekran dönüşü) aynı veri kullanılır.
    “Ekran döndükten sonra tekrar yüklemeyi önlemek” ve RecyclerView’ın scroll pozisyonunu korumak için.
     */

    // API'ye "nat" parametresini virgülle ayrılmış string olarak yolluyoruz
    private fun List<String>.toNatParam(): String? = takeIf { it.isNotEmpty() }?.joinToString(",")

    /*
    Filtre uygula: SavedStateHandle üzerine yazar, combine akışını tetikler
    Hem StateFlow değerini günceller, hem de process death sonrası geri yükleme imkânı sağlar.
    Her applyFilter / resetFilter çağrısı combine’ı tetikler ve users akışında yeniden sayfalama başlar. */
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
