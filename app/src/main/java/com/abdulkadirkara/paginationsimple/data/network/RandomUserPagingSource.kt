package com.abdulkadirkara.paginationsimple.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.abdulkadirkara.paginationsimple.data.model.Result
import com.abdulkadirkara.paginationsimple.data.service.ApiService

class RandomUserPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, Result>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Result> {
        val page = params.key ?: 1

        return try {
            val response = apiService.getUsers(page, 10)

            LoadResult.Page(
                data = response.results,
                prevKey = if (page == 1) null else page.minus(1),
                nextKey = if (response.results.isEmpty()) null else page.plus(1)
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Result>): Int? {
        /*
        return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
         */
        return null
    }

}