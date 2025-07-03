package com.abdulkadirkara.paginationsimple.data.network

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abdulkadirkara.paginationsimple.data.model.Result
import com.abdulkadirkara.paginationsimple.data.service.ApiService
import com.abdulkadirkara.paginationsimple.util.Constants.PAGE_SIZE

class RandomUserPagingSource(
    private val apiService: ApiService,
    private val gender: String? = null,
    private val nat: String? = null
) : PagingSource<Int, Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        val page = params.key ?: 1
        Log.d("PagingSource", "Loading page=$page gender=$gender nat=$nat")

        return try {
            val response = apiService.getUsers(page, PAGE_SIZE, gender, nat)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.results.isEmpty()) null else page.plus(1)
            )
        } catch (e: Exception) {
            Log.e("PagingSource", "Load error: ${e.message}")
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    /*
    PagingSource sınıfı, verinin nasıl getirileceğini anlatan katmandır.

    load() Fonksiyonu - Asıl Veri Yükleme Burada
    params.key: Şu anda hangi sayfayı istiyoruz? (İlk yüklemede null, biz 1 veriyoruz.)

    getRefreshKey() - Liste Yenilenince Nereden Başlasın?
    Refresh işlemi kullanıcı listeyi yenilediğinde (örneğin pull-to-refresh) PagingSource’un hangi sayfadan başlaması gerektiğini belirler.
    En yakın görünür öğenin bulunduğu sayfayı hesaplayarak, oradan yeniden yükleme yapılması sağlanır.
    anchorPosition: Kullanıcının şu anki scroll ettiği pozisyon.
    closestPageToPosition: Bu pozisyona en yakın sayfayı bulur.
    Ona göre sayfa numarasını tekrar yüklemek için karar veririz.
     */
}